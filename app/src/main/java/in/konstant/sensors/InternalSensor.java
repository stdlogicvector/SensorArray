package in.konstant.sensors;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class InternalSensor extends Sensor implements SensorEventListener {
    private android.hardware.Sensor mSensor;
    private SensorManager mSensorManager;

    public InternalSensor(SensorManager manager, android.hardware.Sensor sensor) {
        super();

        this.mSensor = sensor;
        this.mSensorManager = manager;

        initializeMeasurements();
    }

    public String getName() {
        return mSensor.getName();
    }

    public String getPart() {
        return mSensor.getVendor() + ' ' + mSensor.getVersion();
    }

    public Measurement getMeasurement(int id) {
        return null;
    }

    public void activate() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void deactivate() {
        mSensorManager.unregisterListener(this);
    }

    public float[] getValue(int id) {
        float[] result = new float[1];

        return result;
    }

    public void onSensorChanged(SensorEvent event) {

    }

    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }

    private void initializeMeasurements() {
        Range[] ranges = {new Range(0, mSensor.getMaximumRange(), 1)};
        Subunit[] subunits;

        switch (mSensor.getType()) {
            case android.hardware.Sensor.TYPE_GYROSCOPE_UNCALIBRATED:

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.DEGREE, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -1);

                mMeasurements.add(new Measurement(
                                "Angular Rate Vector",
                                3,
                                ranges,
                                new Unit("Angular Velocity", "rad/s", Prefix.NO_PREFIX, subunits),
                                mSensor.getMinDelay())
                );

                mMeasurements.add(new Measurement(
                                "Drift Vector",
                                3,
                                ranges,
                                new Unit("Angular Velocity", "rad/s", Prefix.NO_PREFIX, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:

                subunits = new Subunit[3];
                subunits[0] = new Subunit(BaseUnit.KILOGRAM, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -2);
                subunits[2] = new Subunit(BaseUnit.AMPERE, -1);

                mMeasurements.add(new Measurement(
                                "Magnetic Flux Vector",
                                3,
                                ranges,
                                new Unit("Tesla", "T", Prefix.MICRO, subunits),
                                mSensor.getMinDelay())
                );

                mMeasurements.add(new Measurement(
                                "Drift Vector",
                                3,
                                ranges,
                                new Unit("Tesla", "T", Prefix.MICRO, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_GRAVITY:
            case android.hardware.Sensor.TYPE_ACCELEROMETER:

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.METER, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -2);

                mMeasurements.add(new Measurement(
                                "Acceleration Vector",
                                3,
                                ranges,
                                new Unit("Acceleration", "a", Prefix.NO_PREFIX, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_MAGNETIC_FIELD:

                subunits = new Subunit[3];
                subunits[0] = new Subunit(BaseUnit.KILOGRAM, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -2);
                subunits[2] = new Subunit(BaseUnit.AMPERE, -1);

                mMeasurements.add(new Measurement(
                                "Magnetic Flux Vector",
                                3,
                                ranges,
                                new Unit("Tesla", "T", Prefix.MICRO, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_GYROSCOPE:

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.DEGREE, +1);
                subunits[1] = new Subunit(BaseUnit.SECOND, -1);

                mMeasurements.add(new Measurement(
                                "Angular Rate Vector",
                                3,
                                ranges,
                                new Unit("Angular Velocity", "rad/s", Prefix.NO_PREFIX, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_LIGHT:

                subunits = new Subunit[2];
                subunits[0] = new Subunit(BaseUnit.CANDELA, +1);
                subunits[1] = new Subunit(BaseUnit.METER, -2);

                mMeasurements.add(new Measurement(
                                "Illuminance",
                                1,
                                ranges,
                                new Unit("Lux", "lx", Prefix.NO_PREFIX, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_AMBIENT_TEMPERATURE:

                subunits = new Subunit[1];
                subunits[0] = new Subunit(BaseUnit.KELVIN, +1);

                mMeasurements.add(new Measurement(
                                "Ambient Temperature",
                                1,
                                ranges,
                                new Unit("Celsius", "°C", Prefix.NO_PREFIX, subunits),
                                mSensor.getMinDelay())
                );
            break;

            case android.hardware.Sensor.TYPE_PROXIMITY:

                subunits = new Subunit[1];
                subunits[0] = new Subunit(BaseUnit.METER, +1);

                mMeasurements.add(new Measurement(
                                "Proximity",
                                1,
                                ranges,
                                new Unit("Centimeter", "cm", Prefix.CENTI, subunits),
                                mSensor.getMinDelay())
                );
                break;

            case android.hardware.Sensor.TYPE_PRESSURE:
                subunits = new Subunit[3];
                subunits[0] = new Subunit(BaseUnit.KILOGRAM, +1);
                subunits[1] = new Subunit(BaseUnit.METER, -1);
                subunits[2] = new Subunit(BaseUnit.SECOND, -2);

                mMeasurements.add(new Measurement(
                                "Atmospheric Pressure",
                                1,
                                ranges,
                                new Unit("Millibar", "mBar", Prefix.MILLI, subunits),
                                mSensor.getMinDelay())
                );
            break;
        }
    }
}