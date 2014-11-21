package in.konstant.sensors;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Subunit {
    private final BaseUnit baseunit;
    private final int exponent;

    public Subunit(final BaseUnit baseunit, final int exponent) {
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

    public static Subunit[] fromString(final String unitString) {
        ArrayList<Subunit> subunits = new ArrayList<Subunit>();

        String[] subUnitString = unitString.split("[;]");

        for (int s = 0; s < subUnitString.length; ++s) {
            subunits.add(new Subunit(
                            BaseUnit.fromInteger(subUnitString[s].charAt(0) - '0'),
                            Integer.parseInt(subUnitString[s].substring(2, 4).trim()))
            );
        }
        return subunits.toArray(new Subunit[subunits.size()]);
    }
}