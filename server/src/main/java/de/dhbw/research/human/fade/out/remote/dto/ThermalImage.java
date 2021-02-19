package de.dhbw.research.human.fade.out.remote.dto;

import android.graphics.Bitmap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

// Size 2.457.784 Byte
public class ThermalImage {

    private int width;
    private int height;
    private int[] thermalData;
    private Bitmap bitmap;
    private BufferedImage bufferedImage;

    public ThermalImage(int width, int height, int[] thermalData, Bitmap bitmap) {
        this.width = width;
        this.height = height;
        this.thermalData = thermalData;
        this.bitmap = bitmap;
    }

    public ThermalImage(int width, int height, int[] thermalData, BufferedImage bufferedImage) {
        this.width = width;
        this.height = height;
        this.thermalData = thermalData;
        this.bufferedImage = bufferedImage;
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

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void send(DataOutputStream outputStream) throws IOException {
        sendImage(outputStream);

        for (int thermalValue : thermalData) {
            outputStream.writeShort((short) thermalValue);
        }
    }

    public void sendImage(DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        } else {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        }

        outputStream.writeInt(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(outputStream);
    }

    public static ThermalImage receive(DataInputStream inputStream) throws IOException {
        BufferedImage bufferedImage = receiveImage(inputStream);

        int[] thermalData = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        for (int i = 0; i < thermalData.length; i++) {
            thermalData[i] = inputStream.readShort();
        }

        return new ThermalImage(bufferedImage.getWidth(), bufferedImage.getHeight(), thermalData, bufferedImage);
    }

    public static BufferedImage receiveImage(DataInputStream inputStream) throws IOException {
        int length = inputStream.readInt();
        byte[] bytes = new byte[length];
        inputStream.read(bytes);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return ImageIO.read(byteArrayInputStream);
    }

}
