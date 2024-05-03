package risk;

public interface Risk {

    double getInitRiskPercentThreshold();

    double getInitVolPercentThreshold();

    double getOngoingRiskPercentThreshold();

    double getOngoingVolPercentThreshold();

    double getCurrentTotalPercentRisk(double positionAmt, double nav);

    double getCurrentTotalVolPercentRisk(double atr, double nav);
}
