package metrics;

public class FinancialMetrics {

    // Constructor may take in parameters like arrays of periodic returns, initial investment, etc.
    public FinancialMetrics() {
        // Initialize with actual data
    }

    public double calculateCAGR(double initialInvestment, double finalValue, int numberOfYears) {
        return Math.pow(finalValue / initialInvestment, 1.0 / numberOfYears) - 1;
    }

    public double calculateSharpeRatio(double[] returns, double riskFreeRate) {
        // This method assumes that 'returns' is an array of percentage returns
        double averageReturn = calculateAverage(returns);
        double standardDeviation = calculateStandardDeviation(returns);
        return (averageReturn - riskFreeRate) / standardDeviation;
    }

    public double calculateSortinoRatio(double[] returns, double riskFreeRate) {
        double averageReturn = calculateAverage(returns);
        double downsideDeviation = calculateDownsideDeviation(returns, riskFreeRate);
        return (averageReturn - riskFreeRate) / downsideDeviation;
    }

    public double calculateAverageDrawdown(double[] drawdowns) {
        double avgDrawdown = calculateAverage(drawdowns);
        return avgDrawdown;
    }

    public double calculateMaximumDrawdown(double[] values) {
        // 'values' should be an array of cumulative returns or portfolio values
        double maxDrawdown = 0;
        double peak = values[0];

        for (double value : values) {
            if (value > peak) {
                peak = value;
            }
            double drawdown = (peak - value) / peak;
            if (drawdown > maxDrawdown) {
                maxDrawdown = drawdown;
            }
        }

        return maxDrawdown;
    }

    public double calculateMARRatio(double cagr, double maxDrawdown) {
        return cagr / maxDrawdown;
    }

    public int calculateWinCount(double[] returns) {
        int wins = 0;
        for (double ret : returns) {
            if (ret > 0) {
                wins++;
            }
        }
        return wins;
    }

    public int calculateLossCount(double[] returns) {
        int losses = 0;
        for (double ret : returns) {
            if (ret < 0) {
                losses++;
            }
        }
        return losses;
    }

    public double calculateTotalProfit(double[] profits) {
        double total = 0;
        for (double profit : profits) {
            total += profit;
        }
        return total;
    }

    public double calculateProfitFactor(double[] profits, double[] losses) {
        double totalProfit = calculateTotal(profits);
        double totalLoss = Math.abs(calculateTotal(losses)); // Losses are typically negative
        return totalProfit / totalLoss;
    }

    // Utility methods for calculations
    private double calculateAverage(double[] values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum / values.length;
    }

    private double calculateStandardDeviation(double[] values) {
        double mean = calculateAverage(values);
        double sum = 0;
        for (double v : values) {
            sum += Math.pow(v - mean, 2);
        }
        return Math.sqrt(sum / values.length);
    }

    private double calculateDownsideDeviation(double[] returns, double riskFreeRate) {
        double downsideSum = 0;
        for (double ret : returns) {
            if (ret < riskFreeRate) {
                downsideSum += Math.pow(ret - riskFreeRate, 2);
            }
        }
        return Math.sqrt(downsideSum / returns.length);
    }

    private double calculateTotal(double[] values) {
        double total = 0;
        for (double value : values) {
            total += value;
        }
        return total;
    }
}
