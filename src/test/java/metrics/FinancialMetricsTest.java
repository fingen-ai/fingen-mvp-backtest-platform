package metrics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FinancialMetricsTest {

    private FinancialMetrics fm;
    private double[] returns;
    private double[] profits;
    private double[] losses;
    private double[] drawdowns;
    private double riskFreeRate;
    private double initialInvestment;
    private double finalValue;
    private int numberOfYears;

    @BeforeEach
    void setUp() {
        // Initialize with sample data
        fm = new FinancialMetrics();
        returns = new double[]{0.10, 0.05, -0.02, 0.04, -0.01}; // sample annual returns
        drawdowns = new double[]{100, 110, 105, 107, 106}; // sample values for drawdown calculation
        profits = new double[]{200, 150, 300}; // sample profits
        losses = new double[]{-50, -70, -90}; // sample losses
        riskFreeRate = 0.02; // example risk-free rate
        initialInvestment = 10000000;
        finalValue = 10100000;
        numberOfYears = 1;
    }

    @Test
    void testCalculateCAGR() {
        double cagr = fm.calculateCAGR(initialInvestment, finalValue, numberOfYears);
        assertEquals(0.010000000000000009, cagr, 0.0001);
    }

    @Test
    void testCalculateSharpeRatio() {
        double sharpeRatio = fm.calculateSharpeRatio(returns, riskFreeRate);
        assertEquals(0.27558912730477536, sharpeRatio, 0.001);
    }

    @Test
    void testCalculateSortinoRatio() {
        double sortinoRatio = fm.calculateSortinoRatio(returns, riskFreeRate);
        assertEquals(0.5366563145999499, sortinoRatio, 0.001);
    }

    @Test
    void testCalculateMaximumDrawdown() {
        double maxDrawdown = fm.calculateMaximumDrawdown(drawdowns);
        assertEquals(0.0455, maxDrawdown, 0.0001);
    }

    @Test
    void testCalculateAverageDrawdown() {
        double avgDrawdown = fm.calculateAverageDrawdown(drawdowns);
        assertEquals(105.6, avgDrawdown, 0.0001);
    }

    @Test
    void testCalculateMARRatio() {
        double cagr = fm.calculateCAGR(initialInvestment, finalValue, numberOfYears);
        double maxDrawdown = fm.calculateMaximumDrawdown(drawdowns);
        double marRatio = fm.calculateMARRatio(cagr, maxDrawdown);
        assertEquals(0.2200000000000002, marRatio, 0.001);
    }

    @Test
    void testCalculateWinCount() {
        int winCount = fm.calculateWinCount(returns);
        assertEquals(3, winCount);
    }

    @Test
    void testCalculateLossCount() {
        int lossCount = fm.calculateLossCount(returns);
        assertEquals(2, lossCount);
    }

    @Test
    void testCalculateTotalProfit() {
        double totalProfit = fm.calculateTotalProfit(profits);
    }

    @Test
    void testCalculateProfitFactor() {
        double profitFactor = fm.calculateProfitFactor(profits, losses);
        assertEquals(3.0952380952380953, profitFactor, 0.001);
    }
}
