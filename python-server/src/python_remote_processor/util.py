import cv2
from datetime import datetime


def save_image(image):
    current_time = get_current_time()
    if not cv2.imwrite("../out/image-{}.jpg".format(current_time), image):
        raise Exception("Could not write image")


def get_current_time():
    now = datetime.now()
    current_time = now.strftime("%d-%m-%Y-%H:%M:%S")
    return current_time


class VideoCreator:

    def __init__(self, size=(480, 640), fps=4):
        self.size = size
        self.fps = fps
        self.fourcc = cv2.VideoWriter_fourcc(*'mp4v')
        self.video = None

    def add_frame(self, frame, new_video: bool):
        if new_video:
            self.video = cv2.VideoWriter('../out/video-{}.mp4'.format(get_current_time()), self.fourcc, self.fps,
                                         self.size)
        self.video.write(frame)

    def save(self):
        self.video.release()
