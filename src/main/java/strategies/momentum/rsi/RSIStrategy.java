package strategies.momentum.rsi;

public class RSIStrategy {
    private double[] prices = new double[0];
    private int period = 0;
    private double overboughtThreshold = 0;
    private double oversoldThreshold = 0;

    public RSIStrategy(double[] prices, int period, double overboughtThreshold, double oversoldThreshold) {
        this.prices = prices;
        this.period = period;
        this.overboughtThreshold = overboughtThreshold;
        this.oversoldThreshold = oversoldThreshold;
    }

    private double calculateRSI() {
        double averageGain = 0;
        double averageLoss = 0;

        // Calculate initial averages of gains and losses
        for (int i = 1; i <= period; i++) {
            double change = prices[i] - prices[i - 1];
            if (change > 0) {
                averageGain += change;
            } else {
                averageLoss -= change;
            }
        }
        averageGain /= period;
        averageLoss /= period;

        // Calculate smoothed averages
        for (int i = period + 1; i < prices.length; i++) {
            double change = prices[i] - prices[i - 1];
            if (change > 0) {
                averageGain = (averageGain * (period - 1) + change) / period;
                averageLoss = (averageLoss * (period - 1)) / period;
            } else {
                averageGain = (averageGain * (period - 1)) / period;
                averageLoss = (averageLoss * (period - 1) - change) / period;
            }
        }

        double rs = averageGain / averageLoss;
        return 100 - (100 / (1 + rs));
    }

    public String generateSignal() {
        double rsi = calculateRSI();
        if (rsi >= overboughtThreshold) {
            return "Sell";
        } else if (rsi <= oversoldThreshold) {
            return "Buy";
        }
        return "Hold";
    }
}
