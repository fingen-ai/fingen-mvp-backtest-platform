package strategies.trend.basso;

import org.junit.Assert;
import org.junit.Test;

public class BassoTrendBassoTrendStrategyTest {

    private double[] prices = new double[50];
    private double[] high = new double[50];
    private double[] low = new double[50];
    private double[] close = new double[50];

    @Test
    public void testStrategy() {

        BassoTrendStrategy strat = new BassoTrendStrategyImpl();
        String strategyDecision = strat.getStrategyDecision(prices, high, low, close);
        Assert.assertEquals("Neutral", strategyDecision);
    }
}
