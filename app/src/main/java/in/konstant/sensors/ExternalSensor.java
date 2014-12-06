package in.konstant.sensors;

public class ExternalSensor extends Sensor {

    private final int id;
    private final String name;
    private final String part;

    public ExternalSensor(final int id, final Type type, final String name, final String part) {
        super();

        this.id = id;
        this.type = type;
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

    public float[] getValue(final int id) {

        return null;
    }

    public void addMeasurement(final Measurement measurement) {
        mMeasurements.add(measurement);
    }
}
