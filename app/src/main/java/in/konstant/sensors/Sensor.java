package in.konstant.sensors;

import java.util.ArrayList;

public abstract class Sensor {
    ArrayList<Measurement> mMeasurements;

    public Sensor() {

    }

    abstract String getName();
    abstract String getPart();

    public int getNumberOfMeasurements() {
        return mMeasurements.size();
    }

    abstract void activate();
    abstract void deactivate();

    void setRange(int measurement, int range) {
        if (measurement >= 0 && measurement < mMeasurements.size()) {
            mMeasurements.get(measurement).setRange(range);
        }
    }

    abstract Measurement getMeasurement(int id);

    abstract float[] getValue(int id);
}
