package indicators.rsi;

public class RSI {
    private final double[] priceChanges;
    private final int period;

    public RSI(double[] prices, int period) {
        if (prices == null || prices.length <= period) {
            throw new IllegalArgumentException("Prices array too short relative to the period.");
        }
        this.period = period;
        this.priceChanges = calculateChanges(prices);
    }

    private double[] calculateChanges(double[] prices) {
        double[] changes = new double[prices.length - 1];
        for (int i = 1; i < prices.length; i++) {
            changes[i - 1] = prices[i] - prices[i - 1];
        }
        return changes;
    }

    public double calculateRSI() {
        double averageGain = 0;
        double averageLoss = 0;

        // First calculate initial averages of gains and losses
        for (int i = 0; i < period; i++) {
            if (priceChanges[i] > 0) {
                averageGain += priceChanges[i];
            } else {
                averageLoss -= priceChanges[i];
            }
        }
        averageGain /= period;
        averageLoss /= period;

        // Smooth the averages
        for (int i = period; i < priceChanges.length; i++) {
            if (priceChanges[i] > 0) {
                averageGain = (averageGain * (period - 1) + priceChanges[i]) / period;
                averageLoss = (averageLoss * (period - 1)) / period;
            } else {
                averageGain = (averageGain * (period - 1)) / period;
                averageLoss = (averageLoss * (period - 1) - priceChanges[i]) / period;
            }
        }

        double rs = averageGain / averageLoss;
        return 100 - (100 / (1 + rs));
    }
}
