package strategies.indicators.bollingerbands;

import strategies.indicators.bollinger.BollingerBands;
import org.junit.Assert;
import org.junit.Test;

public class BollingerBandsTest {

    @Test
    public void testBollingerBandsCalculation() {
        double[] prices = new double[]{22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39, 22.38, 22.61, 23.36, 24.05, 23.75, 23.83, 23.95, 23.63, 23.82, 23.87, 23.65, 23.19, 23.10, 23.33, 22.68, 23.10, 22.40, 22.17};
        System.out.println("Price Array Length: " + prices.length);
        BollingerBands bb = new BollingerBands(prices, 20);
        double[] bands = bb.calculateChannel(29);

        Assert.assertNotNull("Bands should not be null", bands);
        Assert.assertEquals("Middle band should match calculated SMA", 23.170499999999997, bands[0], 0.005);
        Assert.assertEquals("Upper band should be middle band + 2 * SD", 24.43546600744842, bands[1], 0.005);
        Assert.assertEquals("Lower band should be middle band - 2 * SD", 21.905533992551575, bands[2], 0.005);
    }
}
