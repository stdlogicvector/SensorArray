package in.konstant.sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.*;
import android.hardware.Sensor;
import android.os.Build;
import android.os.HandlerThread;

import java.util.List;

public class InternalSensorDevice
        extends SensorDevice {

    private final static String TAG = "InternalSensorDevice";
    private final static boolean DBG = false;

    private final SensorManager mSensorManager;

    public InternalSensorDevice(final Context context) {
        super(BluetoothAdapter.getDefaultAdapter().getAddress());

        this.mDeviceName = Build.MODEL + " (Internal)";
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        mConnected = (mSensorManager != null);
    }

    public boolean initialize() {
        List<Sensor> internalSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor internalSensor : internalSensors) {
            if (internalSensor.getType() != Sensor.TYPE_ORIENTATION &&
                internalSensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
                mSensors.add(new InternalSensor(mSensorManager, internalSensor));
            }
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

    public boolean quit() {
        disconnect();
        return true;
    }

    public String getBluetoothName() {
        return BluetoothAdapter.getDefaultAdapter().getName();
    }

    public boolean getMeasurementValue(final int sensorId, final int measurementId) {
        if (mConnected) {

            //TODO: Better Solution for different behaviour of InternalSensor?
            float[] value = ((InternalSensor) mSensors.get(sensorId)).getValue(measurementId);

            notifySensorValueEvent(mSensors.get(sensorId), measurementId, value);

            return true;
        } else
            return false;
    }
}
