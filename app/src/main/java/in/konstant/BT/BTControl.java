package in.konstant.BT;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BTControl {
    public static final int REQ_ENABLE_BT = 2;

    public static boolean available() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean enabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            return true;
        } else {
            return false;
        }
    }

    public static void enable(Context context) {
        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        ((Activity) context).startActivityForResult(enableIntent, REQ_ENABLE_BT);
    }

    public static void disable() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
        }
    }

    public static boolean getBluetoothEnabled(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                return true;
            } else {
                return false;
            }
        } else {
                return true;
        }
    }

    public static void registerStateChangeReceiver(Context context, BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
    }

    public static void unregisterStateChangeReceiver(Context context, BroadcastReceiver receiver) {
        context.unregisterReceiver(receiver);
    }


    public static String getServiceMajorClassName(int majorClass) {
        switch (majorClass) {
            case BluetoothClass.Service.AUDIO:
                return "Audio";
            case BluetoothClass.Service.CAPTURE:
                return "Capture";
            case BluetoothClass.Service.INFORMATION:
                return "Information";
            case BluetoothClass.Service.NETWORKING:
                return "Networking";
            case BluetoothClass.Service.OBJECT_TRANSFER:
                return "Transfer";
            case BluetoothClass.Service.POSITIONING:
                return "Positioning";
            case BluetoothClass.Service.RENDER:
                return "Render";
            case BluetoothClass.Service.TELEPHONY:
                return "Telephony";
            default:
                return "Unknown";
        }
    }

    public static String getDeviceMajorClassName(int majorClass) {
        switch (majorClass) {
            case BluetoothClass.Device.Major.COMPUTER:
                return "PC";
            case BluetoothClass.Device.Major.PHONE:
                return "Phone";
            case BluetoothClass.Device.Major.IMAGING:
                return "Imaging";
            case BluetoothClass.Device.Major.NETWORKING:
                return "Networking";
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AV";
            case BluetoothClass.Device.Major.HEALTH:
                return "Health";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "Peripheral";
            case BluetoothClass.Device.Major.TOY:
                return "Toy";
            case BluetoothClass.Device.Major.WEARABLE:
                return "Wearable";
            case BluetoothClass.Device.Major.MISC:
                return "Misc";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "Uncategorized";
            default:
                return "Unknown";
        }
    }
}


