#!/bin/bash

function cleanup {
  echo "Stopping preview"
  kill $PREVIEW_PID
}

trap cleanup EXIT

python src/read_images.py "/tmp/image.sock" &
PREVIEW_PID=$!
docker run --gpus all --name python-ai-server -v "/tmp/out:/out" -v "/tmp/image.sock:/image.sock" --rm -it -p 4444:4444 python-ai-server:latest python main.py --use-gpu true --preview_socket_name /image.sock
