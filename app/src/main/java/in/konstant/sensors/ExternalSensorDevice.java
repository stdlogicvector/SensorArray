package in.konstant.sensors;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import in.konstant.BT.BTDevice;

public class ExternalSensorDevice
        extends SensorDevice {

    private final static String TAG = "ExternalSensorDevice";
    private final static boolean DBG = true;

    public final BTDevice btDevice;
    public final SensorCommandHandler CommandHandler;

    public ExternalSensorDevice(String address) {
        super(address);

        btDevice = new BTDevice(this.address);
        btDevice.setStateHandler(messageHandler);

        CommandHandler = new SensorCommandHandler(this);
        CommandHandler.start();
    }

    public boolean initialize() {
        if (connected) {

            //TODO: Synchronize clearing of sensors with UI-Tread to avoid accessing empty list
//            sensors.clear();

            Runnable initializer = new Runnable() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                    try {

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

                                sensors.add(sensor);
                            }
                        }

                        messageHandler.sendEmptyMessage(SensorEvent.INITIALIZED);
                    } catch (IllegalStateException e) {
                        messageHandler.sendEmptyMessage(SensorEvent.INIT_FAILED);
                    }
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
        for (Sensor sensor : sensors)
            sensor.stopMeasuring();

        btDevice.disconnect();
    }

    public boolean quit() {
        disconnect();
        CommandHandler.interrupt();
        btDevice.destroy();
        return true;
    }

    public String getBluetoothName() {
        return btDevice.getName();
    }

   public final Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BTDevice.BTStateEvent.CONNECTED:
                    connected = true;
                    notifySensorDeviceEvent(SensorEvent.CONNECTED);
                    initialize();
                    break;
                case BTDevice.BTStateEvent.DISCONNECTED:
                    connected = false;
                    notifySensorDeviceEvent(SensorEvent.DISCONNECTED);
                    break;
                case BTDevice.BTStateEvent.CONNECTING:
                    connected = false;
                    notifySensorDeviceEvent(SensorEvent.CONNECTING);
                    break;
                case BTDevice.BTStateEvent.CONNECTION_FAILED:
                    connected = false;
                    notifySensorDeviceEvent(SensorEvent.CONNECTION_FAILED);
                    break;
                case BTDevice.BTStateEvent.CONNECTION_LOST:
                    connected = false;
                    notifySensorDeviceEvent(SensorEvent.CONNECTION_LOST);
                    break;

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
