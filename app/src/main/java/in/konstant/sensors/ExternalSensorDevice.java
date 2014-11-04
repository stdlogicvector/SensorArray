package in.konstant.sensors;

import in.konstant.BT.BTConnectionListener;
import in.konstant.BT.BTDevice;

public class ExternalSensorDevice
        extends SensorDevice
        implements BTConnectionListener {

    private final static String TAG = "ExtSensorDevice";
    private final static boolean DBG = false;

    private BTDevice btDevice;

    public ExternalSensorDevice(String address) {
        super(address);

        btDevice = new BTDevice(mAddress);
        btDevice.setConnectionListener(this);
    }

    public boolean initialize() {
        if (mConnectionState == STATE.CONNECTED) {

            return true;
        } else {
            return false;
        }
    }

    public void connect() {
        btDevice.connect();
    }

    public void disconnect() {
        btDevice.disconnect();
    }

    public boolean quit() {
        disconnect();
        btDevice.destroy();
        return true;
    }

    public String getBluetoothName() {
        return btDevice.getName();
    }

    public void onConnected() {

        mConnectionState = STATE.CONNECTED;
        onSensorDeviceStateChange();
    }

    public void onConnecting() {

        mConnectionState = STATE.CONNECTING;
        onSensorDeviceStateChange();
    }

    public void onDisconnected() {

        mConnectionState = STATE.DISCONNECTED;
        onSensorDeviceStateChange();
    }

    public void onConnectionLost() {

        mConnectionState = STATE.DISCONNECTED;
        onSensorDeviceStateChange();
    }

    public void onConnectionFailed() {

        mConnectionState = STATE.DISCONNECTED;
        onSensorDeviceStateChange();
    }

}
