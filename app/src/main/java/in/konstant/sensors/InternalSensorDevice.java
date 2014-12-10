package in.konstant.sensors;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.*;
import android.hardware.Sensor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.List;

public class InternalSensorDevice
        extends SensorDevice {

    private final static String TAG = "InternalSensorDevice";
    private final static boolean DBG = false;

    public final SensorManager sensorManager;

    public InternalSensorDevice(final Context context) {
        super(BluetoothAdapter.getDefaultAdapter().getAddress());

        this.deviceName = Build.MODEL + " (Internal)";
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        connected = (sensorManager != null);
    }

    public boolean initialize() {
        List<Sensor> internalSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        int id = 0;

        for (Sensor internalSensor : internalSensors) {
            if (internalSensor.getType() != Sensor.TYPE_ORIENTATION &&
                internalSensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
                sensors.add(new InternalSensor(this, internalSensor, id++));
            }
        }

        messageHandler.sendEmptyMessage(SensorEvent.INITIALIZED);

        return true;
    }

    public void connect() {
        // InternalSensors are always connected
    }

    public void disconnect() {
        // InternalSensors are always connected
        // Use disconnect() to stop measurement of all sensors
        for (in.konstant.sensors.Sensor mSensor : sensors) {
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

    public final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DBG) Log.d(TAG, "handleMessage(" + msg.what + ")");

            switch (msg.what) {
                case SensorEvent.INITIALIZED:
                    notifySensorDeviceEvent(SensorEvent.INITIALIZED);
                    break;
                case SensorEvent.VALUE:
                    notifySensorValueEvent(getSensor(msg.arg1),
                            msg.arg2,
                            msg.getData().getFloatArray("VALUE"));
                    break;
            }
        }
    };

}
