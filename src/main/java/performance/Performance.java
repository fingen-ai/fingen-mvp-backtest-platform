package performance;

import core.service.performance.PerfData;

public interface Performance {

    double getInitialInvestment();
    double getCAGRPercentage(PerfData perfData);
    double getSharpeRatio();
    double getSortinoRatio();
    double getReturnToAvgDrawdown();
    double getMARRatio(PerfData perfData);
    double getMaxDrawdownPercentage();
    double getWinCount();
    double getLossCount();
    double getWinPercent();
    double getTotalProfit();
    double getProfitFactor();
    long getTradeCount();
}
