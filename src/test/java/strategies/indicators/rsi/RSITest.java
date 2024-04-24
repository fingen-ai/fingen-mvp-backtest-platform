package strategies.indicators.rsi;

import org.junit.Assert;
import org.junit.Test;
import strategies.indicators.rsi.RSI;

public class RSITest {

    @Test
    public void testRSICalculation() {
        double[] prices = { 44.34, 44.09, 44.15, 43.61, 44.33, 44.83, 45.10, 45.42, 45.84, 46.08, 45.89, 46.03, 45.61, 46.28, 46.28, 46.00 };
        RSI rsiCalculator = new RSI(prices, 14);
        double rsi = rsiCalculator.calculateRSI();

        Assert.assertEquals("Check RSI value", 66.24961855355505, rsi, 0.01);
    }
}
