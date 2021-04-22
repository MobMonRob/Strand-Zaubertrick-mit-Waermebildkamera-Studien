from .thermal_image import ThermalImage
from abc import ABC, abstractmethod


class ImageProcessor(ABC):

    @abstractmethod
    def on_image_received(self, thermal_image: ThermalImage):
        pass

    @abstractmethod
    def on_connection_closed(self):
        pass
