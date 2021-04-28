import cv2
import socket
import os
import pickle
import sys

if __name__ == '__main__':
    socket_file = sys.argv[1]
    s = socket.socket(socket.AF_UNIX, socket.SOCK_STREAM)
    try:
        os.remove(socket_file)
    except OSError:
        pass
    s.bind(socket_file)
    s.listen(1)
    while True:
        conn, addr = s.accept()
        file = conn.makefile(mode="rb")
        while True:
            data = file.read(921764)
            if not data:
                break
            img = pickle.loads(data)
            cv2.imshow("inpainted Image", img)
            cv2.waitKey(1)
