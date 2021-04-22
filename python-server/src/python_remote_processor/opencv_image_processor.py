import cv2
import numpy

from .image_processor import ImageProcessor
from .thermal_image import ThermalImage, increase_mask
from .util import save_image, VideoCreator


class OpenCvImageProcessor(ImageProcessor):
    convert_to_cv_mask = numpy.vectorize(lambda value: 255 if value else 0)

    def __init__(self):
        self.video_creator = VideoCreator()
        self.recording = False

    def on_image_received(self, thermal_image: ThermalImage):
        cv_image = cv2.cvtColor(numpy.array(thermal_image.image), cv2.COLOR_RGB2BGR)

        mask = thermal_image.thermal_mask
        mask = mask.reshape(thermal_image.height, thermal_image.width)
        mask = increase_mask(mask)
        mask = self.convert_to_cv_mask(mask).astype(numpy.uint8)

        inpainted_image = cv2.inpaint(cv_image, mask, 1, cv2.INPAINT_NS)
        cv2.imshow("Image", cv_image)
        cv2.imshow("Mask", mask)
        cv2.imshow("inpainted Image", inpainted_image)
        cv2.waitKey(1)

        if thermal_image.should_take_photo():
            save_image(inpainted_image)

        if thermal_image.should_capture():
            self.video_creator.add_frame(inpainted_image, not self.recording)
            if not self.recording: self.recording = True
        if not thermal_image.should_capture() and self.recording:
            self.video_creator.save()
            self.recording = False

    def on_connection_closed(self):
        self.video_creator.save()
