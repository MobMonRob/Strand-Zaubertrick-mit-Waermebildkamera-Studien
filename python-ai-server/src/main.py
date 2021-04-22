import argparse

from python_remote_processor.server import Server
from ai_image_processor import AIImageProcessor

parser = argparse.ArgumentParser()
parser.add_argument('--checkpoint_dir', default='../model_logs/release_places2_256_deepfill_v2',
                    type=str,
                    help='The directory of tensorflow checkpoint.')

if __name__ == '__main__':
    args = parser.parse_args()
    Server(AIImageProcessor(args.checkpoint_dir)).run()
