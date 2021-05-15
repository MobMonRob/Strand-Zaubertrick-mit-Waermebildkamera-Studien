# Python-Server

This server-implementation was designed as python-package containing all the required code to receive frames from the client.
To implement a new server you have to create a custom `ImageProcessor` implementing the corresponding interface.

It also provides a reference implementation using opencv which works just like the java-version.

## How to use

### Library

To use this project as package to create a custom server-implementation simply install it via `pip`:

```bash
pip install git+https://github.com/MobMonRob/Strand-Zaubertrick-mit-Waermebildkamera-Studien#subdirectory=python-server
```

### Run

To run the included opencv-implementation you need to install all packages from `requirements.txt`
```bash
# optional
python3 -m venv venv
source venv/bin/activate

pip install -r requirements.txt 
```

Then you can start the server by calling:
```bash
python3 src/main.py
```

This will start a new server on port 4444.