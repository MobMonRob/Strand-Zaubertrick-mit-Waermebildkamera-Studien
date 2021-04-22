import argparse
import timeit

import socket

import cv2
import io
from PIL import Image
import numpy as np
import tensorflow as tf
import neuralgym as ng

from inpaint_model import InpaintCAModel

HOST = '0.0.0.0'
PORT = 4444

parser = argparse.ArgumentParser()
parser.add_argument('--checkpoint_dir', default='./generative_inpainting/model_logs/release_places2_256_deepfill_v2',
                    type=str,
                    help='The directory of tensorflow checkpoint.')


class ThermalImage:
    def __init__(self, mode, image, thermal_mask):
        self.mode = mode
        self.width = image.width
        self.height = image.height
        self.image = image
        self.thermal_mask = thermal_mask

    def should_reset(self):
        return (self.mode & 1) != 0

    def should_capture(self):
        return (self.mode & 2) != 0

    def should_take_photo(self):
        return (self.mode & 4) != 0

    @staticmethod
    def receive(socket_file):
        mode = int.from_bytes(socket_file.read(1), "little")

        img_length = ThermalImage.read_int(socket_file)
        if not img_length > 0:
            return None

        img_data = socket_file.read(img_length)
        img = Image.open(io.BytesIO(img_data))

        thermal_mask = socket_file.read(int((img.width * img.height) / 8))
        thermal_mask = np.frombuffer(thermal_mask, dtype=np.byte, count=-1, offset=0)
        thermal_mask = thermal_mask.reshape(len(thermal_mask), 1)
        byte_to_bool = np.vectorize(ThermalImage.decode_byte, otypes=[np.ndarray])
        thermal_mask = np.hstack(byte_to_bool(thermal_mask).flatten())

        return ThermalImage(mode, img, thermal_mask)

    @staticmethod
    def read_int(file):
        return int.from_bytes(file.read(4), byteorder='big', signed=False)

    @staticmethod
    def decode_byte(byte):
        return [(byte & 128) != 0,
                (byte & 64) != 0,
                (byte & 32) != 0,
                (byte & 16) != 0,
                (byte & 8) != 0,
                (byte & 4) != 0,
                (byte & 2) != 0,
                (byte & 1) != 0]


class ImageProcessor:
    convert_to_cv_mask = np.vectorize(lambda value: np.array([255, 255, 255] if value else [0, 0, 0], dtype=np.uint8),
                                      otypes=[np.ndarray])

    def __init__(self):
        self.frames = 0

        size = (480, 640)
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        self.video = cv2.VideoWriter('/tmp/output.mp4', fourcc, 10, size)

        FLAGS = ng.Config('inpaint.yml')
        # ng.get_gpus(1)
        args = parser.parse_args()

        sess_config = tf.compat.v1.ConfigProto()
        sess_config.gpu_options.allow_growth = True
        self.sess = tf.compat.v1.Session(config=sess_config)

        model = InpaintCAModel()
        self.input_image_ph = tf.compat.v1.placeholder(
            tf.float32, shape=(1, 640, 480 * 2, 3))
        output = model.build_server_graph(FLAGS, self.input_image_ph)
        output = (output + 1.) * 127.5
        output = tf.reverse(output, [-1])
        self.output = tf.saturate_cast(output, tf.uint8)
        vars_list = tf.compat.v1.get_collection(tf.compat.v1.GraphKeys.GLOBAL_VARIABLES)
        assign_ops = []
        for var in vars_list:
            vname = var.name
            from_name = vname
            var_value = tf.contrib.framework.load_variable(
                args.checkpoint_dir, from_name)
            assign_ops.append(tf.compat.v1.assign(var, var_value))
        self.sess.run(assign_ops)

    def on_image_received(self, thermal_image):
        image = cv2.cvtColor(np.array(thermal_image.image), cv2.COLOR_RGB2BGR)
        mask = np.hstack(self.convert_to_cv_mask(thermal_image.thermal_mask).flatten())
        mask = mask.reshape(thermal_image.height, thermal_image.width, 3)

        # mask = cv2.resize(mask, (0,0), fx=0.5, fy=0.5)

        h, w, _ = image.shape
        grid = 8
        image = image[:h // grid * grid, :w // grid * grid, :]
        mask = mask[:h // grid * grid, :w // grid * grid, :]

        image = np.expand_dims(image, 0)
        mask = np.expand_dims(mask, 0)
        input_image = np.concatenate([image, mask], axis=2)

        result = self.sess.run(self.output, feed_dict={self.input_image_ph: input_image})

        if thermal_image.should_take_photo():
            if not cv2.imwrite("../compare/image.jpg".format(self.frames), result[0][:, :, ::-1]):
                raise Exception("Could not write image")



        # print("Rendered frame " + str(self.frames))
        self.frames += 1
        # cv2.imshow("inpainted Image", result[0][:, :, ::-1])
        # cv2.waitKey(1)

        # cv_image = cv2.cvtColor(np.array(thermal_image.image), cv2.COLOR_RGB2BGR)
        #
        # mask = thermal_image.thermal_mask.reshape(thermal_image.height, thermal_image.width)
        # mask = self.convert_to_cv_mask(mask).astype(np.uint8)
        #
        # inpainted_image = cv2.inpaint(cv_image, mask, 5, cv2.INPAINT_NS)
        # cv2.imshow("Image", cv_image)
        # cv2.imshow("Mask", mask)
        # cv2.imshow("inpainted Image", inpainted_image)
        # cv2.waitKey(1)
        #
        # self.video.write(inpainted_image)


def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen()
        conn, addr = s.accept()
        file = conn.makefile(mode="rb")
        image_processor = ImageProcessor()

        with conn:
            print('Connected by', addr)
            while True:
                thermal_image = ThermalImage.receive(file)
                if thermal_image is None:
                    break
                print(timeit.timeit(lambda: image_processor.on_image_received(thermal_image), number=1))


if __name__ == "__main__":
    main()
