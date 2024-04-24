package indicators.bollinger;

public class BollingerBands {
    private final double[] closingPrices;
    private final int period;

    public BollingerBands(double[] closingPrices, int period) {
        this.closingPrices = closingPrices;
        this.period = period;
    }

    private double calculateSMA(int start, int end) {
        double sum = 0.0;
        for (int i = start; i < end; i++) {
            sum += closingPrices[i];
        }
        return sum / (end - start);
    }

    private double calculateStandardDeviation(int start, int end, double mean) {
        double sum = 0.0;
        for (int i = start; i < end; i++) {
            sum += Math.pow(closingPrices[i] - mean, 2);
        }
        return Math.sqrt(sum / (end - start));
    }

    public double[] calculateBollingerBands(int index) {
        if (index < period - 1) {
            throw new IllegalArgumentException("Not enough data to calculate Bollinger Bands.");
        }
        int start = index - period + 1;
        double mean = calculateSMA(start, index + 1);
        double stdDeviation = calculateStandardDeviation(start, index + 1, mean);

        return new double[] {
                mean, // Middle Band
                mean + 2 * stdDeviation, // Upper Band
                mean - 2 * stdDeviation // Lower Band
        };
    }
}
