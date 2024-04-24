package strategies.statarb.price.meanrevert.iqr;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class IQRExtremesReversionStrategyTest {
    @Test
    public void testShortScenario() {
        double[] prices = {100, 115, 120, 125, 130, 135, 140, 145, 150, 155, 160};
        IQRExtremesReversionStrategy strategy = new IQRExtremesReversionStrategy(prices, 10);
        assertEquals("Short at 160.0 - Expecting mean reversion to 140.0", strategy.evaluateTrade());
    }

    @Test
    public void testLongScenario() {
        double[] prices = {160, 155, 150, 145, 140, 135, 130, 125, 120, 115, 110};
        IQRExtremesReversionStrategy strategy = new IQRExtremesReversionStrategy(prices, 10);
        assertEquals("Long at 110.0 - Expecting mean reversion to 135.0", strategy.evaluateTrade());
    }

    @Test
    public void testNoTradeScenario() {
        double[] prices = {110, 115, 120, 125, 130, 135, 140, 135, 130, 125, 120};
        IQRExtremesReversionStrategy strategy = new IQRExtremesReversionStrategy(prices, 10);
        assertEquals("No trade - Price within normal range", strategy.evaluateTrade());
    }
}
