package de.dhbw.research.human.fade.out.remote.dto;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

// Size 2.457.784 Byte
public class ThermalImage implements Transmissible {

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

    public void send(BufferedOutputStream outputStream) {
        ByteBuffer buffer =  ByteBuffer.allocate(4 + thermalData.length * 2 + visualData.length * 4);
        buffer.putShort((short) width);
        buffer.putShort((short) height);
        for (int thermalValue : thermalData) {
            buffer.putShort((short)thermalValue);
        }
        for (int visualPixel : visualData) {
            buffer.putInt(visualPixel);
        }

        try {
            outputStream.write(buffer.array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
