package de.dhbw.research.human.fade.out.evaluation;

import java.awt.*;

public class PixelProperties {
    private static final int MASK_RED = 0x00ff0000;
    private static final int MASK_GREEN = 0x0000ff00;
    private static final int MASK_BLUE = 0x000000ff;
    private static final int MASK_ALL = 0x00ffffff;

    private int referenceImageR;
    private int referenceImageG;
    private int referenceImageB;

    private int imageR;
    private int imageG;
    private int imageB;

    private boolean masked;

    public PixelProperties(int referenceImagePixel, int maskPixel, int imagePixel) {
        referenceImageR = (referenceImagePixel & MASK_RED) >> 16;
        referenceImageG = (referenceImagePixel & MASK_GREEN) >> 8;
        referenceImageB = (referenceImagePixel & MASK_BLUE);

        imageR = (imagePixel & MASK_RED) >> 16;
        imageG = (imagePixel & MASK_GREEN) >> 8;
        imageB = (imagePixel & MASK_BLUE);

        masked = (maskPixel & MASK_ALL) > 0;
    }

    public float getRGBDifference() {
        float rgbDifference = Math.abs((referenceImageR - imageR) / 255f);
        rgbDifference += Math.abs((referenceImageG - imageG) / 255f);
        rgbDifference += Math.abs((referenceImageB - imageB) / 255f);
        rgbDifference /= 3;
        return rgbDifference;
    }

    public float[] getHSVDifference() {
        float[] hsvReference = new float[3];
        Color.RGBtoHSB(referenceImageR, referenceImageG, referenceImageB, hsvReference);

        float[] hsvImage = new float[3];
        Color.RGBtoHSB(imageR, imageG, imageB, hsvImage);

        float[] hsvDifference = new float[3];
        hsvDifference[0] = Math.abs(hsvReference[0] - hsvImage[0]);
        hsvDifference[1] = Math.abs(hsvReference[1] - hsvImage[1]);
        hsvDifference[2] = Math.abs(hsvReference[2] - hsvImage[2]);
        return hsvDifference;
    }

    public int getReferenceImageR() {
        return referenceImageR;
    }

    public int getReferenceImageG() {
        return referenceImageG;
    }

    public int getReferenceImageB() {
        return referenceImageB;
    }

    public int getImageR() {
        return imageR;
    }

    public int getImageG() {
        return imageG;
    }

    public int getImageB() {
        return imageB;
    }

    public boolean isMasked() {
        return masked;
    }
}
