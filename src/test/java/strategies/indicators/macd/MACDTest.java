package strategies.indicators.macd;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import strategies.indicators.macd.MACD;

public class MACDTest {
    private MACD macd;

    @Before
    public void setUp() {
        // Initialize MACD with typical settings for trading: 12, 26, 9
        macd = new MACD(12, 26, 9);
    }

    @Test
    public void testInitialValues() {
        // The very first update should initialize EMAs to the price itself
        double initialPrice = 100.0;
        macd.updatePrice(initialPrice);
        Assert.assertEquals("Initial MACD should be 0", 0.0, macd.getMACD(), 0.001);
        Assert.assertEquals("Initial Signal Line should be 0", 0.0, macd.getSignalLine(), 0.001);
    }

    @Test
    public void testSequentialUpdates() {
        //double[] prices = {100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0};
        double[] prices = new double[]{22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39, 22.38, 22.61, 23.36, 24.05, 23.75, 23.83, 23.95, 23.63, 23.82, 23.87, 23.65, 23.19, 23.10, 23.33, 22.68, 23.10, 22.40, 22.17};
        // Sequentially update prices
        for (double price : prices) {
            macd.updatePrice(price);
        }

        Assert.assertEquals("MACD after sequential updates", 0.024876889902010646, macd.getMACD(), 0.001);
        Assert.assertEquals("Signal Line after sequential updates", 0.202482831471189, macd.getSignalLine(), 0.001);
    }

    @Test
    public void testPriceFluctuations() {
        // Simulate price going up and down
        //double[] prices = {100.0, 105.0, 100.0, 105.0, 100.0};
        double[] prices = new double[]{22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39, 22.38, 22.61, 23.36, 24.05, 23.75, 23.83, 23.95, 23.63, 23.82, 23.87, 23.65, 23.19, 23.10, 23.33, 22.68, 23.10, 22.40, 22.17};
        for (double price : prices) {
            macd.updatePrice(price);
        }

        System.out.println("Signal Line: " + macd.getSignalLine());
        System.out.println("MACD Line: " + macd.getMACD());

        // Check that MACD and signal line adjust to fluctuations
        Assert.assertTrue("MACD should adjust to price fluctuations", Math.abs(macd.getMACD()) > 0);
        Assert.assertTrue("Signal Line should adjust to price fluctuations", Math.abs(macd.getSignalLine()) > 0);
    }
}
