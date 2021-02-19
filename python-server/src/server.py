import socket
import cv2
import io
from PIL import Image
import numpy

HOST = '127.0.0.1'
PORT = 4444


class ThermalImage:
    def __init__(self, image, thermal_data):
        self.width = image.width
        self.height = image.height
        self.image = image
        self.thermal_data = thermal_data

    @staticmethod
    def receive(socket_file):
        img_length = ThermalImage.read_int(socket_file)
        if not img_length > 0:
            return None

        img_data = socket_file.read(img_length)
        img = Image.open(io.BytesIO(img_data))
        thermal_data = socket_file.read(img.width * img.height * 2)

        return ThermalImage(img, thermal_data)

    @staticmethod
    def read_int(file):
        return int.from_bytes(file.read(4), byteorder='big', signed=False)


class ImageProcessor:
    def __init__(self):
        size = (480, 640)
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        self.video = cv2.VideoWriter('/tmp/output.mp4', fourcc, 10, size)

    @staticmethod
    def thermal_data_to_mask(value):
        return 255 if value > 0 else 0

    def on_image_received(self, thermal_image):
        cv_image = cv2.cvtColor(numpy.array(thermal_image.image), cv2.COLOR_RGB2BGR)

        thermal_data_to_mask_vectorized = numpy.vectorize(self.thermal_data_to_mask)
        mask = numpy.frombuffer(thermal_image.thermal_data, dtype='>h', count=-1, offset=0)
        mask = mask.reshape(thermal_image.height, thermal_image.width)
        mask = thermal_data_to_mask_vectorized(mask).astype(numpy.uint8)

        cv_mask = cv2.cvtColor(mask, cv2.COLOR_GRAY2BGR)
        # cv2.imshow("test", cv_mask)
        # cv2.waitKey()

        self.video.write(cv_mask)


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
