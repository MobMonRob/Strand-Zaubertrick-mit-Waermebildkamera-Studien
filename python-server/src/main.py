from python_remote_processor.server import Server
from python_remote_processor.opencv_image_processor import OpenCvImageProcessor

if __name__ == "__main__":
    Server(OpenCvImageProcessor()).run()
