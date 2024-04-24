package strategies.statarb.price.meanrevert.iqr;

import strategies.indicators.iqr.IQRExtremes;

public class IQRExtremesReversionStrategy {
    private double[] prices;
    private int period;

    public IQRExtremesReversionStrategy(double[] prices, int period) {
        this.prices = prices;
        this.period = period;
    }

    public String evaluateTrade() {
        if (prices.length < period) {
            return "Not enough data";
        }

        IQRExtremes iqr = new IQRExtremes(prices, period);
        iqr.calculateQuartiles(); // This method currently only prints values, need return values for logic.

        double currentPrice = prices[prices.length - 1];
        double lowerExtreme = iqr.getLowerExtreme();
        double upperExtreme = iqr.getUpperExtreme();
        double midPoint = iqr.getMidPoint();

        if (currentPrice > upperExtreme) {
            return "Short at " + currentPrice + " - Expecting mean reversion to " + midPoint;
        } else if (currentPrice < lowerExtreme) {
            return "Long at " + currentPrice + " - Expecting mean reversion to " + midPoint;
        } else {
            return "No trade - Price within normal range";
        }
    }
}
