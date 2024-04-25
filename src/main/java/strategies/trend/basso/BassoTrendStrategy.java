package strategies.trend.basso;

import core.service.strategy.StrategyData;

public interface BassoTrendStrategy {

    String getStrategyDecision(double[] prices, double[] high, double[] low, double[] close);
}
