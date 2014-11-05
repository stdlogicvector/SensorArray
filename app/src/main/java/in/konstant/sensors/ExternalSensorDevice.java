package in.konstant.sensors;

import android.util.Log;

import in.konstant.BT.BTConnectionListener;
import in.konstant.BT.BTDevice;

public class ExternalSensorDevice
        extends SensorDevice
        implements BTConnectionListener {

    private final static String TAG = "ExternalSensorDevice";
    private final static boolean DBG = false;

    private BTDevice btDevice;

    public ExternalSensorDevice(String address) {
        super(address);

        btDevice = new BTDevice(mAddress);
        btDevice.setConnectionListener(this);
    }

    public boolean initialize() {
        if (mConnected) {

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

    public void onBTConnectionEvent(final int event) {
        notifySensorDeviceEvent(event);
    }

}
