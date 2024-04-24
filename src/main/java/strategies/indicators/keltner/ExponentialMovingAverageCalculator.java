package strategies.indicators.keltner;

public class ExponentialMovingAverageCalculator implements MovingAverageCalculator {
    private final int period;

    public ExponentialMovingAverageCalculator(int period) {
        this.period = period;
    }

    @Override
    public double calculate(double[] prices) {
        double ema = prices[0];
        double alpha = 2.0 / (period + 1.0);
        for (int i = 1; i < prices.length; i++) {
            ema = alpha * prices[i] + (1 - alpha) * ema;
        }
        return ema;
    }
}

