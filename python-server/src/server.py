import socket

from thermal_image import ThermalImage
from opencv_image_processor import OpenCvImageProcessor

HOST = '127.0.0.1'
PORT = 4444


def main():
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        s.bind((HOST, PORT))
        s.listen()
        conn, addr = s.accept()
        file = conn.makefile(mode="rb")
        image_processor = OpenCvImageProcessor()

        with conn:
            print('Connected by', addr)
            while True:
                thermal_image = ThermalImage.receive(file)
                if thermal_image is None:
                    break
                image_processor.on_image_received(thermal_image)


if __name__ == "__main__":
    main()
