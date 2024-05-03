package performance;

import performance.metrics.FinancialMetrics;

public class PerformanceImpl implements Performance {

    int tradeCount = 0;
    double initialInvestment = 10000000;
    double finalValue = 11000000;
    double numberOfYears = 5.5415;
    double riskFreeRate = 0.02;
    double[] returns = new double[]{}; // PerfTest data {0.10, 0.05, -0.02, 0.04, -0.01}; // sample annual returns
    double[] profits = new double[]{}; // PerfTest data {200, 150, 300}; // sample profits
    double[] losses = new double[]{}; // PerfTest data {-50, -70, -90}; // sample losses


    FinancialMetrics finMet = new FinancialMetrics();
    
    double cagr = 0;
    double sharpeRatio = 0;
    double sortinoRatio = 0;
    double returnToAvgDrawdown = 0;
    double marRatio = 0;
    double maxDD = 0;
    double winCount = 0;
    double lossCount = 0;
    double reliablityPercentage = 0;
    double totalProfit = 0;
    double profitFactor = 0;

    public double getInitialInvestment() {
        return initialInvestment;
    }

    public double getCAGRPercentage() {
        cagr = finMet.calculateCAGR(initialInvestment, finalValue, numberOfYears);
        return cagr;
    }

    public double getSharpeRatio() {
        sharpeRatio = finMet.calculateSharpeRatio(returns, riskFreeRate);;
        return sharpeRatio;
    }

    public double getSortinoRatio() {
        sortinoRatio = finMet.calculateSortinoRatio(returns, riskFreeRate);
        return sortinoRatio;
    }

    public double getReturnToAvgDrawdown() {
        returnToAvgDrawdown = 0; // BUILD
        return returnToAvgDrawdown;
    }

    public double getMARRatio() {
        marRatio = finMet.calculateMARRatio(getCAGRPercentage(), getMaxDrawdownPercentage());
        return marRatio;
    }

    public double getMaxDrawdownPercentage() {
        maxDD = finMet.calculateMaximumDrawdown(returns);
        return maxDD;
    }

    public double getWinCount() {
        winCount = finMet.calculateWinCount(returns);
        return winCount;
    }

    public double getLossCount() {
        lossCount = finMet.calculateLossCount(returns);
        return lossCount;
    }

    public double getReliabilityPercentage() {
        reliablityPercentage = winCount / (winCount + lossCount);
        return reliablityPercentage;
    }

    public double getTotalProfit() {
        totalProfit = finMet.calculateTotalProfit(profits);
        return totalProfit;
    }

    public double getProfitFactor() {
        profitFactor = finMet.calculateProfitFactor(profits, losses);
        return profitFactor;
    }
    public int getTradeCount() {
        return tradeCount;
    }

}
