package de.dhbw.research.human.fade.out.remote.thermalImage;

public class TemperatureRange {

    private final int min;
    private final int max;

    public TemperatureRange(int min, int max) {
        if (min > max) {
            throw new RuntimeException("Minimum must be lower than maximum");
        }
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public boolean inRange(int value) {
        return value >= min && value <= max;
    }
}
