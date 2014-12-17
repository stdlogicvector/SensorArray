package in.konstant.sensors;

import android.os.Bundle;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;

public class ExternalSensor extends Sensor {

    private final ExternalSensorDevice sensorDevice;

    private final String name;
    private final String part;

    private Timer measurementTimer;
    private TimerTask measurementTimerTask;

    public ExternalSensor(final int id,
                          final SensorType sensorType,
                          final String name,
                          final String part,
                          ExternalSensorDevice sensorDevice) {
        super(id, sensorType);

        this.sensorDevice = sensorDevice;

        this.name = name;
        this.part = part;
    }

    public String getName() {
        return this.name;
    }

    public String getPart() {
        return this.part;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void addMeasurement(final Measurement measurement) {
        measurements.add(measurement);
    }

    public boolean startMeasuring(final int measurementId, final int interval) {
        if (sensorDevice.connected && active) {

            stopMeasuring();

            measurementTimer = new Timer();
            measurementTimerTask = new TimerTask() {
                @Override
                public void run() {
                    float[] value = sensorDevice.CommandHandler.getSensorValue(id, measurementId);

                    Message msg = sensorDevice.messageHandler.obtainMessage(SensorEvent.VALUE, id, measurementId);
                    Bundle msgData = new Bundle();
                    msgData.putFloatArray("VALUE", value);
                    msg.setData(msgData);
                    sensorDevice.messageHandler.sendMessage(msg);
                }
            };

            measuring = true;

            if (interval == 0)
                measurementTimer.schedule(measurementTimerTask, 100);  // One-shot, delayed by 100ms
            else
                measurementTimer.schedule(measurementTimerTask, interval, interval);

            return true;
        } else
            return false;
    }

    public void stopMeasuring() {
        if (measurementTimer != null) {
            measurementTimer.cancel();
            measurementTimer.purge();
            measurementTimer = null;
            measuring = false;
        }
    }
}
