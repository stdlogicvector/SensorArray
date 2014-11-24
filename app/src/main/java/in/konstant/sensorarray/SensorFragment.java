package in.konstant.sensorarray;

import android.app.Activity;
import android.app.Fragment;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.konstant.R;
import in.konstant.sensors.Sensor;
import in.konstant.sensors.SensorArray;
import in.konstant.sensors.SensorArrayAdapter;
import in.konstant.sensors.SensorDevice;

public class SensorFragment extends Fragment {

    private static final String ARG_DEVICE_NUMBER = "device_number";
    private static final String ARG_SENSOR_NUMBER = "sensor_number";

    private SensorArray sensorArray;
    private SensorDevice device;
    private Sensor sensor;

    private int deviceNumber;
    private int sensorNumber;

    // Returns a new instance of this fragment for the given sensor
    public static SensorFragment newInstance(int deviceNumber, int sensorNumber) {
        SensorFragment fragment = new SensorFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEVICE_NUMBER, deviceNumber);
        args.putInt(ARG_SENSOR_NUMBER, sensorNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public SensorFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.deviceNumber = getArguments().getInt(ARG_DEVICE_NUMBER);
        this.sensorNumber = getArguments().getInt(ARG_SENSOR_NUMBER);

        sensorArray = SensorArray.getInstance();

        if (deviceNumber < sensorArray.count()) {
            device = sensorArray.getDevice(deviceNumber);

            if (sensorNumber < device.getNumberOfSensors())
                sensor = device.getSensor(sensorNumber);
        }

        if (sensor != null)
            ((MainActivity) getActivity()).onFragmentCreated(sensor.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

        if (sensor != null) {
            if (device.isConnected())
                ((ImageView) rootView.findViewById(R.id.icSensorDeviceIcon)).setImageResource(R.drawable.ic_device_connected);
            else
                ((ImageView) rootView.findViewById(R.id.icSensorDeviceIcon)).setImageResource(R.drawable.ic_device_disconnected);

            ((TextView) rootView.findViewById(R.id.tvDeviceName)).setText(device.getDeviceName());
            ((TextView) rootView.findViewById(R.id.tvDeviceAddress)).setText(device.getBluetoothAddress());
            ((TextView) rootView.findViewById(R.id.tvSensorName)).setText(sensor.getName());
            ((TextView) rootView.findViewById(R.id.tvSensorPart)).setText(sensor.getPart());
            ((ImageView) rootView.findViewById(R.id.icSensorIcon)).setImageResource(sensor.getType().icon());



        }

        return rootView;
    }
}
