import argparse

from python_remote_processor.server import Server
from ai_image_processor import AIImageProcessor

parser = argparse.ArgumentParser()
parser.add_argument('--checkpoint-dir', default='../model_logs/release_places2_256_deepfill_v2',
                    type=str,
                    help='The directory of tensorflow checkpoint.')
parser.add_argument('--preview_socket_name',
                    type=str,
                    help='File to use for socket-connection to enable preview in docker environment')
parser.add_argument('--use-gpu',
                    type=bool,
                    help='Activate gpu usage')

if __name__ == '__main__':
    args = parser.parse_args()
    Server(AIImageProcessor(args.checkpoint_dir, args.use_gpu, args.preview_socket_name), host='0.0.0.0').run()
