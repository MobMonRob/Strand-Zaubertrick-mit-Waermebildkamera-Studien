import io
from PIL import Image
import numpy as np
import numba


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

        thermal_mask = np.asarray(ThermalImage.decode_byte(thermal_mask))
        thermal_mask = np.transpose(thermal_mask)
        thermal_mask = thermal_mask.reshape(thermal_mask.size)

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


RADIUS = 2


@numba.stencil(neighborhood=((-RADIUS, RADIUS), (-RADIUS, RADIUS)))
def __increase_areas(mask):
    for x in range(-RADIUS, RADIUS + 1):
        for y in range(abs(x) - RADIUS, -abs(x) + RADIUS + 1):
            if mask[x, y]:
                return True
    return False


@numba.njit
def __increase_areas_jit(mask):
    return __increase_areas(mask)


def increase_mask(mask):
    mask = np.pad(mask, pad_width=RADIUS, mode='constant', constant_values=False)
    mask = __increase_areas_jit(mask)
    return mask[RADIUS:-RADIUS, RADIUS:-RADIUS]
