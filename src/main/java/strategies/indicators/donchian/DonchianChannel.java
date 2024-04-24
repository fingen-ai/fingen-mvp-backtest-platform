package strategies.indicators.donchian;

public class DonchianChannel {
    private final double[] prices;
    private final int period;

    public DonchianChannel(double[] prices, int period) {
        if (prices == null || prices.length == 0 || period <= 0) {
            throw new IllegalArgumentException("Invalid parameters for Donchian Channel calculation.");
        }
        this.prices = prices;
        this.period = period;
    }

    public double[] calculateChannel(int index) {
        if (index < period - 1) {
            throw new IllegalArgumentException("Not enough data points to calculate Donchian Channel.");
        }

        int start = index - period + 1;
        double maxHigh = Double.MIN_VALUE;
        double minLow = Double.MAX_VALUE;

        for (int i = start; i <= index; i++) {
            double price = prices[i];
            if (price > maxHigh) {
                maxHigh = price;
            }
            if (price < minLow) {
                minLow = price;
            }
        }

        return new double[] { maxHigh, minLow };
    }
}
