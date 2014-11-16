package in.konstant.sensors;

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
        btDevice.setStateHandler(BTStateHandler);

        CommandHandler = new SensorCommandHandler();
        CommandHandler.start();

        CommandHandler.waitUntilReady();
        CommandHandler.setReplyHandler(CommandReplyHandler);
        btDevice.setDataHandler(CommandHandler.getDataHandler());
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

    private final Handler CommandReplyHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    private final Handler BTStateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (DBG) Log.d(TAG, "handleMessage(" + msg.what + ")");

            // Translating BTEvent to SensorEvent
            switch (msg.what) {
                case BTDevice.BTStateEvent.CONNECTED:
                    notifySensorDeviceEvent(SensorEvent.CONNECTED);
                    break;
                case BTDevice.BTStateEvent.DISCONNECTED:
                    notifySensorDeviceEvent(SensorEvent.DISCONNECTED);
                    break;
                case BTDevice.BTStateEvent.CONNECTING:
                    notifySensorDeviceEvent(SensorEvent.CONNECTING);
                    break;
                case BTDevice.BTStateEvent.CONNECTION_FAILED:
                    notifySensorDeviceEvent(SensorEvent.CONNECTION_FAILED);
                    break;
                case BTDevice.BTStateEvent.CONNECTION_LOST:
                    notifySensorDeviceEvent(SensorEvent.CONNECTION_LOST);
                    break;
            }
        }
    };

}
