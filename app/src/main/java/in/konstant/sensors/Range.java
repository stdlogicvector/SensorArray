package in.konstant.sensors;

public class Range {
    private final float min;
    private final float max;
    private final int digits;

    public Range(final float min, final float max, final int digits) {
        this.min = min;
        this.max = max;
        this.digits = digits;
    }

    public float getMin() {
        return this.min;
    }

    public float getMax() {
        return this.max;
    }

    public int getDigits() {
        return this.digits;
    }

    public String toString() {
       return String.format("%." + digits + "f - %." + digits + "f");
   }

}
