package in.konstant.sensors;

import in.konstant.R;

public enum MeasurementType {
    GENERIC("Measurement", R.drawable.ic_generic),
    ACCELERATION("Acceleration",R.drawable.ic_acceleration),
    ANGLE("Angle", R.drawable.ic_polarisation),
    ANGULAR_SPEED("Angular speed", R.drawable.ic_rotation),
    BRIGHTNESS("Brightness", R.drawable.ic_light),
    COLOR("Color", R.drawable.ic_color),
    CONCENTRATION("Concentration", R.drawable.ic_gas),
    COUNTS("Counts", R.drawable.ic_adc),
    DIRECTION("Direction", R.drawable.ic_compass),
    DISTANCE("Distance", R.drawable.ic_distance),
    //  CURRENT("Current", R.drawable.ic_current),
    FORCE("Force", R.drawable.ic_pressure),
    HUMIDITY("Humidity", R.drawable.ic_humidity),
    LOUDNESS("Loudness", R.drawable.ic_audio),
    FLUX("Flux", R.drawable.ic_magnetic),
    TEMPERATURE("Temperature", R.drawable.ic_temperature),
    //  VOLTAGE("Voltage", R.drawable.ic_voltage);
    ;

    private final String name;
    private final int iconId;

    MeasurementType(final String name, final int iconId) {
        this.name = name;
        this.iconId = iconId;
    }

    public String toString() {
        return this.name;
    }

    public int icon() {
        return this.iconId;
    }

    private static final MeasurementType[] enumValues = MeasurementType.values();

    public static MeasurementType fromInteger(final int id) {
        return enumValues[id];
    }
}