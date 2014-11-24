package in.konstant.sensorarray;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import in.konstant.R;
import in.konstant.sensors.Sensor;
import in.konstant.sensors.SensorArray;
import in.konstant.sensors.SensorDevice;

public class SensorFragment extends Fragment {

    private static final String ARG_DEVICE_NUMBER = "device_number";
    private static final String ARG_SENSOR_NUMBER = "sensor_number";

    private SensorArray sensorArray;
    private SensorDevice device;
    private Sensor sensor;

    private int deviceNumber;
    private int sensorNumber;

    private MeasurementPagerAdapter mAdapter;
    private ViewPager mPager;

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

        if (!(deviceNumber < sensorArray.count()))
            deviceNumber = 0; // Fall back to Internal Device

        device = sensorArray.getDevice(deviceNumber);

        if (!(sensorNumber < device.getNumberOfSensors()))
            sensorNumber = 0; // Fall back to first Sensor

        sensor = device.getSensor(sensorNumber);

        if (sensor != null)
            ((MainActivity) getActivity()).onFragmentCreated(sensor.getName());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new MeasurementPagerAdapter(getFragmentManager(), deviceNumber, sensorNumber);
        mPager = (ViewPager) getView().findViewById(R.id.pgSensorMeasurements);
        mPager.setAdapter(mAdapter);
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

    public static class MeasurementPagerAdapter extends FragmentPagerAdapter {

        private final SensorArray sensorArray;
        private final int deviceNumber;
        private final int sensorNumber;

        public MeasurementPagerAdapter(FragmentManager fm,
                                       int deviceNumber,
                                       int sensorNumber) {
            super(fm);

            sensorArray = SensorArray.getInstance();

            this.deviceNumber = deviceNumber;
            this.sensorNumber = sensorNumber;
        }

        @Override
        public Fragment getItem(int num) {
            return MeasurementFragment.newInstance(deviceNumber, sensorNumber, num);

        }

        @Override
        public int getCount() {
            return sensorArray.getDevice(deviceNumber).getSensor(sensorNumber).getNumberOfMeasurements();
        }

    }
}
