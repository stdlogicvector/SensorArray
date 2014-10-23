package in.konstant.sensors;

public enum BaseUnit {
    NONE,
    METER,
    KILOGRAM,
    SECOND,
    AMPERE,
    KELVIN,
    MOLE,
    CANDELA,
    DEGREE;

    @Override
    public String toString() {
        switch (this) {
            default:
            case NONE:
                return "";
            case METER:
                return "meter";
            case KILOGRAM:
                return "kilogram";
            case SECOND:
                return "second";
            case AMPERE:
                return "ampere";
            case KELVIN:
                return "kelvin";
            case MOLE:
                return "mole";
            case CANDELA:
                return "candela";
            case DEGREE:
                return "degree";
        }
    }

    public String toQuantity() {
        switch (this) {
            default:
            case NONE:
                return "";
            case METER:
                return "length";
            case KILOGRAM:
                return "mass";
            case SECOND:
                return "time";
            case AMPERE:
                return "electric current";
            case KELVIN:
                return "temperature";
            case MOLE:
                return "amount of substance";
            case CANDELA:
                return "luminous intensity";
            case DEGREE:
                return "angle";
        }
    }

    public String toSymbol() {
        switch (this) {
            default:
            case NONE:
                return "";
            case METER:
                return "m";
            case KILOGRAM:
                return "kg";
            case SECOND:
                return "s";
            case AMPERE:
                return "A";
            case KELVIN:
                return "K";
            case MOLE:
                return "mol";
            case CANDELA:
                return "cd";
            case DEGREE:
                return "°";
        }
    }
}