package performance;

import core.service.performance.PerfData;

public interface Performance {

    double getInitialInvestment();

    double getCAGRPercentage(PerfData perfData);

    double getSharpeRatio(double[] returns);
    double getSortinoRatio(double[] returns);
    double getMARRatio(PerfData perfData, double[] returns);

    double getDrawdown(double[] returns);
    double getReturnToAvgDrawdown();
    double getDrawdownPercentage(double[] returns);

    double getWinCount();
    double getLossCount();

    double getWinPercent();
    double getLossPercent();

    double getTotalProfit();
    double getProfitFactor();
    long getTradeCount();

    double getAvgWinAmt(PerfData perfData);
    double getAvgLossAmt(PerfData perfData);

    double getAvgWinPercent();
    double getAvgLossPercent();

    double getEdge();
}
