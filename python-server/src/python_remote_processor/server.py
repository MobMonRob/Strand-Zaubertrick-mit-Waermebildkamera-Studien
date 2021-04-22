import socket

from .thermal_image import ThermalImage
from .image_processor import ImageProcessor


class Server:

    def __init__(self, image_processor: ImageProcessor, host="127.0.0.1", port=4444):
        self.image_processor = image_processor
        self.host = host
        self.port = port

    def run(self):
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
            s.bind((self.host, self.port))
            s.listen()
            print("Server running at {}:{}".format(self.host, self.port))
            while True:
                conn, addr = s.accept()
                file = conn.makefile(mode="rb")

                with conn:
                    print('Connected by', addr)
                    while True:
                        thermal_image = ThermalImage.receive(file)
                        if thermal_image is None:
                            break
                        self.image_processor.on_image_received(thermal_image)
