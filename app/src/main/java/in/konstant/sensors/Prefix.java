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
            default:
            case NO_PREFIX:
                return "";
            case PICO:
                return "pico";
            case NANO:
                return "nano";
            case MICRO:
                return "micro";
            case MILLI:
                return "milli";
            case CENTI:
                return "centi";
            case DECI:
                return "deci";
            case DECA:
                return "deca";
            case HECTO:
                return "hecto";
            case KILO:
                return "kilo";
            case MEGA:
                return "mega";
            case GIGA:
                return "giga";
            case TERA:
                return "tera";
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