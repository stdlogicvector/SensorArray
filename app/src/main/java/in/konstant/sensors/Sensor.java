package in.konstant.sensors;

import java.util.ArrayList;

public abstract class Sensor {

    Type type;
    ArrayList<Measurement> mMeasurements;

    public Sensor() {
        mMeasurements = new ArrayList<Measurement>();
        type = Type.GENERIC;
    }

    public abstract String getName();
    public abstract String getPart();

    public int getNumberOfMeasurements() {
        return mMeasurements.size();
    }

    public Type getType() {
        return this.type;
    }

    public abstract void activate();
    public abstract void deactivate();

    void setRange(final int measurement, final int range) {
        if (measurement >= 0 && measurement < mMeasurements.size()) {
            mMeasurements.get(measurement).setRange(range);
        }
    }

    abstract Measurement getMeasurement(final int id);

    abstract float[] getValue(final int id);
}
