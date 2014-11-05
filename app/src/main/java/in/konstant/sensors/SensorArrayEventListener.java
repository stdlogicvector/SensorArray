package in.konstant.sensors;

// Used to inform Listeners about changes in the SensorArray
// such as adding/removing/connecting/disconnecting SensorDevices

public interface SensorArrayEventListener {
    void onSensorArrayEvent(final SensorDevice device, final int event);
}
