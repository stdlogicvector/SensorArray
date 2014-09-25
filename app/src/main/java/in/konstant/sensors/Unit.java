package in.konstant.sensors;

public class Unit {
    private final String name;
    private final String symbol;
    private final Prefix prefix;
    private final Subunit subunits[];

    public Unit(String name, String symbol, Prefix prefix, Subunit subunits[]) {
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

    @Override
    public String toString() {
        StringBuilder num = new StringBuilder();
        StringBuilder den = new StringBuilder();

        for (int i = 0; i < this.subunits.length; i++) {

            if (this.subunits[i].getExponent() < 0) {
                den.append(this.subunits[i].toString());
                den.append(" ");
            } else if (this.subunits[i].getExponent() > 0)
            {
                num.append(this.subunits[i].toString());
                num.append(" ");
            }
        }

        return (num.toString() + " per " + den.toString());
    }
}