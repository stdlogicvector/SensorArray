package in.konstant.sensors;

// Used to inform Listeners about changes in the SensorArray
// such as adding/removing/connecting/disconnecting SensorDevices

public interface SensorArrayStateListener {
    void onSensorDeviceAdd(final SensorDevice device);
    void onSensorDeviceRemove(final SensorDevice device);
    void onSensorDeviceConnecting(final SensorDevice device);
    void onSensorDeviceConnect(final SensorDevice device);
    void onSensorDeviceDisconnect(final SensorDevice device);
}
