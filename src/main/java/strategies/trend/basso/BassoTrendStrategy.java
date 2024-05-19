package strategies.trend.basso;

public interface BassoTrendStrategy {

    String getStrategyDecision(double[] prices, double[] high, double[] low, double[] close);
}
