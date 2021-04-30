import cv2
import numpy as np

from .image_processor import ImageProcessor
from .thermal_image import ThermalImage
from .util import save_image, VideoCreator


class OpenCvImageProcessor(ImageProcessor):

    def __init__(self):
        self.video_creator = VideoCreator()
        self.recording = False

    def on_image_received(self, thermal_image: ThermalImage):
        cv_image = cv2.cvtColor(np.array(thermal_image.image), cv2.COLOR_RGB2BGR)

        mask = thermal_image.thermal_mask

        mask = np.where(mask, 255, 0)
        mask = mask.reshape(thermal_image.height, thermal_image.width, 1).astype(np.uint8)

        edged = cv2.Canny(mask, 30, 200)
        contours, hierarchy = cv2.findContours(edged, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)
        cv2.drawContours(mask, contours, -1, 255, 40)

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
