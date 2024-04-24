package performance;

public interface Performance {

    double getCAGRPercentage();
    double getSharpeRatio();
    double getSortinoRatio();
    double getReturnToAvgDrawdown();
    double getMARRatio();
    double getMaxDrawdownPercentage();
    double getWinCount();
    double getLossCount();
    double getReliabilityPercentage();
    double getTotalProfit();
    double getProfitFactor();
}
