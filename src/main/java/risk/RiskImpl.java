package risk;

public class RiskImpl implements Risk {

    // risk thresholds
    double initRiskThreshold = 0.5;
    double initVolThreshold = 0.3;
    double ongoingRiskThreshold = 1;
    double ongoingVolThreshold = 0.4;
    double totalRiskThreshold = 16;
    double totalVolThreshold = 6;

    public double getInitRiskThreshold() {
        return initRiskThreshold;
    }

    public double getInitVolThreshold() {
        return initVolThreshold;
    }

    public double getOngoingRiskThreshold() {
        return ongoingRiskThreshold;
    }

    public double getOngoingVolThreshold() {
        return ongoingVolThreshold;
    }

    public double getTotalRiskThreshold() {
        return totalRiskThreshold;
    }

    public double getTotalVolThreshold() {
        return totalVolThreshold;
    }
}
