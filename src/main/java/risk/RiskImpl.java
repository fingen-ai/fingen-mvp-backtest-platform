package risk;

public class RiskImpl implements Risk {

    // risk thresholds
    double initRiskPercentThreshold = 0.5;
    double initVolPercentThreshold = 0.3;
    double ongoingRiskPercentThreshold = 1;
    double ongoingVolPercentThreshold = 0.4;

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

    public double getCurrentTotalPercentRisk(double position, double nav) {
        return position/nav;
    }

    public double getCurrentTotalVolPercentRisk(double atr, double nav) {
        return atr/nav;
    }

    public double updateOngoingRiskAmtThreshold(double nav, double ongoingRiskPercent) {
        return ongoingRiskPercent*nav;
    }

    public double updateOngoingVolAmtThreshold(double nav, double ongoingVolPercent) {
        return ongoingVolPercent*nav;
    }
}
