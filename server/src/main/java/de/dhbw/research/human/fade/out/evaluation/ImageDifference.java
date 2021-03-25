package de.dhbw.research.human.fade.out.evaluation;

public class ImageDifference {
    private int pixelsOff = 0;
    private int pixelsOff5 = 0;
    private int pixelsOff10 = 0;
    private float differingRGBAll = 0;
    private float differingHSV_H = 0;
    private float differingHSV_S = 0;
    private float differingHSV_V = 0;

    private int pixelCount = 0;

    private boolean initialized = false;

    public ImageDifference() {
    }

    public void update(PixelProperties properties) {
        differingRGBAll += properties.getRGBDifference();
        differingHSV_H += properties.getHSVDifference()[0];
        differingHSV_S += properties.getHSVDifference()[1];
        differingHSV_V += properties.getHSVDifference()[2];

        if (initialized) {
            differingRGBAll /= 2;
            differingHSV_H /= 2;
            differingHSV_S /= 2;
            differingHSV_V /= 2;
        } else {
            initialized = true;
        }

        pixelCount++;
        if (properties.getRGBDifference() != 0) {
            pixelsOff++;
        }
        if (properties.getRGBDifference() >= 0.05) {
            pixelsOff5++;
        }
        if (properties.getRGBDifference() >= 0.1) {
            pixelsOff10++;
        }
    }

    public float getPixelsOffInPercent() {
        return ((float) pixelsOff / pixelCount) * 100;
    }

    public float getPixelsOff5InPercent() {
        return ((float) pixelsOff5 / pixelCount) * 100;
    }

    public float getPixelsOff10InPercent() {
        return ((float) pixelsOff10 / pixelCount) * 100;
    }

    public float getDifferingRGBInPercent() {
        return differingRGBAll * 100f;
    }

    public float getDifferingHSV_HInPercent() {
        return differingHSV_H * 100f;
    }

    public float getDifferingHSV_SInPercent() {
        return differingHSV_S * 100f;
    }

    public float getDifferingHSV_VInPercent() {
        return differingHSV_V * 100f;
    }
}
