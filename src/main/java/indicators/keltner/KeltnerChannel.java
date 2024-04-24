package indicators.keltner;

public class KeltnerChannel {
    private final MovingAverageCalculator maCalculator;
    private final AverageTrueRangeCalculator atrCalculator;
    private final double multiplier;

    public KeltnerChannel(MovingAverageCalculator maCalculator, AverageTrueRangeCalculator atrCalculator, double multiplier) {
        this.maCalculator = maCalculator;
        this.atrCalculator = atrCalculator;
        this.multiplier = multiplier;
    }

    public double[] calculateChannel(double[] prices, double[] high, double[] low, double[] close) {
        double ema = maCalculator.calculate(prices);
        double atr = atrCalculator.calculate(high, low, close);

        double upperBand = ema + multiplier * atr;
        double lowerBand = ema - multiplier * atr;

        return new double[] {ema, upperBand, lowerBand};
    }
}
