package in.konstant.sensors;

import java.util.ArrayList;

public abstract class Sensor {

    public static enum TYPE {
        GENERIC,
        ADC,
        AUDIO,
        COMPASS,
        COLOR,
        DISTANCE,
        GAS,
        HUMIDITY,
        LIGHT,
        MAGNETIC,
        POLARISATION,
        PRESSURE,
        ROTATION,
        RADIATION,
        SPATIAL,
        TEMPERATURE,
        THERMAL
    }

    TYPE type;
    ArrayList<Measurement> mMeasurements;

    public Sensor() {
        mMeasurements = new ArrayList<Measurement>();
        type = TYPE.GENERIC;
    }

    public abstract String getName();
    public abstract String getPart();

    public int getNumberOfMeasurements() {
        return mMeasurements.size();
    }

    public TYPE getType() {
        return this.type;
    }

    public abstract void activate();
    public abstract void deactivate();

    void setRange(int measurement, int range) {
        if (measurement >= 0 && measurement < mMeasurements.size()) {
            mMeasurements.get(measurement).setRange(range);
        }
    }

    abstract Measurement getMeasurement(int id);

    abstract float[] getValue(int id);
}
