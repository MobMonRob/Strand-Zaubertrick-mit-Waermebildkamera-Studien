import io
from PIL import Image
import numpy


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
        mode = socket_file.read(1)

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