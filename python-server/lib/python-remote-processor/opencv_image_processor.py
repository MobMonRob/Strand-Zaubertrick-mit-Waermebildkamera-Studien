import cv2
import numpy

from image_processor import ImageProcessor
from thermal_image import ThermalImage


class OpenCvImageProcessor(ImageProcessor):
    convert_to_cv_mask = numpy.vectorize(lambda value: 255 if value else 0)

    def __init__(self):
        size = (480, 640)
        fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        self.video = cv2.VideoWriter('/tmp/output.mp4', fourcc, 10, size)

    def on_image_received(self, thermal_image: ThermalImage):
        cv_image = cv2.cvtColor(numpy.array(thermal_image.image), cv2.COLOR_RGB2BGR)

        mask = thermal_image.thermal_mask.reshape(thermal_image.height, thermal_image.width)
        mask = self.convert_to_cv_mask(mask).astype(numpy.uint8)

        inpainted_image = cv2.inpaint(cv_image, mask, 5, cv2.INPAINT_NS)
        cv2.imshow("Image", cv_image)
        cv2.imshow("Mask", mask)
        cv2.imshow("inpainted Image", inpainted_image)
        cv2.waitKey(1)

        self.video.write(inpainted_image)
