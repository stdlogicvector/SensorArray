package in.konstant.sensors;

import java.util.ArrayList;

public abstract class Sensor {

    final int id;
    Type type;
    ArrayList<Measurement> measurements;

    boolean active = false;
    boolean measuring = false;

    public Sensor(final int id, final Type type) {
        this.id = id;
        this.type = type;

        measurements = new ArrayList<Measurement>();
    }

    public abstract String getName();

    public abstract String getPart();

    public abstract void activate();

    public abstract void deactivate();

    public boolean isActive() {
        return active;
    }

    public int getNumberOfMeasurements() {
        return measurements.size();
    }

    public Measurement getMeasurement(final int id) {
        return measurements.get(id);
    }

    public Type getType() {
        return this.type;
    }

    void setRange(final int measurement, final int range) {
        if (measurement >= 0 && measurement < measurements.size()) {
            measurements.get(measurement).setRange(range);
        } else
            throw new IndexOutOfBoundsException("Measurement ID not available.");
    }

//    public abstract float[] getValue(final int measurementId);

    public abstract boolean startMeasuring(final int measurementId, final int interval);

    public abstract void stopMeasuring();

    public boolean isMeasuring() {
        return measuring;
    }
}
