package in.konstant.sensors;

public enum BaseUnit {
    NONE("", "", ""),
    METER("meter", "length", "m"),
    KILOGRAM("kilogram", "mass", "kg"),
    SECOND("second", "time", "s"),
    AMPERE("ampere", "electric current", "A"),
    KELVIN("kelvin", "temperature", "K"),
    MOLE("mole", "amount of substance", "mol"),
    CANDELA("candela", "luminous intensity", "cd"),
    DEGREE("degree", "angle", "°");

    final private String string;
    final private String quantity;
    final private String symbol;

    BaseUnit(final String string, final String quantity, final String symbol) {
        this.string = string;
        this.quantity = quantity;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public String toQuantity() {
        return this.quantity;
    }

    public String toSymbol() {
        return this.symbol;
    }
}