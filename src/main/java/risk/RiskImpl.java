package risk;

public class RiskImpl implements Risk {

    // risk thresholds
    double initRiskPercentThreshold = 0.5;
    double initVolPercentThreshold = 0.3;
    double ongoingRiskPercentThreshold = 1;
    double ongoingVolPercentThreshold = 0.4;
    double currentTotalPercentRisk = 16;
    double currentTotalVolPercentRisk = 6;

    public double getInitRiskPercentThreshold() {
        return initRiskPercentThreshold;
    }

    public double getInitVolPercentThreshold() {
        return initVolPercentThreshold;
    }

    public double getOngoingRiskPercentThreshold() {
        return ongoingRiskPercentThreshold;
    }

    public double getOngoingVolPercentThreshold() {
        return ongoingVolPercentThreshold;
    }

    public double getCurrentTotalPercentRisk(double positionRisk, double equity) {
        return currentTotalPercentRisk = positionRisk/equity;
    }

    public double getCurrentTotalVolPercentRisk(double atr, double equity) {
        return currentTotalVolPercentRisk = atr/equity;
    }
}
