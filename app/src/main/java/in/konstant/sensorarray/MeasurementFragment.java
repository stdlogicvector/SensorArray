package in.konstant.sensorarray;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import in.konstant.R;
import in.konstant.sensors.Measurement;
import in.konstant.sensors.Sensor;
import in.konstant.sensors.SensorArray;
import in.konstant.sensors.SensorDevice;
import in.konstant.sensors.SensorValueListener;

public class MeasurementFragment
        extends Fragment
        implements SensorValueListener {

    private static final int PLOT_HISTORY_SIZE = 30;

    private static final String ARG_DEVICE_NUMBER = "device_number";
    private static final String ARG_SENSOR_NUMBER = "sensor_number";
    private static final String ARG_MEASUREMENT_NUMBER = "measurement_number";

    private static final HashMap<Integer, MeasurementFragment> instances = new HashMap<Integer, MeasurementFragment>();

    private SensorArray sensorArray;
    private SensorDevice sensorDevice = null;
    private Sensor sensor = null;
    private Measurement measurement = null;

    private int deviceNumber;
    private int sensorNumber;
    private int measurementNumber;

    private Activity activity;
    private TextView value;

    private XYPlot valuePlot;
    private ArrayList<SimpleXYSeries> valueSeries;

    // Returns a new instance of this fragment for the given sensor
    public static MeasurementFragment getInstance(int deviceNumber, int sensorNumber, int measurementNumber) {
        Integer key = (deviceNumber << 16) + (sensorNumber << 8) + measurementNumber;

        Log.d("MeasurementFragment", "get " + deviceNumber + ' ' + sensorNumber + ' ' + measurementNumber + " " + key);

        MeasurementFragment instance = instances.get(key);

        if (instance == null) {
            instance = new MeasurementFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_DEVICE_NUMBER, deviceNumber);
            args.putInt(ARG_SENSOR_NUMBER, sensorNumber);
            args.putInt(ARG_MEASUREMENT_NUMBER, measurementNumber);
            instance.setArguments(args);

            instances.put(key, instance);
        }

        return instance;
    }

    public MeasurementFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
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

            sensorDevice = sensorArray.getDevice(deviceNumber);
            sensor = sensorDevice.getSensor(sensorNumber);
            measurement = sensor.getMeasurement(measurementNumber);

            sensorDevice.registerValueListener(this);
        }
/*
        if (measurement != null)
            ((MainActivity) getActivity()).onFragmentCreated(measurement.getName());
*/
    }

    @Override
    public void onDestroy() {
        sensorDevice.unregisterValueListener(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_measurement, container, false);

        if (measurement != null) {
            String unitDesc = getResources().getString(R.string.MeasurementFragmentUnitName);

            if (!measurement.getUnit().noPrefix())
                unitDesc = unitDesc.concat(measurement.getUnit().getPrefix().toString() + "-");

            unitDesc = unitDesc.concat(measurement.getUnit().getName() +
                       " (" +
                       measurement.getUnit().toString() +
                       ")");

            String name = measurement.getName();

            if (measurement.getSize() > 1)
                name = name.concat(" [" + measurement.getSize() + "]");

            String SIunit = getResources().getString(R.string.MeasurementFragmentUnitSI) +
                            measurement.getUnit().getSIUnit(false);

            String range = getResources().getString(R.string.MeasurementFragmentRange) +
                           measurement.getCurrentRange().getMinFormatted() +
                           measurement.getUnit().toString() +
                           " - " +
                           measurement.getCurrentRange().getMaxFormatted() +
                           measurement.getUnit().toString();

            ((TextView) rootView.findViewById(R.id.tvMeasurementName)).setText(name);
            ((TextView) rootView.findViewById(R.id.tvMeasurementUnitName)).setText(unitDesc);
            ((TextView) rootView.findViewById(R.id.tvMeasurementUnitSubunits)).setText(SIunit);
            ((TextView) rootView.findViewById(R.id.tvMeasurementRange)).setText(range);

            Typeface tf = Typeface.createFromAsset(activity.getAssets(), "fonts/pocket_calculator.ttf");

            value = ((TextView) rootView.findViewById(R.id.tvMeasurementValue));
            value.setTypeface(tf);

            initPlot(rootView);

            ((Button) rootView.findViewById(R.id.btEnableMeasurement)).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    enableOnClick(v);
                }
            });
        }

        return rootView;
    }

    private void initPlot(View rootView) {
        valuePlot = (XYPlot) rootView.findViewById(R.id.apMeasurementPlot);
        valueSeries = new ArrayList<SimpleXYSeries>();

        valuePlot.setRangeBoundaries(measurement.getCurrentRange().getMin(),
                measurement.getCurrentRange().getMax(),
                BoundaryMode.FIXED);

        valuePlot.setDomainBoundaries(0, PLOT_HISTORY_SIZE, BoundaryMode.FIXED);

        for (int s = 0; s < measurement.getSize(); s++) {
            SimpleXYSeries series = new SimpleXYSeries("[" + s + "]");

            series.useImplicitXVals();

            valueSeries.add(series);

            valuePlot.addSeries(series, new LineAndPointFormatter(Color.RED, Color.GREEN, Color.TRANSPARENT, null));
        }

        valuePlot.setDomainStepValue(4);
        valuePlot.setTicksPerRangeLabel(3);

        valuePlot.setDomainLabel("Sample");
        valuePlot.getDomainLabelWidget().pack();

        valuePlot.setRangeLabel(measurement.getUnit().getSymbol());
        valuePlot.getRangeLabelWidget().pack();
    }

    public void enableOnClick(View v) {
        if (sensor.isMeasuring()) {
            ((Button) v).setText("Enable");
            sensor.stopMeasuring();
            sensor.deactivate();
        } else {
            sensor.activate();
            if (sensor.startMeasuring(measurementNumber, 500))
                ((Button) v).setText("Disable");
        }
    }

    public void onSensorValueChanged(final Sensor sensor, final int measurementId, final float[] value) {
        this.value.setText("");

        if (valueSeries.get(0).size() > PLOT_HISTORY_SIZE) {
            for (SimpleXYSeries series:valueSeries)
                series.removeFirst();
        }

        int n = 0;

        for (SimpleXYSeries series:valueSeries) {
            if (n < value.length) {
                this.value.append("" + value[n] + " ");
                valueSeries.get(n).addLast(null, value[n]);
            }
            n++;
        }

        valuePlot.redraw();
    }
}
