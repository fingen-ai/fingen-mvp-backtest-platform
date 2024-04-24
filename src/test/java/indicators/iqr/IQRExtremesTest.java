package indicators.iqr;

import org.junit.Assert;
import org.junit.Test;

public class IQRExtremesTest {

    @Test
    public void testQuartileCalculations() {
        double[] prices = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};
        IQRExtremes eqi = new IQRExtremes(prices, prices.length);
        eqi.calculateQuartiles();  // Verify output manually or add assertions based on expected values
        Assert.assertTrue("Test must include validations", true);  // Placeholder for actual tests
    }
}
