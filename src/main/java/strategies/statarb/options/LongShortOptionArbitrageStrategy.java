package strategies.statarb.options;

public class LongShortOptionArbitrageStrategy {
    private double stockPrice;
    private double strikePrice;
    private double timeToExpiration; // In years
    private double riskFreeRate; // Annual interest rate
    private double volatility; // Annual volatility

    public LongShortOptionArbitrageStrategy(double stockPrice, double strikePrice, double timeToExpiration, double riskFreeRate, double volatility) {
        this.stockPrice = stockPrice;
        this.strikePrice = strikePrice;
        this.timeToExpiration = timeToExpiration;
        this.riskFreeRate = riskFreeRate;
        this.volatility = volatility;
    }

    public String evaluateOptionTrade(double marketPriceOfOption) {
        double theoreticalPrice = BlackScholesModel.blackScholesCallPrice(stockPrice, strikePrice, timeToExpiration, riskFreeRate, volatility);

        if (marketPriceOfOption < theoreticalPrice) {
            return "Buy Call - Market price " + marketPriceOfOption + " is underpriced compared to theoretical price " + theoreticalPrice;
        } else if (marketPriceOfOption > theoreticalPrice) {
            return "Sell Call - Market price " + marketPriceOfOption + " is overpriced compared to theoretical price " + theoreticalPrice;
        } else {
            return "Hold - Option price is aligned with theoretical price";
        }
    }
}
