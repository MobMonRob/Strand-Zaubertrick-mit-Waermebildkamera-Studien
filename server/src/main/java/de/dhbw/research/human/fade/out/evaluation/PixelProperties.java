package de.dhbw.research.human.fade.out.evaluation;

public class PixelProperties {
    private static final int MASK_RED = 0x00ff0000;
    private static final int MASK_GREEN = 0x0000ff00;
    private static final int MASK_BLUE = 0x000000ff;

    private int referenceImageR;
    private int referenceImageG;
    private int referenceImageB;

    private int imageR;
    private int imageG;
    private int imageB;

    public PixelProperties(int referenceImagePixel, int imagePixel) {
        referenceImageR = (referenceImagePixel & MASK_RED) >> 16;
        referenceImageG = (referenceImagePixel & MASK_GREEN) >> 8;
        referenceImageB = (referenceImagePixel & MASK_BLUE);

        imageR = (imagePixel & MASK_RED) >> 16;
        imageG = (imagePixel & MASK_GREEN) >> 8;
        imageB = (imagePixel & MASK_BLUE);
    }

    public float getRGBDifference() {
        float rgbDifference = Math.abs((referenceImageR - imageR) / 255f);
        rgbDifference += Math.abs((referenceImageG - imageG) / 255f);
        rgbDifference += Math.abs((referenceImageB - imageB) / 255f);
        rgbDifference /= 3;
        return rgbDifference;
    }
}
