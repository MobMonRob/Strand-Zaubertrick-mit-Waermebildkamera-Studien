from setuptools import setup

setup(
    name='python_remote_processor',
    version='0.1',
    description='',
    package_dir={'': 'src'},
    packages=['python_remote_processor'],
    install_requires=['llvmlite',
                      'numba',
                      'numpy',
                      'opencv-contrib-python',
                      'Pillow']
)
