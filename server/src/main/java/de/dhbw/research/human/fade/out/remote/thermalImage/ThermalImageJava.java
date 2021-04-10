package de.dhbw.research.human.fade.out.remote.thermalImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

// Size 2.457.784 Byte
// Image dimensions must be multiple of 8
public class ThermalImageJava extends ThermalImage {

    private final BufferedImage bufferedImage;

    public ThermalImageJava(BufferedImage bufferedImage, int[] thermalData, TemperatureRange range, byte mode) {
        this(bufferedImage, ThermalDataEncoder.encode(thermalData, range), mode);
    }

    public ThermalImageJava(BufferedImage bufferedImage, byte[] thermalMask, byte mode) {
        this.width = bufferedImage.getWidth();
        this.height = bufferedImage.getHeight();
        this.mode = mode;

        this.bufferedImage = bufferedImage;
        this.thermalMask = thermalMask;
    }

    void sendImage(DataOutputStream outputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        outputStream.writeInt(byteArrayOutputStream.size());
        byteArrayOutputStream.writeTo(outputStream);
    }

    public static ThermalImageJava receive(DataInputStream inputStream) throws IOException {
        byte mode = inputStream.readByte();

        BufferedImage bufferedImage = receiveImage(inputStream);

        byte[] encodedThermalMask = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() / 8];
        inputStream.readFully(encodedThermalMask);

        return new ThermalImageJava(bufferedImage, encodedThermalMask, mode);
    }

    private static BufferedImage receiveImage(DataInputStream inputStream) throws IOException {
        int length = inputStream.readInt();
        byte[] bytes = new byte[length];
        inputStream.readFully(bytes);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        return ImageIO.read(byteArrayInputStream);
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public boolean[] getBooleanThermalMask() {
        boolean[] boolMask = new boolean[bufferedImage.getWidth() * bufferedImage.getHeight()];
        for (int i = 0; i < thermalMask.length; i++) {
            byte encodedByte = thermalMask[i];
            boolMask[i * 8] = (encodedByte & 128) != 0;
            boolMask[i * 8 + 1] = (encodedByte & 64) != 0;
            boolMask[i * 8 + 2] = (encodedByte & 32) != 0;
            boolMask[i * 8 + 3] = (encodedByte & 16) != 0;
            boolMask[i * 8 + 4] = (encodedByte & 8) != 0;
            boolMask[i * 8 + 5] = (encodedByte & 4) != 0;
            boolMask[i * 8 + 6] = (encodedByte & 2) != 0;
            boolMask[i * 8 + 7] = (encodedByte & 1) != 0;
        }
        return boolMask;
    }
}
