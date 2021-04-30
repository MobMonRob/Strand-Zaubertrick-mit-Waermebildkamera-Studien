import cv2
import numpy as np
import tensorflow as tf
import neuralgym as ng
from PIL import Image
import socket
import pickle
import timeit

from generative_inpainting.inpaint_model import InpaintCAModel
from python_remote_processor.thermal_image import ThermalImage, increase_mask
from python_remote_processor.image_processor import ImageProcessor
from python_remote_processor.util import VideoCreator, save_image

MEASURE_PROCESSING_TIME = True


class AIImageProcessor(ImageProcessor):

    def __init__(self, checkpoint_dir, use_gpu=False, preview_socket_name=None):
        self.video_creator = VideoCreator()
        if preview_socket_name is not None:
            self.preview_socket = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
            self.preview_socket.connect(preview_socket_name)
        self.recording = False

        FLAGS = ng.Config('generative_inpainting/inpaint.yml')
        if use_gpu:
            ng.get_gpus(1)

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
                checkpoint_dir, from_name)
            assign_ops.append(tf.compat.v1.assign(var, var_value))
        self.sess.run(assign_ops)

        self.on_image_received(
            ThermalImage(0, Image.new('RGB', (480, 640), color='black'), np.full((480 * 640), False)))

    def on_image_received(self, thermal_image):
        if MEASURE_PROCESSING_TIME:
            print(timeit.timeit(lambda: self.on_image_received_real(thermal_image), number=1))
        else:
            self.on_image_received_real(thermal_image)

    def on_image_received_real(self, thermal_image):
        image = cv2.cvtColor(np.array(thermal_image.image), cv2.COLOR_RGB2BGR)

        mask = thermal_image.thermal_mask

        mask = mask.reshape(thermal_image.height, thermal_image.width)
        mask = increase_mask(mask)

        mask = np.where(mask, 255, 0).reshape(1, mask.size)
        mask = np.concatenate((mask, mask, mask), axis=0)
        mask = np.transpose(mask)

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
        result = result[0][:, :, ::-1]

        if hasattr(self, 'preview_socket'):
            self.preview_socket.send(pickle.dumps(result))
        else:
            cv2.imshow("inpainted Image", result)
            cv2.waitKey(1)

        if thermal_image.should_take_photo():
            save_image(result)

        if thermal_image.should_capture():
            self.video_creator.add_frame(result, not self.recording)
            if not self.recording: self.recording = True
        if not thermal_image.should_capture() and self.recording:
            self.video_creator.save()
            self.recording = False

    def on_connection_closed(self):
        self.video_creator.save()
