package in.konstant.sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.*;
import android.hardware.Sensor;
import android.os.Build;

import java.util.List;

public class InternalSensorDevice extends SensorDevice {
    private final Context mContext;
    private final SensorManager mSensorManager;

    public InternalSensorDevice(Context context) {
        super(BluetoothAdapter.getDefaultAdapter().getAddress());
        this.mContext = context;

        this.mDeviceName = Build.MODEL + " (Internal)";
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mConnected = (mSensorManager != null);
    }

    public boolean initialize() {
        List<Sensor> internalSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor internalSensor : internalSensors) {
            mSensors.add(new InternalSensor(mSensorManager, internalSensor));
        }

        return true;
    }

    public void connect() {

    }

    public void disconnect() {
        for (in.konstant.sensors.Sensor mSensor : mSensors) {
            mSensor.deactivate();
        }
    }

    public int getConnectionState() {
        if (mSensorManager != null) {
            return STATE.CONNECTED;
        } else{
            return STATE.DISCONNECTED;
        }
    }

    public String getBluetoothName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }
}
