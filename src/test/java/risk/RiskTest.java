package risk;

import org.junit.Assert;
import org.junit.Test;

public class RiskTest {

    @Test
    public void testRisk() {
        Risk risk = new RiskImpl();

        double initialRiskThreshold = risk.getInitRiskThreshold();
        Assert.assertEquals(initialRiskThreshold, 0.5, 0);

        double initialVolThreshold = risk.getInitVolThreshold();
        Assert.assertEquals(initialVolThreshold, 0.3, 0);

        double ongoingRiskThreshold = risk.getOngoingRiskThreshold();
        Assert.assertEquals(ongoingRiskThreshold, 1.0, 0);

        double ongoingVolThreshold = risk.getOngoingVolThreshold();
        Assert.assertEquals(ongoingVolThreshold, 0.4, 0);

        double totalRiskThreshold = risk.getTotalRiskThreshold();
        Assert.assertEquals(totalRiskThreshold, 16.0, 0);

        double totalVolThreshold = risk.getTotalVolThreshold();
        Assert.assertEquals(totalVolThreshold, 6.0, 0);
    }
}
