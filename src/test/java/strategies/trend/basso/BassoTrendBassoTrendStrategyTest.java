package strategies.trend.basso;

import org.junit.Assert;
import org.junit.Test;
import strategies.trend.basso.BassoTrendStrategy;
import strategies.trend.basso.BassoTrendStrategyImpl;

public class BassoTrendBassoTrendStrategyTest {

    @Test
    public void testStrategy() {

        BassoTrendStrategy strat = new BassoTrendStrategyImpl();
        String strategyDecision = strat.getStrategyDecision();
        Assert.assertEquals("Neutral", strategyDecision);
    }
}
