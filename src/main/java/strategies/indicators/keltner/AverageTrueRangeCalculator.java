package strategies.indicators.keltner;

public interface AverageTrueRangeCalculator {
    double calculate(double[] high, double[] low, double[] close);
}
