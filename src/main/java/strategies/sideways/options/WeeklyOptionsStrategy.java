package strategies.sideways.options;

import indicators.hurst.HurstExponent;

public class WeeklyOptionsStrategy {
    private TrendStateMap trendStateMap;
    private double[] weeklyPriceData;
    private static final double HURST_THRESHOLD = 0.5; // Ideal threshold for non-trending market
    private static final double HURST_MARGIN = 0.1; // Adjusting margin for more flexibility

    public WeeklyOptionsStrategy(TrendStateMap trendStateMap, double[] weeklyPriceData) {
        this.trendStateMap = trendStateMap;
        this.weeklyPriceData = weeklyPriceData;
    }

    public String execute() {
        // Calculate the Hurst Exponent for the provided price data
        double hurstExponent = HurstExponent.calculateHurstExponent(weeklyPriceData);

        // Determine if the market is trending or sideways with adjusted margins
        boolean isSidewaysMarket = hurstExponent >= (HURST_THRESHOLD - HURST_MARGIN) &&
                hurstExponent <= (HURST_THRESHOLD + HURST_MARGIN);

        // Get the most recent trend state
        String recentTrend = trendStateMap.getMostRecentTrendState();

        if (isSidewaysMarket) {
            // Decide which options spread to use based on the most recent trend state
            if ("Bearish".equals(recentTrend)) {
                return "Bear Call Spread executed for 7 days.";
            } else if ("Bullish".equals(recentTrend)) {
                return "Bull Put Spread executed for 7 days.";
            }
        }
        return "No options spread executed this week.";
    }
}
