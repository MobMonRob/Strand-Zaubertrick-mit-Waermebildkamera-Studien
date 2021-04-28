FROM tensorflow/tensorflow:1.15.5-gpu-py3

ENV TZ=Europe/Berlin
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN apt-get update \
 && apt-get install -y git \
 && apt-get install -y python3-opencv \
 && pip install PyYAML Pillow opencv-contrib-python \
 && pip install git+https://github.com/JiahuiYu/neuralgym \
 && pip install git+https://github.com/MobMonRob/Strand-Zaubertrick-mit-Waermebildkamera-Studien#subdirectory=python-server

COPY neuralgym-patch/gpus.py /usr/local/lib/python3.6/dist-packages/neuralgym/utils/gpus.py
COPY model_logs /model_logs
COPY src /server

WORKDIR /server

