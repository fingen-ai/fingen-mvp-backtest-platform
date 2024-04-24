package indicators.keltner;

public class SimpleAverageTrueRangeCalculator implements AverageTrueRangeCalculator {
    private final int period;

    public SimpleAverageTrueRangeCalculator(int period) {
        this.period = period;
    }

    @Override
    public double calculate(double[] high, double[] low, double[] close) {
        double[] tr = new double[high.length];
        for (int i = 0; i < high.length; i++) {
            tr[i] = Math.max(high[i] - low[i], Math.max(Math.abs(high[i] - close[i]), Math.abs(low[i] - close[i])));
        }

        double atr = tr[0];
        for (int i = 1; i < tr.length; i++) {
            atr = (atr * (period - 1) + tr[i]) / period;
        }
        return atr;
    }
}
