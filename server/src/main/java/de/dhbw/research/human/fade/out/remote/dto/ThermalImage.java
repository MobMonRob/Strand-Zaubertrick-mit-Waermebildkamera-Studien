package de.dhbw.research.human.fade.out.remote.dto;

import android.graphics.Bitmap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

// Size 2.457.784 Byte
public class ThermalImage implements Serializable {

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

    private void writeObject(ObjectOutputStream outputStream) throws IOException {
        outputStream.writeShort((short) width);
        outputStream.writeShort((short) height);
        for (int thermalValue : thermalData) {
            outputStream.writeShort((short) thermalValue);
        }
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } else {
            ImageIO.write(bufferedImage, "jpg", outputStream);
        }
    }

    private void readObject(ObjectInputStream inputStream) throws IOException {
        width = inputStream.readShort();
        height = inputStream.readShort();

        thermalData = new int[width * height];
        for (int i = 0; i < thermalData.length; i++) {
            thermalData[i] = inputStream.readShort();
        }

        bufferedImage = ImageIO.read(inputStream);
    }
}
