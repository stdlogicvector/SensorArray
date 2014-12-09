package in.konstant.sensors;

// Used to inform Listeners about changes in Sensorvalues
public interface SensorValueListener {
    void onSensorValueChanged(final Sensor sensor, final int measurementId, final float[] value);
}
