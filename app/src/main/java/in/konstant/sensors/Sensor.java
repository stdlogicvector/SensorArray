package in.konstant.sensors;

import java.util.ArrayList;

public abstract class Sensor {

    public static enum TYPE {
        SPATIAL,
        ROTATION,
        DISTANCE,
        COLOR,
        LIGHT,
        HUMIDITY,
        MAGNETIC,
        PRESSURE,
        TEMPERATURE,
        AUDIO,
        GENERIC
    }

    TYPE type;
    ArrayList<Measurement> mMeasurements;

    public Sensor() {
        mMeasurements = new ArrayList<Measurement>();
        type = TYPE.GENERIC;
    }

    abstract String getName();
    abstract String getPart();

    public int getNumberOfMeasurements() {
        return mMeasurements.size();
    }

    public TYPE getType() {
        return this.type;
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
