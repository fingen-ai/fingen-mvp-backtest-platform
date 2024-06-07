package performance;

import org.junit.Assert;
import org.junit.Test;

public class PerformanceTest {

    @Test
    public void testPerformance() {

        double[] drawdowns = new double[0]; // call DD svc
        Performance performance = new PerformanceImpl(drawdowns);
        double cagrPercentage = performance.getCAGRPercentage();
        double sharpeRatio = performance.getSharpeRatio();
        double sortinoRatio = performance.getSortinoRatio();
        double returnToAvgDrawdown = performance.getReturnToAvgDrawdown();
        double marRatio = performance.getMARRatio();
        double maxDrawdownPercentage = performance.getMaxDrawdownPercentage();
        double winCount = performance.getWinCount();
        double lossCount = performance.getLossCount();
        double reliabilityPercentage = performance.getWinPercent();
        double totalProfit = performance.getTotalProfit();
        double profitFactor = performance.getProfitFactor();

        Assert.assertEquals(0.6270686999999999, cagrPercentage , 0);
        Assert.assertEquals(0.27558912730477536, sharpeRatio, 0);
        Assert.assertEquals(0.5366563145999499, sortinoRatio, 0);
        Assert.assertEquals(0, returnToAvgDrawdown, 0);
        Assert.assertEquals(0.52255725, marRatio, 0);
        Assert.assertEquals(1.2, maxDrawdownPercentage, 0);
        Assert.assertEquals(3.00, winCount, 0);
        Assert.assertEquals(2.0, lossCount, 0);
        Assert.assertEquals(0.6, reliabilityPercentage, 0);
        Assert.assertEquals(650, totalProfit, 0);
        Assert.assertEquals(3.0952380952380953, profitFactor, 0);


    }
}