package in.konstant.sensors;

import java.util.ArrayList;

public abstract class SensorDevice {
    protected String mAddress;
    protected String mDeviceName;

    protected int mConnectionState = STATE.DISCONNECTED;

    protected ArrayList<Sensor> mSensors;
    protected ArrayList<SensorDeviceStateListener> mStateListeners;

    public static final class STATE {
        public static final int DISCONNECTED = -1;
        public static final int CONNECTING = 0;
        public static final int CONNECTED = 1;
    }

    public SensorDevice(final String address) {
        this.mAddress = address;
        mSensors = new ArrayList<Sensor>();
        mStateListeners = new ArrayList<SensorDeviceStateListener>();
    }

    public boolean registerStateListener(final SensorDeviceStateListener listener) {
        return mStateListeners.add(listener);
    }

    public boolean unregisterStateListener(final SensorDeviceStateListener listener) {
        return mStateListeners.remove(listener);
    }

    protected void onSensorDeviceStateChange() {
        for (SensorDeviceStateListener listener : mStateListeners) {
            listener.onSensorDeviceStateChange(this, mConnectionState);
        }
    }

    public abstract boolean initialize();
    public abstract void connect();
    public abstract void disconnect();
    public abstract boolean quit();

    public int getConnectionState() {
        return mConnectionState;
    }

    public boolean isConnected() {
        return (mConnectionState == STATE.CONNECTED);
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
}
