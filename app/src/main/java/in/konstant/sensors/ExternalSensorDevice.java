package in.konstant.sensors;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import in.konstant.BT.BTDevice;

public class ExternalSensorDevice
        extends SensorDevice {

    private final static String TAG = "ExternalSensorDevice";
    private final static boolean DBG = true;

    private BTDevice btDevice;
    private SensorCommandHandler CommandHandler;

    public ExternalSensorDevice(String address) {
        super(address);

        btDevice = new BTDevice(mAddress);
        btDevice.setStateHandler(messageHandler);

        CommandHandler = new SensorCommandHandler(btDevice);
        CommandHandler.start();
    }

    public boolean initialize() {
        if (mConnected) {

            //TODO: Synchronize clearing of mSensors with UI-Tread to avoid accessing empty list
//            mSensors.clear();

            Runnable initializer = new Runnable() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    int nrOfSensors = CommandHandler.getNrOfSensors();

                    for (int sID = 0; sID < nrOfSensors; ++sID) {
                        ExternalSensor sensor = CommandHandler.getSensor(sID);

                        if (sensor != null) {
                            int nrOfMeasurements = CommandHandler.getNrOfMeasurements(sID);

                            for (int mID = 0; mID < nrOfMeasurements; ++mID) {
                                Measurement measurement = CommandHandler.getMeasurement(sID, mID);

                                if (measurement != null)
                                    sensor.addMeasurement(measurement);
                            }

                            mSensors.add(sensor);
                        }
                    }

                    messageHandler.sendEmptyMessage(SensorEvent.INITIALIZED);
                }
            };

            new Thread(initializer).start();

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
        CommandHandler.interrupt();
        disconnect();
        btDevice.destroy();
        return true;
    }

    public String getBluetoothName() {
        return btDevice.getName();
    }

    public boolean getMeasurementValue(final int sensorId, final int measurementId) {
        if (mConnected) {
            Runnable valueGetter = new Runnable() {
                @Override
                public void run() {
                    float[] value = CommandHandler.getSensorValue(sensorId, measurementId);

                    Message msg = messageHandler.obtainMessage(SensorEvent.VALUE, sensorId, measurementId);
                    Bundle msgData = new Bundle();
                    msgData.putFloatArray("VALUE", value);
                    msg.setData(msgData);
                    messageHandler.sendMessage(msg);
                }
            };

            new Thread(valueGetter).start();

            return true;
        } else
            return false;
    }

    private final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DBG) Log.d(TAG, "handleMessage(" + msg.what + ")");

            // Translating BTEvent to SensorEvent
            switch (msg.what) {
                case BTDevice.BTStateEvent.CONNECTED:
                    mConnected = true;
                    notifySensorDeviceEvent(SensorEvent.CONNECTED);
                    initialize();
                    break;
                case SensorEvent.INITIALIZED:
                    notifySensorDeviceEvent(SensorEvent.INITIALIZED);
                    break;
                case SensorEvent.VALUE:
                    notifySensorValueEvent(getSensor(msg.arg1),
                                           msg.arg2,
                                           msg.getData().getFloatArray("VALUE"));
                    break;
                case BTDevice.BTStateEvent.DISCONNECTED:
                    mConnected = false;
                    notifySensorDeviceEvent(SensorEvent.DISCONNECTED);
                    break;
                case BTDevice.BTStateEvent.CONNECTING:
                    mConnected = false;
                    notifySensorDeviceEvent(SensorEvent.CONNECTING);
                    break;
                case BTDevice.BTStateEvent.CONNECTION_FAILED:
                    mConnected = false;
                    notifySensorDeviceEvent(SensorEvent.CONNECTION_FAILED);
                    break;
                case BTDevice.BTStateEvent.CONNECTION_LOST:
                    mConnected = false;
                    notifySensorDeviceEvent(SensorEvent.CONNECTION_LOST);
                    break;
            }
        }
    };

}
