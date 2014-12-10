package in.konstant.sensors;

import java.util.ArrayList;

public abstract class SensorDevice {

    protected String mAddress;
    protected String mDeviceName;
    protected boolean mConnected = false;

    protected ArrayList<Sensor> mSensors;
    protected ArrayList<SensorDeviceEventListener> mStateListeners;
    protected ArrayList<SensorValueListener> mValueListeners;

    public SensorDevice(final String address) {
        this.mAddress = address;
        mSensors = new ArrayList<Sensor>();
        mStateListeners = new ArrayList<SensorDeviceEventListener>();
        mValueListeners = new ArrayList<SensorValueListener>();
    }

    public boolean registerStateListener(final SensorDeviceEventListener listener) {
        return mStateListeners.add(listener);
    }

    public boolean unregisterStateListener(final SensorDeviceEventListener listener) {
        return mStateListeners.remove(listener);
    }

    protected void notifySensorDeviceEvent(final int event) {
        for (SensorDeviceEventListener listener : mStateListeners) {
            listener.onSensorDeviceEvent(this, event);
        }
    }

    public boolean registerValueListener(final SensorValueListener listener) {
        return mValueListeners.add(listener);
    }

    public boolean unregisterValueListener(final SensorValueListener listener) {
        return mValueListeners.remove(listener);
    }

    protected void notifySensorValueEvent(final Sensor sensor, final int measurementId, final float[] value) {
        for (SensorValueListener listener : mValueListeners) {
            listener.onSensorValueChanged(sensor, measurementId, value);
        }
    }

    public abstract boolean initialize();
    public abstract void connect();
    public abstract void disconnect();
    public abstract boolean quit();

    public boolean isConnected() {
        return mConnected;
    }

    abstract String getBluetoothName();

    public String getBluetoothAddress() {
        return mAddress;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public void setDeviceName(final String name) {
        this.mDeviceName = name;
    }

    public int getNumberOfSensors() {
        return mSensors.size();
    }

    public Sensor getSensor(final int id) {
        return mSensors.get(id);
    }

    public abstract boolean getMeasurementValue(final int sensorId, final int measurementId);
    public abstract boolean getMeasurementValue(final int sensorId, final int measurementId, final int interval);

    public abstract void stopMeasuring();
}
