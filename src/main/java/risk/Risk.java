package risk;

public interface Risk {

    double getInitRiskThreshold();

    double getInitVolThreshold();

    double getOngoingRiskThreshold();

    double getOngoingVolThreshold();

    double getTotalRiskThreshold();

    double getTotalVolThreshold();
}
