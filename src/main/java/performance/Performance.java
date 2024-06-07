package performance;

public interface Performance {

    double getInitialInvestment();
    double getCAGRPercentage();
    double getSharpeRatio();
    double getSortinoRatio();
    double getReturnToAvgDrawdown();
    double getMARRatio();
    double getMaxDrawdownPercentage();
    double getWinCount();
    double getLossCount();
    double getWinPercent();
    double getTotalProfit();
    double getProfitFactor();
    long getTradeCount();
}
