package in.konstant.sensorarray;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.support.v4.view.ViewPager;
import android.support.v13.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import in.konstant.R;
import in.konstant.sensors.Sensor;
import in.konstant.sensors.SensorArray;
import in.konstant.sensors.SensorDevice;

public class SensorFragment extends Fragment {

    private static final String ARG_DEVICE_NUMBER = "device_number";
    private static final String ARG_SENSOR_NUMBER = "sensor_number";

    private static final HashMap<Integer, SensorFragment> instances = new HashMap<Integer, SensorFragment>();

    private SensorArray sensorArray;
    private SensorDevice device;
    private Sensor sensor;

    private int deviceNumber;
    private int sensorNumber;

    private int pagerId;

    private MeasurementPagerAdapter pagerAdapter;
    private ViewPager pager;

    // Returns a new instance of this fragment for the given sensor
    public static synchronized SensorFragment getInstance(int deviceNumber, int sensorNumber) {
        Integer key = (deviceNumber << 16) + (sensorNumber << 8);

        Log.d("SensorFragment", "getInstance " + deviceNumber + " " + sensorNumber);

        SensorFragment instance = instances.get(key);

        if (instance == null) {
            instance = new SensorFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_DEVICE_NUMBER, deviceNumber);
            args.putInt(ARG_SENSOR_NUMBER, sensorNumber);
            instance.setArguments(args);

            instances.put(key, instance);
        }

        return instance;
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
        this.pagerId = 0x00FFFFFF - ((deviceNumber + 1) << 8) - (sensorNumber + 1);

        sensorArray = SensorArray.getInstance();

        if (!(deviceNumber < sensorArray.count()))
            deviceNumber = 0; // Fall back to Internal Device

        device = sensorArray.getDevice(deviceNumber);

        if (!(sensorNumber < device.getNumberOfSensors()))
            sensorNumber = 0; // Fall back to first Sensor

        sensor = device.getSensor(sensorNumber);

        pagerAdapter = new MeasurementPagerAdapter(getFragmentManager(), deviceNumber, sensorNumber);

        if (sensor != null)
            ((MainActivity) getActivity()).onFragmentCreated(sensor.getName());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d("SensorFragment", "onActivityCreated()");

        super.onActivityCreated(savedInstanceState);

        pager = (ViewPager) getView().findViewById(pagerId);
        pager.setAdapter(pagerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sensor, container, false);

        Log.d("SensorFragment", "onCreateView() " + pagerId);

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

            ((ViewPager) rootView.findViewById(R.id.pgSensorMeasurements)).setId(pagerId);
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

            Log.d("MeasurementPagerAdapter", "new " + deviceNumber + " " + sensorNumber);

            sensorArray = SensorArray.getInstance();

            this.deviceNumber = deviceNumber;
            this.sensorNumber = sensorNumber;
        }

        @Override
        public Fragment getItem(int num) {
            Log.d("MeasurementPagerAdapter", "getItem " + deviceNumber + " " + sensorNumber + " " + num);
            return MeasurementFragment.getInstance(deviceNumber, sensorNumber, num);
        }

        @Override
        public int getCount() {
            return sensorArray.getDevice(deviceNumber).getSensor(sensorNumber).getNumberOfMeasurements();
        }

    }
}
