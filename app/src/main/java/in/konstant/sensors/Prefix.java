package in.konstant.sensors;

public enum Prefix {
    PICO("pico", "p"),
    NANO("nano", "n"),
    MICRO("micro", "µ"),
    MILLI("milli", "m"),
    CENTI("centi", "c"),
    DECI("deci", "d"),
    NO_PREFIX("", ""),
    DECA("deca", "D"),
    HECTO("hecto", "H"),
    KILO("kilo", "k"),
    MEGA("mega", "M"),
    GIGA("giga", "G"),
    TERA("tera", "T");

    private final String string;
    private final String symbol;

    Prefix(final String string, final String symbol) {
        this.string = string;
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return this.string;
    }

    public String toSymbol() {
        return this.symbol;
    }

    private static final Prefix[] enumValues = Prefix.values();

    public static Prefix fromInteger(final int id) {
        return enumValues[id];
    }
}