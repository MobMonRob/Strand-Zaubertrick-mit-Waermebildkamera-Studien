package de.dhbw.research.human.fade.out.remote.thermalImage;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class ThermalImage {

    public static final byte MODE_NONE = 0;
    public static final byte MODE_RESET = 1;
    public static final byte MODE_CAPTURE = 2;
    public static final byte MODE_PHOTO = 4;

    int width;
    int height;
    byte mode;
    byte[] thermalMask;

    public void send(DataOutputStream outputStream) throws IOException {
        outputStream.writeByte(mode);
        sendImage(outputStream);
        outputStream.write(thermalMask);
    }

    abstract void sendImage(DataOutputStream outputStream) throws IOException;

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getThermalMask() {
        return thermalMask;
    }

    public boolean shouldReset() {
        return (mode & MODE_RESET) != 0;
    }

    public boolean shouldCapture() {
        return (mode & MODE_CAPTURE) != 0;
    }

    public boolean shouldTakePhoto() {
        return (mode & MODE_PHOTO) != 0;
    }
}
