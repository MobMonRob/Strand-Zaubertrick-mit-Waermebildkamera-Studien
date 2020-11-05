package de.dhbw.research.human.fade.out.remote.dto;

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
}
