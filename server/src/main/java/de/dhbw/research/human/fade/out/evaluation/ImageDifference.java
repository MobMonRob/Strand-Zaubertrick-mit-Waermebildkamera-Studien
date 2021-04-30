package de.dhbw.research.human.fade.out.evaluation;

public class ImageDifference {
    private int pixelsOff = 0;
    private int pixelsOff5 = 0;
    private int pixelsOff10 = 0;

    private int pixelCount = 0;

    public ImageDifference() {
    }

    public void update(PixelProperties properties) {
        pixelCount++;
        if (properties.getRGBDifference() > 0) {
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
}
