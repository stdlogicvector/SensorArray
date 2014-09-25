package in.konstant.sensors;

public class Subunit {
    private final BaseUnit baseunit;
    private final int exponent;

    public Subunit(BaseUnit baseunit, int exponent) {
        this.baseunit = baseunit;
        this.exponent = exponent;
    }

    public BaseUnit getBaseUnit() {
        return this.baseunit;
    }

    public int getExponent() {
        return this.exponent;
    }

    public String toSymbol() {
        switch (this.exponent) {
            case 1:
                return this.baseunit.toSymbol();
            case 2:
                return (this.baseunit.toSymbol() + "²");
            case 3:
                return (this.baseunit.toSymbol() + "³");
            default:
                return (baseunit.toSymbol() + "^" + exponent);
        }
    }

    @Override
    public String toString() {
        switch (this.exponent) {
            case 1:
                return this.baseunit.toString();
            case 2:
                return ("square-" + this.baseunit.toString());
            case 3:
                return ("cubic-" + this.baseunit.toString());
            default:
                return (this.baseunit.toString() + "^" + this.exponent);
        }
    }
}