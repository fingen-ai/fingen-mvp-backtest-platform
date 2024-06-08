package performance;

import core.service.performance.PerfData;

public interface Performance {

    double getInitialInvestment();
    double getCAGRPercentage(PerfData perfData);
    double getSharpeRatio(double[] returns);
    double getSortinoRatio();
    double getReturnToAvgDrawdown();
    double getMARRatio(PerfData perfData, double[] returns);
    double getMaxDrawdownPercentage(double[] returns);
    double getWinCount();
    double getLossCount();
    double getWinPercent();
    double getTotalProfit();
    double getProfitFactor();
    long getTradeCount();
}
