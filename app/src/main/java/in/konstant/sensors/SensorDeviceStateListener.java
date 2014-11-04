package in.konstant.sensors;

public interface SensorDeviceStateListener {
    public void onSensorDeviceStateChange(final SensorDevice device, final int state);
}
