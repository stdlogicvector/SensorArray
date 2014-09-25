package in.konstant.sensors;

public class Measurement {
    private final String name;      // Name of the Measurement (e.g. "Acceleration")
    private final int size;         // Number of Values (e.g. 3 (x,y,z))
    private final Range ranges[];   // Ranges of the Measurement (e.g. -8g to +8g)
    private final Unit unit;        // Unit of the Measurement (e.g. m/s²)
    private final int minDelay;     // Duration of one Measurement in Milliseconds

    private int currentRange;       // Currently active range

    public Measurement(String name, int size, Range[] ranges, Unit unit, int minDelay) {
        this.name = name;
        this.size = size;
        this.ranges = ranges;
        this.unit = unit;
        this. minDelay = minDelay;

        this.currentRange = 0;
    }

    public void setRange(int range) {
        if (range >= 0 && range < ranges.length) {
            currentRange = range;
        }
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public Range getRange(int id) {
        return ranges[id];
    }

    public Range[] getRanges() {
        return ranges;
    }

    public Unit getUnit() {
        return unit;
    }

    public int getMinDelay() {
        return minDelay;
    }

    public Range getCurrentRange() {
        return ranges[currentRange];
    }

    public int getCurrentRangeId() {
        return currentRange;
    }
}
