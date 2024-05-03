package risk;

import org.junit.Assert;
import org.junit.Test;

public class RiskTest {

    @Test
    public void testRisk() {
        Risk risk = new RiskImpl();

        double initialRiskPercentThreshold = risk.getInitRiskPercentThreshold();
        Assert.assertEquals(initialRiskPercentThreshold, 0.5, 0);

        double initialVolPercentThreshold = risk.getInitVolPercentThreshold();
        Assert.assertEquals(initialVolPercentThreshold, 0.3, 0);

        double ongoingRiskPercentThreshold = risk.getOngoingRiskPercentThreshold();
        Assert.assertEquals(ongoingRiskPercentThreshold, 1.0, 0);

        double ongoingVolPercentThreshold = risk.getOngoingVolPercentThreshold();
        Assert.assertEquals(ongoingVolPercentThreshold, 0.4, 0);

        double positionRisk = 0;
        double equity = 0;
        double currentTotalRiskPercent = risk.getCurrentTotalPercentRisk(positionRisk, equity);
        Assert.assertEquals(currentTotalRiskPercent, 16.0, 0);

        double atr = 0;
        equity = 0;
        double currentTotalVolPercentRiskPercent = risk.getCurrentTotalVolPercentRisk(atr, equity);
        Assert.assertEquals(currentTotalVolPercentRiskPercent, 6.0, 0);
    }
}
