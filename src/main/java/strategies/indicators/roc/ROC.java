package strategies.indicators.roc;

import java.util.LinkedList;
import java.util.Queue;

public class ROC {
    private final int period;
    private final Queue<Double> priceHistory = new LinkedList<>();
    private double roc = 0.0;

    public ROC(int period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Period must be greater than zero.");
        }
        this.period = period;
    }

    public double update(double currentPrice) {
        priceHistory.add(currentPrice);
        if (priceHistory.size() > period) {
            double nPeriodsAgoPrice = priceHistory.poll();
            roc = ((currentPrice - nPeriodsAgoPrice) / nPeriodsAgoPrice) * 100;
        }
        return roc;
    }

    public double getRoc() {
        return roc;
    }
}
