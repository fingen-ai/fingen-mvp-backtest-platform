package risk;

public interface Risk {

    double getInitRiskPercentThreshold();

    double getInitVolPercentThreshold();

    double getOngoingRiskPercentThreshold();

    double getOngoingVolPercentThreshold();

    double getCurrentTotalPercentRisk(double positionRisk, double equity);

    double getCurrentTotalVolPercentRisk(double atr, double equity);
}
