package strategies.indicators.emax;

public class FiftyDayEMA {

    private static final int PERIOD = 50;
    private final double smoothingFactor;

    public FiftyDayEMA() {
        this.smoothingFactor = 2.0 / (PERIOD + 1);
    }

    public double calculateEMA(double previousEMA, double currentPrice) {
        return (currentPrice - previousEMA) * smoothingFactor + previousEMA;
    }
}
