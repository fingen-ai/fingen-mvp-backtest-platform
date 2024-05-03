package strategies.indicators.emax;

public class EMAImpl implements EMA {

    private final int period;
    private double ema; // to hold the current EMA value
    private boolean isFirstDataPoint = true;

    public EMAImpl(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be greater than zero.");
        }
        this.period = period;
    }

    public double update(double price) {
        if (isFirstDataPoint) {
            ema = price; // Start the EMA with the first price point
            isFirstDataPoint = false;
        } else {
            double alpha = 2.0 / (period + 1);
            ema = price * alpha + ema * (1 - alpha);
        }
        return ema;
    }

    public double getEma() {
        return ema;
    }
}
