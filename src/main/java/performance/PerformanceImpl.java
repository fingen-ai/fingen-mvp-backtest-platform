package performance;

import core.service.performance.PerfData;
import performance.metrics.FinancialMetrics;

public class PerformanceImpl implements Performance {
    // returns
    double initialInvestment = 10000000;
    double cagr = 0;
    double sharpeRatio = 0;
    double sortinoRatio = 0;
    double returnToAvgDrawdown = 0;
    double marRatio = 0;
    double maxDD = 0;
    double winCount = 0;
    double lossCount = 0;
    double winPercentage = 0;
    double lossPercentage = 0;
    double totalProfit = 0;
    double profitFactor = 0;
    double sumOfWinPercentages = 0;
    double sumOfLossPercentages = 0;
    double avgWinPercentage = 0;
    double avgLossPercentage = 0;
    double avgWinAmount = 0;
    double avgLossAmount = 0;
    double edge = 0;
    // vars
    long tradeCount = 0;
    double finalValue = 11000000;
    double numberOfYears = 5.5415;
    double riskFreeRate = 0.02;
    double[] returns = new double[]{}; // PerfTest data {0.10, 0.05, -0.02, 0.04, -0.01}; // sample annual returns
    double[] profits = new double[]{}; // PerfTest data {200, 150, 300}; // sample profits
    double[] losses = new double[]{}; // PerfTest data {-50, -70, -90}; // sample losses


    FinancialMetrics finMet = new FinancialMetrics();

    private double[] drawdowns; // CALC

    public PerformanceImpl(double[] drawdowns) {
        this.drawdowns = drawdowns;
    }

    public double getInitialInvestment() {
        return initialInvestment;
    }

    public double getCAGRPercentage(PerfData perfData) {
        initialInvestment = perfData.initialInvestment;
        finalValue = perfData.nav;
        cagr = finMet.calculateCAGR(initialInvestment, finalValue, numberOfYears);
        return cagr;
    }

    public double getSharpeRatio(double[] returns) {
        sharpeRatio = finMet.calculateSharpeRatio(returns, riskFreeRate);;
        return sharpeRatio;
    }

    public double getSortinoRatio(double[] returns) {
        sortinoRatio = finMet.calculateSortinoRatio(returns, riskFreeRate);
        return sortinoRatio;
    }

    public double getReturnToAvgDrawdown() {
        returnToAvgDrawdown = 0; // BUILD
        return returnToAvgDrawdown;
    }

    public double getMARRatio(PerfData perfData, double[] returns) {
        marRatio = finMet.calculateMARRatio(getCAGRPercentage(perfData), getDrawdownPercentage(returns));
        return marRatio;
    }

    @Override
    public double getDrawdown(double[] returns) {
        return finMet.calculateDrawdown(returns);
    }

    public double getDrawdownPercentage(double[] returns) {
        maxDD = finMet.calculateDrawdown(returns);
        return maxDD;
    }

    // edge related stats
    public double getWinCount(double[] returns) {
        winCount = finMet.calculateWinCount(returns);
        return winCount;
    }

    public double getLossCount(double[] returns) {
        lossCount = finMet.calculateLossCount(returns);
        return lossCount;
    }

    public double getWinPercent() {
        winPercentage = winCount / (winCount + lossCount);
        sumOfWinPercentages += winPercentage;
        return winPercentage;
    }

    public double getLossPercent() {
        lossPercentage = lossCount / (winCount + lossCount);
        sumOfLossPercentages += lossPercentage;
        return lossPercentage;
    }

    public double getAvgWinPercent() {
        avgWinPercentage = sumOfWinPercentages / winCount;
        return avgWinPercentage;
    }

    public double getAvgLossPercent() {
        avgLossPercentage = sumOfLossPercentages / lossCount;
        return avgLossPercentage;
    }

    @Override
    public double getAvgWinAmt(PerfData perfData) {
        avgWinAmount = perfData.openOrderQty * perfData.close / winCount;
        return avgWinAmount;
    }

    @Override
    public double getAvgLossAmt(PerfData perfData) {
        avgLossAmount = perfData.openOrderQty * perfData.close / lossCount;
        return avgLossAmount;
    }

    public double getEdge() {
        edge = (avgWinPercentage * avgWinPercentage) - (avgLossAmount * avgWinPercentage);
        return edge;
    }

    public double getTotalProfit() {
        totalProfit = finMet.calculateTotalProfit(profits);
        return totalProfit;
    }

    public double getProfitFactor() {
        profitFactor = finMet.calculateProfitFactor(profits, losses);
        return profitFactor;
    }

    public long getTradeCount() {
        return tradeCount;
    }
}
