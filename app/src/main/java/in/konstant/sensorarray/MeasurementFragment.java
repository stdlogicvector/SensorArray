package in.konstant.sensorarray;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import in.konstant.R;
import in.konstant.sensors.Measurement;
import in.konstant.sensors.Sensor;
import in.konstant.sensors.SensorArray;

public class MeasurementFragment extends Fragment {

    private static final String ARG_DEVICE_NUMBER = "device_number";
    private static final String ARG_SENSOR_NUMBER = "sensor_number";
    private static final String ARG_MEASUREMENT_NUMBER = "measurement_number";

    private SensorArray sensorArray;
    private Measurement measurement;

    private int deviceNumber;
    private int sensorNumber;
    private int measurementNumber;

    // Returns a new instance of this fragment for the given sensor
    public static MeasurementFragment newInstance(int deviceNumber, int sensorNumber, int measurementNumber) {
        MeasurementFragment fragment = new MeasurementFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DEVICE_NUMBER, deviceNumber);
        args.putInt(ARG_SENSOR_NUMBER, sensorNumber);
        args.putInt(ARG_MEASUREMENT_NUMBER, measurementNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MeasurementFragment() {
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
        this.measurementNumber = getArguments().getInt(ARG_MEASUREMENT_NUMBER);

        sensorArray = SensorArray.getInstance();

        if (deviceNumber < sensorArray.count() &&
            measurementNumber < sensorArray.getDevice(deviceNumber).getNumberOfSensors()) {
            measurement = sensorArray.getDevice(deviceNumber)
                                     .getSensor(sensorNumber)
                                     .getMeasurement(measurementNumber);
        }

        if (measurement != null)
            ((MainActivity) getActivity()).onFragmentCreated(measurement.getName());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurement, container, false);

        if (measurement != null) {
            String unitDesc = measurement.getUnit().getPrefix().toString() +
                              "-" +
                              measurement.getUnit().getName() +
                              " (" +
                              measurement.getUnit().toString() +
                              ")";

            String name = measurement.getName();

            if (measurement.getSize() > 1)
                name = name.concat("[" + measurement.getSize() + "]");

            String range = measurement.getCurrentRange().getMin() +
                           measurement.getUnit().getPrefix().toString() +
                           " - " +
                           measurement.getCurrentRange().getMax() +
                           measurement.getUnit().getPrefix().toString();

            ((TextView) rootView.findViewById(R.id.tvMeasurementName)).setText(name);
            ((TextView) rootView.findViewById(R.id.tvMeasurementUnitName)).setText(unitDesc);
            ((TextView) rootView.findViewById(R.id.tvMeasurementUnitSubunits)).setText(measurement.getUnit().getSIUnit(false));
        }

        return rootView;
    }
}
