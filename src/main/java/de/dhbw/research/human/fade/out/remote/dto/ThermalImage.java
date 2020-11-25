package de.dhbw.research.human.fade.out.remote.dto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Size 2.457.784 Byte
public class ThermalImage implements Serializable {

    private int width;
    private int height;
    private int[] thermalData;
    private int length;
    private byte[] visualData;

    public ThermalImage(int width, int height, int[] thermalData, byte[] visualData) {
        this.width = width;
        this.height = height;
        this.thermalData = thermalData;
        this.length = visualData.length;
        this.visualData = visualData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int[] getThermalData() {
        return thermalData;
    }

    public byte[] getVisualData() {
        return visualData;
    }

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeShort((short) width);
        outputStream.writeShort((short) height);
        for (int thermalValue : thermalData) {
            outputStream.writeShort((short) thermalValue);
        }
        outputStream.writeInt(length);
        outputStream.write(visualData);
    }

    private void readObject(ObjectInputStream inputStream) throws IOException {
        width = inputStream.readShort();
        height = inputStream.readShort();

        thermalData = new int[width * height];
        for (int i = 0; i < thermalData.length; i++) {
            thermalData[i] = inputStream.readShort();
        }

        length = inputStream.readInt();

        visualData = new byte[length];
        for (int i = 0; i < length; i++) {
            visualData[i] = inputStream.readByte();
        }
    }
}
