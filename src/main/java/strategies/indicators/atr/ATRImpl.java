package strategies.indicators.atr;

import java.util.LinkedList;
import java.util.Queue;

public class ATRImpl implements ATR {
    private final int period;
    private final Queue<Double> trValues = new LinkedList<>();
    private double atr = 0.0;

    public ATRImpl(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be greater than zero.");
        }
        this.period = period;
    }

    // This method updates ATR with new high, low, and close values
    public double update(double high, double low, double close, double previousClose) {
        double tr = calculateTR(high, low, previousClose);
        if (trValues.size() == period) {
            atr = (atr * period - trValues.poll() + tr) / period;
        } else {
            trValues.offer(tr);
            atr = (atr * (trValues.size() - 1) + tr) / trValues.size();
        }
        return atr;
    }

    public double calculateTR(double high, double low, double previousClose) {
        return Math.max(high - low, Math.max(Math.abs(high - previousClose), Math.abs(low - previousClose)));
    }
}
