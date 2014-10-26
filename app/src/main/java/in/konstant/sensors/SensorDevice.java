package in.konstant.sensors;

import java.util.ArrayList;

public abstract class SensorDevice {
    protected String mAddress;
    protected String mDeviceName;
    protected boolean mConnected = false;
    protected ArrayList<Sensor> mSensors;

    public static final class STATE {
        public static final int DISCONNECTED = -1;
        public static final int CONNECTING = 0;
        public static final int CONNECTED = 1;
    }

    public SensorDevice(String address) {
        this.mAddress = address;
        mSensors = new ArrayList<Sensor>();
    }

    public abstract boolean initialize();
    public abstract void connect();
    public abstract void disconnect();
    public abstract int getConnectionState();
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

    public void setDeviceName(String name) {
        this.mDeviceName = name;
    }

    public int getNumberOfSensors() {
        return mSensors.size();
    }

    public Sensor getSensor(int id) {
        return mSensors.get(id);
    }
}
