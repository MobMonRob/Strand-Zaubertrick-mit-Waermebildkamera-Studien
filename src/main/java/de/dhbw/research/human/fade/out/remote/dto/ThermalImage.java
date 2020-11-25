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
    private int[] visualData;

    public ThermalImage(int width, int height, int[] thermalData, int[] visualData) {
        this.width = width;
        this.height = height;
        this.thermalData = thermalData;
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

    public int[] getVisualData() {
        return visualData;
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException {
        aOutputStream.writeShort((short) width);
        aOutputStream.writeShort((short) height);
        for (int thermalValue : thermalData) {
            aOutputStream.writeShort((short) thermalValue);
        }
        for (int visualPixel : visualData) {
            aOutputStream.writeInt(visualPixel);
        }
    }

    private void readObject(ObjectInputStream aInputStream) throws IOException {
        width = aInputStream.readShort();
        height = aInputStream.readShort();

        thermalData = new int[width * height];

        for (int i = 0; i < thermalData.length; i++) {
            thermalData[i] = aInputStream.readShort();
        }

        visualData = new int[width * height];
        for (int i = 0; i < visualData.length; i++) {
            visualData[i] = aInputStream.readInt();
        }
    }
}
