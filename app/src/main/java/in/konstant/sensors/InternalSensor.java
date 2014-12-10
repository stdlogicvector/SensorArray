package in.konstant.sensors;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Message;

public class InternalSensor
        extends Sensor
        implements SensorEventListener {

    private final static String TAG = "InternalSensor";
    private final static boolean DBG = false;

    private android.hardware.Sensor sensor;
    private InternalSensorDevice sensorDevice;

    private int activeMeasurementId;

    private int[] offsets;

    public InternalSensor(final InternalSensorDevice sensorDevice, final android.hardware.Sensor sensor, final int id) {
        super(id, Type.GENERIC);

        this.sensorDevice = sensorDevice;
        this.sensor = sensor;

        initializeMeasurements();

        active = true;
        activeMeasurementId = 0;
    }

    public String getName() {
        return type.toString();
    }

    public String getPart() {
        return sensor.getVendor() + " " + sensor.getName();
    }

    public void activate() {
        active = true;
    }

    public void deactivate() {
        stopMeasuring();
        active = false;
    }

    public boolean startMeasuring(final int measurementId, final int interval) {
        if (active && measurementId < measurements.size()) {
            activeMeasurementId = measurementId;

            measuring = sensorDevice.sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);

        } else
            measuring = false;

        return measuring;
    }

    public void stopMeasuring() {
        sensorDevice.sensorManager.unregisterListener(this);
        measuring = false;
    }

    private float[] extractValue(final float[] values, final int id) {
        int size = measurements.get(id).getSize();
        float[] result = new float[size];

        for (int v = offsets[id]; v < offsets[id] + size; ++v) {
            result[v] = values[v];
        }

        return result;
    }

    public void onSensorChanged(SensorEvent event) {
        Message msg = sensorDevice.messageHandler.obtainMessage(in.konstant.sensors.SensorEvent.VALUE, id, activeMeasurementId);
        Bundle msgData = new Bundle();
        msgData.putFloatArray("VALUE", extractValue(event.values, activeMeasurementId));
        msg.setData(msgData);
        sensorDevice.messageHandler.sendMessage(msg);
    }

    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }

    private void initializeMeasurements() {
        Range[] ranges = {new Range(0, sensor.getMaximumRange(), 1)};
        Subunit[] subunits;

        switch (sensor.getType()) {
            case android.hardware.Sensor.TYPE_GYROSCOPE_UNCALIBRATED:

                type = Type.ROTATION;

                offsets = new int[2];
                offsets[0] = 0;
                offsets[1] = 3;

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.DEGREE, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -1);

                measurements.add(new Measurement(
                                "Angular Rate Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Angular Velocity", "rad/s", Prefix.NO_PREFIX, subunits))
                );

                measurements.add(new Measurement(
                                "Drift Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Angular Velocity", "rad/s", Prefix.NO_PREFIX, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:

                type = Type.MAGNETIC;

                offsets = new int[2];
                offsets[0] = 0;
                offsets[1] = 3;

                subunits = new Subunit[3];
                subunits[0] = new Subunit(BaseUnit.KILOGRAM, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -2);
                subunits[2] = new Subunit(BaseUnit.AMPERE, -1);

                measurements.add(new Measurement(
                                "Magnetic Flux Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Tesla", "T", Prefix.MICRO, subunits))
                );

                measurements.add(new Measurement(
                                "Drift Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Tesla", "T", Prefix.MICRO, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_LINEAR_ACCELERATION:
            case android.hardware.Sensor.TYPE_GRAVITY:
            case android.hardware.Sensor.TYPE_ACCELEROMETER:

                type = Type.ACCELERATION;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.METER, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -2);

                measurements.add(new Measurement(
                                "Acceleration Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Acceleration", "g", Prefix.NO_PREFIX, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_MAGNETIC_FIELD:

                type = Type.MAGNETIC;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[3];
                subunits[0] = new Subunit(BaseUnit.KILOGRAM, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -2);
                subunits[2] = new Subunit(BaseUnit.AMPERE, -1);

                measurements.add(new Measurement(
                                "Magnetic Flux Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Tesla", "T", Prefix.MICRO, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_GYROSCOPE:

                type = Type.ROTATION;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.DEGREE, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -1);

                measurements.add(new Measurement(
                                "Angular Rate Vector",
                                3,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Angular Velocity", "rad/s", Prefix.NO_PREFIX, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_LIGHT:

                type = Type.LIGHT;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.CANDELA, +1);
                subunits[1] = new Subunit(BaseUnit.METER, -2);

                measurements.add(new Measurement(
                                "Illuminance",
                                1,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Lux", "lx", Prefix.NO_PREFIX, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE:

                type = Type.TEMPERATURE;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[1];
                subunits[0] = new Subunit(BaseUnit.KELVIN, +1);

                measurements.add(new Measurement(
                                "Ambient Temperature",
                                1,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Celsius", "°C", Prefix.NO_PREFIX, subunits))
                );
            break;

            case android.hardware.Sensor.TYPE_PROXIMITY:

                type = Type.DISTANCE;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[1];
                subunits[0] = new Subunit(BaseUnit.METER, +1);

                measurements.add(new Measurement(
                                "Proximity",
                                1,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Meter", "m", Prefix.CENTI, subunits))
                );
                break;

            case android.hardware.Sensor.TYPE_PRESSURE:

                type = Type.PRESSURE;

                offsets = new int[1];
                offsets[0] = 0;

                subunits = new Subunit[3];
                subunits[0] = new Subunit(BaseUnit.KILOGRAM, +1);
                subunits[1] = new Subunit(BaseUnit.METER, -1);
                subunits[2] = new Subunit(BaseUnit.SECOND, -2);

                measurements.add(new Measurement(
                                "Atmospheric Pressure",
                                1,
                                sensor.getMinDelay(),
                                ranges,
                                new Unit("Bar", "Bar", Prefix.MILLI, subunits))
                );
            break;
        }
    }
}
