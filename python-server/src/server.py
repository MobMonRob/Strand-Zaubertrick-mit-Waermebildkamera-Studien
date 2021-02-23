import socket
import cv2
import io
from PIL import Image
import numpy

HOST = '127.0.0.1'
PORT = 4444


class ThermalImage:
    def __init__(self, image, thermal_mask):
        self.width = image.width
        self.height = image.height
        self.image = image
        self.thermal_mask = thermal_mask

    @staticmethod
    def receive(socket_file):
        img_length = ThermalImage.read_int(socket_file)
        if not img_length > 0:
            return None

        img_data = socket_file.read(img_length)
        img = Image.open(io.BytesIO(img_data))

        thermal_mask = socket_file.read(int((img.width * img.height) / 8))
        thermal_mask = numpy.frombuffer(thermal_mask, dtype=numpy.byte, count=-1, offset=0)
        thermal_mask = thermal_mask.reshape(len(thermal_mask), 1)
        byte_to_bool = numpy.vectorize(ThermalImage.decode_byte, otypes=[numpy.ndarray])
        thermal_mask = numpy.hstack(byte_to_bool(thermal_mask).flatten())

        return ThermalImage(img, thermal_mask)

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
    convert_to_cv_mask = numpy.vectorize(lambda value: 255 if value else 0)

    def __init__(self):
        size = (480, 640)
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        self.video = cv2.VideoWriter('/tmp/output.mp4', fourcc, 10, size)

    def on_image_received(self, thermal_image):
        cv_image = cv2.cvtColor(numpy.array(thermal_image.image), cv2.COLOR_RGB2BGR)

        mask = thermal_image.thermal_mask.reshape(thermal_image.height, thermal_image.width)
        mask = self.convert_to_cv_mask(mask).astype(numpy.uint8)

        inpainted_image = cv2.inpaint(cv_image, mask, 5, cv2.INPAINT_NS)
        cv2.imshow("Image", cv_image)
        cv2.imshow("Mask", mask)
        cv2.imshow("inpainted Image", inpainted_image)
        cv2.waitKey(1)

        self.video.write(inpainted_image)


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
                image_processor.on_image_received(thermal_image)


if __name__ == "__main__":
    main()
