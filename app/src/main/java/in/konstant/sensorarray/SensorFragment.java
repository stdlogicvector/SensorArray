package in.konstant.sensorarray;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.konstant.R;
import in.konstant.sensors.Sensor;
import in.konstant.sensors.SensorArray;

public class SensorFragment extends Fragment {

    private static final String ARG_DEVICE_NUMBER = "device_number";
    private static final String ARG_SENSOR_NUMBER = "sensor_number";

    private SensorArray sensorArray;
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

        sensorArray = SensorArray.getInstance(getActivity());
        sensor = sensorArray.getChild(deviceNumber, sensorNumber);

        ((MainActivity) getActivity()).onFragmentCreated(sensor.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

        ((TextView)rootView.findViewById(R.id.tvSensorName)).setText(sensor.getName());

        return rootView;
    }
}
