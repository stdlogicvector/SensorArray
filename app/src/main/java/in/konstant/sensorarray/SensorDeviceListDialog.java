package in.konstant.sensorarray;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import in.konstant.R;

public class SensorDeviceListDialog extends DialogFragment {
    public static final String ARG_ID = "arg_id";
    public static final String ARG_NAME = "arg_name";
    public static final String ARG_CONNECTED = "arg_connected";

    public interface SensorDeviceListDialogListener {
        public void onSensorDeviceListDialogConnect(int id, boolean connected);
        public void onSensorDeviceListDialogSettings(int id);
        public void onSensorDeviceListDialogDelete(int id);
    }

    SensorDeviceListDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (SensorDeviceListDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement SensorDeviceListDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final Bundle args = getArguments();
        int itemsId;

        if (args.getBoolean(ARG_CONNECTED)) {
            itemsId = R.array.device_list_item_dialog_connected;
        } else {
            itemsId = R.array.device_list_item_dialog_disconnected;
        }

        builder.setTitle(args.getString(ARG_NAME))
                .setIcon(android.R.drawable.ic_menu_preferences)
                .setItems(itemsId, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Connect/Disconnect
                                mListener.onSensorDeviceListDialogConnect(args.getInt(ARG_ID), args.getBoolean(ARG_CONNECTED));
                                break;

                            case 1: // Delete from List
                                mListener.onSensorDeviceListDialogDelete(args.getInt(ARG_ID));
                                break;

                            case 2: // Settings
                                mListener.onSensorDeviceListDialogSettings(args.getInt(ARG_ID));
                                break;
                        }
                    }
                });

        return builder.create();
    }
}
