package strategies.indicators.donchian;

import org.junit.Assert;
import org.junit.Test;
import strategies.indicators.donchian.DonchianChannel;

public class DonchianChannelTest {

    @Test
    public void testDonchianChannelCalculation() {
        double[] prices = new double[]{10.0, 10.5, 11.0, 10.75, 11.5, 11.3, 10.9, 12.0, 11.8, 11.2, 10.6, 10.2, 11.9, 11.5, 12.1};
        DonchianChannel dc = new DonchianChannel(prices, 5);

        double[] channel = dc.calculateChannel(14);

        Assert.assertNotNull("Channel should not be null", channel);
        Assert.assertEquals("Highest high over the last 5 periods should be 12.1", 12.1, channel[0], 0.001);
        Assert.assertEquals("Lowest low over the last 5 periods should be 10.6", 10.2, channel[1], 0.001);
    }
}
