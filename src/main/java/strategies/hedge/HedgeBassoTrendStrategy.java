package strategies.hedge;

import strategies.trend.basso.BassoTrendStrategyImpl;

public class HedgeBassoTrendStrategy {
    private double totalLongPortfolio; // The total value of the long portfolio
    private double hedgePosition;
    private boolean isHedged;
    private BassoTrendStrategyImpl trendStrategy;

    public HedgeBassoTrendStrategy(double totalLongPortfolio) {
        this.totalLongPortfolio = totalLongPortfolio;
        this.hedgePosition = 0;
        this.isHedged = false;
        this.trendStrategy = new BassoTrendStrategyImpl();
    }

    public void updateHedge(double[] prices, double[] high, double[] low, double[] close) {

        String trendDecision = trendStrategy.getStrategyDecision(prices, high, low, close);

        // Calculate the current price's 21-day EMA
        double currentEMA = calculateEMA(prices);

        if ("Bearish".equals(trendDecision) && !isHedged) {
            // Enter hedge position
            hedgePosition = totalLongPortfolio;
            isHedged = true;
            System.out.println("Hedged with position: " + hedgePosition);
        } else if (isHedged && prices[prices.length - 1] > currentEMA) {
            // Exit hedge position if current price crosses above the 21-day EMA
            isHedged = false;
            hedgePosition = 0;
            System.out.println("Exited hedge as price crossed above the 21-day EMA.");
        }
    }

    private double calculateEMA(double[] prices) {
        double ema = prices[0]; // starting point
        double alpha = 2.0 / (21 + 1.0); // smoothing factor
        for (int i = 1; i < prices.length; i++) {
            ema = alpha * prices[i] + (1 - alpha) * ema;
        }
        return ema;
    }

    public double getHedgePosition() {
        return hedgePosition;
    }

    public boolean isHedged() {
        return isHedged;
    }
}
