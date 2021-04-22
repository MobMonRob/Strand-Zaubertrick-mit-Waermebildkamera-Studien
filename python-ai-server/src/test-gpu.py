from tensorflow.python.client import device_lib

if __name__ == "__main__":
    local_device_protos = device_lib.list_local_devices()
    for x in local_device_protos:
        if x.device_type == 'GPU':
            print("GPU found.:" + x.name)
