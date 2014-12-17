package in.konstant.sensors;

import in.konstant.R;

public enum SensorType {
    GENERIC("Sensor", R.drawable.ic_generic),
    ACCELERATION("Accelerometer",R.drawable.ic_acceleration),
    ADC("AD Converter", R.drawable.ic_adc),
    AUDIO("Audio Sensor", R.drawable.ic_audio),
    COMPASS("Compass", R.drawable.ic_compass),
    COLOR("Color Sensor", R.drawable.ic_color),
//  CURRENT("Current Sensor", R.drawable.ic_current),
    DISTANCE("Distance Sensor", R.drawable.ic_distance),
    GAS("Gas Sensor", R.drawable.ic_gas),
    HUMIDITY("Hygrometer", R.drawable.ic_humidity),
    INFRARED("Infrared Sensor", R.drawable.ic_infrared),
    LIGHT("Light Sensor", R.drawable.ic_light),
    MAGNETIC("Magnetometer", R.drawable.ic_magnetic),
//  MOTION("Motion Sensor", R.drawable.ic_motion),
    POLARISATION("Polarimeter", R.drawable.ic_polarisation),
    PRESSURE("Pressure Sensor", R.drawable.ic_pressure),
    ROTATION("Gyroscope", R.drawable.ic_rotation),
    RADIATION("Radiation Sensor", R.drawable.ic_radiation),
//  SPECTRAL("Spectrometer", R.drawable.ic_spectral),
    TEMPERATURE("Temperature", R.drawable.ic_temperature);

    private final String name;
    private final int iconId;

    SensorType(final String name, final int iconId) {
        this.name = name;
        this.iconId = iconId;
    }

    public String toString() {
        return this.name;
    }

    public int icon() {
        return this.iconId;
    }

    private static final SensorType[] enumValues = SensorType.values();

    public static SensorType fromInteger(final int id) {
        return enumValues[id];
    }
}