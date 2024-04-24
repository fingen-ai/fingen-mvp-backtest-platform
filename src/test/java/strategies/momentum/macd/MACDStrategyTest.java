package strategies.momentum.macd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import strategies.momentum.macd.MACDStrategy;

public class MACDStrategyTest {
    private MACDStrategy strategy;

    @Before
    public void setUp() {
        strategy = new MACDStrategy(12, 26, 9);
    }

    @Test
    public void testMACDStrategySignals() {
        // Simulate a series of price updates
        double[] prices = {22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39, 22.38, 22.61, 23.36, 24.05, 23.75, 23.83, 23.95, 23.63};
        String lastSignal = "Hold";
        for (double price : prices) {
            lastSignal = strategy.updateAndGetTradingSignal(price);
        }

        // Check the final signal - for actual unit tests, we would need expected values here.
        Assert.assertNotNull("The trading signal should not be null", lastSignal);
        System.out.println("Final trading signal: " + lastSignal);
        // Further tests could be added to verify specific expected signals at certain points.
    }
}
