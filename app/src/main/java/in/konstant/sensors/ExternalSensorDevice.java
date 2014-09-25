package in.konstant.sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import in.konstant.BT.BTDevice;

public class ExternalSensorDevice extends SensorDevice {
    private final Context context;
    private BTDevice btDevice;

    public ExternalSensorDevice(Context context) {
        super(BluetoothAdapter.getDefaultAdapter().getAddress());
        this.context = context;

        btDevice = new BTDevice(context, mAddress);
    }

    public boolean initialize() {

        return true;
    }

    public void connect() {

    }

    public void disconnect() {

    }

    public int getConnectionState() {
        switch (btDevice.getState()) {
            case BTDevice.STATE.CONNECTED:    return STATE.CONNECTED;
            case BTDevice.STATE.CONNECTING:   return STATE.CONNECTING;
            default:
            case BTDevice.STATE.DISCONNECTED: return STATE.DISCONNECTED;
        }
    }

    public String getBluetoothName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }

}
