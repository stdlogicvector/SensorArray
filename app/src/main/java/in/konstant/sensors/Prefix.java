package in.konstant.sensors;

public enum Prefix {
    PICO,
    NANO,
    MICRO,
    MILLI,
    CENTI,
    DECI,
    NO_PREFIX,
    DECA,
    HECTO,
    KILO,
    MEGA,
    GIGA,
    TERA;

    @Override
    public String toString() {
        switch (this) {
            case NO_PREFIX:
                return "";
            default:
                return this.name();
        }
    }

    public String toSymbol() {
        switch (this) {
            default:
            case NO_PREFIX:
                return "";
            case PICO:
                return "p";
            case NANO:
                return "n";
            case MICRO:
                return "µ";
            case MILLI:
                return "m";
            case CENTI:
                return "c";
            case DECI:
                return "d";
            case DECA:
                return "D";
            case HECTO:
                return "H";
            case KILO:
                return "k";
            case MEGA:
                return "M";
            case GIGA:
                return "G";
            case TERA:
                return "T";
        }
    }
}