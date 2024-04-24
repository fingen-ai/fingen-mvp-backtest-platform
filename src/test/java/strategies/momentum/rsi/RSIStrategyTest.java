package strategies.momentum.rsi;

import org.junit.Assert;
import org.junit.Test;
import strategies.momentum.rsi.RSIStrategy;

public class RSIStrategyTest {

    @Test
    public void testRsiStrategySignals() {
        double[] prices = {44.34, 44.09, 44.15, 43.61, 44.33, 44.83, 45.10, 45.42, 45.84, 46.08, 45.89, 46.03, 45.61, 46.28, 46.28, 46.00, 45.77, 46.03, 45.65, 45.25, 45.23, 46.20, 45.77, 46.42, 46.43, 46.23};
        RSIStrategy strategy = new RSIStrategy(prices, 14, 70, 30);
        String signal = strategy.generateSignal();

        Assert.assertNotNull("Signal should not be null", signal);
        // Example checks (assuming we know the expected signal; "Buy" or "Sell" depends on RSI values calculated)
        //Assert.assertEquals("Signal should be 'Sell' for overbought", "Sell", signal);
        //Assert.assertEquals("Signal should be 'Buy' for oversold", "Buy", signal);
        Assert.assertEquals("Signal should be 'Hold'", "Hold", signal);
    }
}
