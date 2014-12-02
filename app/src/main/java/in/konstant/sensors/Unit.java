package in.konstant.sensors;

public class Unit {
    private final String name;
    private final String symbol;
    private final Prefix prefix;
    private final Subunit subunits[];

    public Unit(final String name, final String symbol, final Prefix prefix, final Subunit subunits[]) {
        this.name = name;
        this.symbol = symbol;
        this.prefix = prefix;
        this.subunits = subunits;
    }

    public String getName() {
        return this.name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public Prefix getPrefix() {
        return this.prefix;
    }

    public boolean noPrefix() {
        return  (this.prefix == Prefix.NO_PREFIX);
    }

    public String getSIUnit(final boolean text) {
        StringBuilder num = new StringBuilder();
        StringBuilder den = new StringBuilder();

        for (int i = 0; i < this.subunits.length; i++) {

            if (this.subunits[i].getExponent() < 0) {
                if (text) {
                    den.append(this.subunits[i].toString());
                    den.append(" ");
                } else {
                    den.append(this.subunits[i].toSymbol());
                }
            } else if (this.subunits[i].getExponent() > 0)
            {
                if (text) {
                    num.append(this.subunits[i].toString());
                    num.append(" ");
                } else {
                    num.append(this.subunits[i].toSymbol());
                }
            }
        }

        if (den.length() > 0) {
            if (text)
                return (num.toString() + " per " + den.toString());
            else
                return ("(" + num.toString() + ")/(" + den.toString() + ")");
        } else
            return num.toString();
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(final boolean text) {
        if (text) {
            return this.prefix.toString() + this.name;
        } else {
            return this.prefix.toSymbol() + this.symbol;
        }
    }
}