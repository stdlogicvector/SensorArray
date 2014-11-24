package in.konstant.sensors;

import java.util.ArrayList;

public abstract class Sensor {

    Type type;
    ArrayList<Measurement> mMeasurements;
    ArrayList<SensorValueListener> mValueListeners;

    public Sensor() {
        mValueListeners = new ArrayList<SensorValueListener>();
        mMeasurements = new ArrayList<Measurement>();
        type = Type.GENERIC;
    }

    public boolean registerValueListener(final SensorValueListener listener) {
        return mValueListeners.add(listener);
    }

    public boolean unregisterValueListener(final SensorValueListener listener) {
        return mValueListeners.remove(listener);
    }

    protected void signalSensorValueChanged() {
        for (SensorValueListener listener : mValueListeners) {
            listener.onSensorValueChanged();
        }
    }

    public abstract String getName();

    public abstract String getPart();

    public abstract void activate();

    public abstract void deactivate();

    //    abstract float[] getValue(final int id);

    public int getNumberOfMeasurements() {
        return mMeasurements.size();
    }

    public Measurement getMeasurement(final int id) {
        return mMeasurements.get(id);
    }

    public Type getType() {
        return this.type;
    }

    void setRange(final int measurement, final int range) {
        if (measurement >= 0 && measurement < mMeasurements.size()) {
            mMeasurements.get(measurement).setRange(range);
        }
    }

}
