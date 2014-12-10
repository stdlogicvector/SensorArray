package in.konstant.sensors;

import java.util.ArrayList;

public abstract class SensorDevice {

    protected String address;
    protected String deviceName;
    protected boolean connected = false;

    protected ArrayList<Sensor> sensors;
    protected ArrayList<SensorDeviceEventListener> stateListeners;
    protected ArrayList<SensorValueListener> valueListeners;

    public SensorDevice(final String address) {
        this.address = address;
        sensors = new ArrayList<Sensor>();
        stateListeners = new ArrayList<SensorDeviceEventListener>();
        valueListeners = new ArrayList<SensorValueListener>();
    }

    public boolean registerStateListener(final SensorDeviceEventListener listener) {
        return stateListeners.add(listener);
    }

    public boolean unregisterStateListener(final SensorDeviceEventListener listener) {
        return stateListeners.remove(listener);
    }

    protected void notifySensorDeviceEvent(final int event) {
        for (SensorDeviceEventListener listener : stateListeners) {
            listener.onSensorDeviceEvent(this, event);
        }
    }

    public boolean registerValueListener(final SensorValueListener listener) {
        return valueListeners.add(listener);
    }

    public boolean unregisterValueListener(final SensorValueListener listener) {
        return valueListeners.remove(listener);
    }

    protected void notifySensorValueEvent(final Sensor sensor, final int measurementId, final float[] value) {
        for (SensorValueListener listener : valueListeners) {
            listener.onSensorValueChanged(sensor, measurementId, value);
        }
    }

    public abstract boolean initialize();
    public abstract void connect();
    public abstract void disconnect();
    public abstract boolean quit();

    public boolean isConnected() {
        return connected;
    }

    abstract String getBluetoothName();

    public String getBluetoothAddress() {
        return address;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(final String name) {
        this.deviceName = name;
    }

    public int getNumberOfSensors() {
        return sensors.size();
    }

    public Sensor getSensor(final int id) {
        return sensors.get(id);
    }
}
