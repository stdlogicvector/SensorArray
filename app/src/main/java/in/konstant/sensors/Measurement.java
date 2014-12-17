package in.konstant.sensors;

public class Measurement {
    private final String name;      // Name of the Measurement (e.g. "Acceleration")
    private final MeasurementType type;
    private final int size;         // Number of Values (e.g. 3 (x,y,z))
    private final Range ranges[];   // Ranges of the Measurement (e.g. -8g to +8g)
    private final Unit unit;        // Unit of the Measurement (e.g. m/s²)
    private final int minDelay;     // Duration of one Measurement in Milliseconds

    private int currentRange;       // Currently active range

    public Measurement(final String name, final MeasurementType type, final int size, final int minDelay, final Range[] ranges, final Unit unit) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.ranges = ranges;
        this.unit = unit;
        this. minDelay = minDelay;

        this.currentRange = 0;
    }

    public void setRange(final int range) {
        if (range >= 0 && range < ranges.length) {
            currentRange = range;
        } else
            throw new IndexOutOfBoundsException("Range ID not available.");
    }

    public String getName() {
        return name;
    }

    public MeasurementType getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public Range getRange(final int id) {
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
