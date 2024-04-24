package strategies.indicators.emax;

public class TenDayEMA {

    private static final int PERIOD = 10;
    private final double smoothingFactor;

    public TenDayEMA() {
        this.smoothingFactor = 2.0 / (PERIOD + 1);
    }

    public double calculateEMA(double previousEMA, double currentPrice) {
        return (currentPrice - previousEMA) * smoothingFactor + previousEMA;
    }
}
