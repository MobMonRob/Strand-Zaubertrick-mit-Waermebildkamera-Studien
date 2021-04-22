from server import Server
from opencv_image_processor import OpenCvImageProcessor

if __name__ == "__main__":
    Server(OpenCvImageProcessor()).run()
