package in.konstant.sensors;

// Used to inform Listeners about changes in the SensorDevice
// such as connecting/disconnecting over BT

public interface SensorDeviceEventListener {
    public void onSensorDeviceEvent(final SensorDevice device, final int event);
}
