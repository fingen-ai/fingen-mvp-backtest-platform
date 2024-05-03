package strategies.indicators.emax;

public class EMACrossoverImpl implements EMACrossover {
    private EMA shortTermEma;
    private EMA longTermEma;
    private double previousShortTermEma;
    private double previousLongTermEma;

    public EMACrossoverImpl(int shortTermPeriod, int longTermPeriod) {
        shortTermEma = new EMAImpl(shortTermPeriod);
        longTermEma = new EMAImpl(longTermPeriod);
    }

    public boolean update(double price) {
        previousShortTermEma = shortTermEma.getEma();
        previousLongTermEma = longTermEma.getEma();

        double newShortTermEma = shortTermEma.update(price);
        double newLongTermEma = longTermEma.update(price);

        // Check if crossover happened
        return (previousShortTermEma < previousLongTermEma && newShortTermEma > newLongTermEma)
                || (previousShortTermEma > previousLongTermEma && newShortTermEma < newLongTermEma);
    }
}
