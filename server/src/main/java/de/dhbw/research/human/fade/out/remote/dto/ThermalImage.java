package de.dhbw.research.human.fade.out.remote.dto;

import android.graphics.Bitmap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

// Size 2.457.784 Byte
// Image dimensions must be multiple of 8
public class ThermalImage {

    private final int width;
    private final int height;

    private int[] thermalData;
    private boolean[] thermalMask;
    private Bitmap bitmap;
    private BufferedImage bufferedImage;

    public ThermalImage(Bitmap bitmap, int[] thermalData) {
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        this.bitmap = bitmap;
        this.thermalData = thermalData;
    }

    public ThermalImage(BufferedImage bufferedImage, int[] thermalData) {
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        this.bufferedImage = bufferedImage;
        this.thermalData = thermalData;
    }

    public ThermalImage(BufferedImage bufferedImage, boolean[] thermalMask) {
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        this.bufferedImage = bufferedImage;
        this.thermalMask = thermalMask;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public boolean[] getThermalMask() {
        return thermalMask;
    }

    public void send(DataOutputStream outputStream) throws IOException {
        sendImage(outputStream);
        sendThermalData(outputStream, 30065);
    }

    private void sendImage(DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        } else {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        }

        outputStream.writeInt(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(outputStream);
    }

    private void sendThermalData(DataOutputStream outputStream, int maskTemperature) throws IOException {
        byte encodedByte = 0;
        int index = 0, count = 0;
        for (int thermalValue : thermalData) {
            index++;
            encodedByte <<= 1;
            encodedByte |= thermalValue > maskTemperature ? 1 : 0;
            if (index == 8) {
                outputStream.writeByte(encodedByte);
                index = 0;
                count++;
            }
        }
        System.out.println(count + " bytes send");
    }

    public static ThermalImage receive(DataInputStream inputStream) throws IOException {
        BufferedImage bufferedImage = receiveImage(inputStream);

        byte[] encodedThermalMask = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() / 8];
        inputStream.readFully(encodedThermalMask);
        boolean[] thermalMask = new boolean[bufferedImage.getWidth() * bufferedImage.getHeight()];
        for (int i = 0; i < encodedThermalMask.length; i++) {
            byte encodedByte = encodedThermalMask[i];
            thermalMask[i * 8] = (encodedByte & 128) != 0;
            thermalMask[i * 8 + 1] = (encodedByte & 64) != 0;
            thermalMask[i * 8 + 2] = (encodedByte & 32) != 0;
            thermalMask[i * 8 + 3] = (encodedByte & 16) != 0;
            thermalMask[i * 8 + 4] = (encodedByte & 8) != 0;
            thermalMask[i * 8 + 5] = (encodedByte & 4) != 0;
            thermalMask[i * 8 + 6] = (encodedByte & 2) != 0;
            thermalMask[i * 8 + 7] = (encodedByte & 1) != 0;
        }

        return new ThermalImage(bufferedImage, thermalMask);
    }

    private static BufferedImage receiveImage(DataInputStream inputStream) throws IOException {
        int length = inputStream.readInt();
        byte[] bytes = new byte[length];
        inputStream.read(bytes);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return ImageIO.read(byteArrayInputStream);
    }

}
