package indicators.hurst;

public class HurstExponent {

    public static double calculateHurstExponent(double[] timeSeries) {
        if (timeSeries.length < 2) {
            throw new IllegalArgumentException("Time series must contain at least two data points.");
        }

        int maxTime = timeSeries.length;
        double[] RS = new double[maxTime];
        double[] nvals = new double[maxTime];

        for (int i = 2; i < maxTime; i++) {
            double[] subSeries = new double[i];
            System.arraycopy(timeSeries, 0, subSeries, 0, i);

            double mean = getMean(subSeries);
            double[] adjustedSeries = adjustSeries(subSeries, mean);
            double range = getMax(adjustedSeries) - getMin(adjustedSeries);
            double standardDeviation = getStandardDeviation(subSeries, mean);

            RS[i] = standardDeviation != 0 ? range / standardDeviation : 0;
            nvals[i] = i;
        }

        double[] logRS = logArray(RS);
        double[] logNvals = logArray(nvals);
        double hurstExp = getRegressionSlope(logNvals, logRS);
        return hurstExp;
    }

    private static double[] adjustSeries(double[] series, double mean) {
        double cumsum = 0;
        double[] adjustedSeries = new double[series.length];
        for (int i = 0; i < series.length; i++) {
            cumsum += series[i] - mean;
            adjustedSeries[i] = cumsum;
        }
        return adjustedSeries;
    }

    private static double getMean(double[] series) {
        double sum = 0;
        for (double v : series) sum += v;
        return sum / series.length;
    }

    private static double getMax(double[] series) {
        double max = series[0];
        for (double v : series) if (v > max) max = v;
        return max;
    }

    private static double getMin(double[] series) {
        double min = series[0];
        for (double v : series) if (v < min) min = v;
        return min;
    }

    private static double getStandardDeviation(double[] series, double mean) {
        double sum = 0;
        for (double v : series) sum += Math.pow(v - mean, 2);
        return Math.sqrt(sum / series.length);
    }

    private static double[] logArray(double[] array) {
        double[] logArray = new double[array.length];
        for (int i = 2; i < array.length; i++) {
            if (array[i] <= 0) {
                logArray[i] = 0;
            } else {
                logArray[i] = Math.log(array[i]);
            }
        }
        return logArray;
    }

    private static double getRegressionSlope(double[] x, double[] y) {
        double xMean = getMean(x);
        double yMean = getMean(y);
        double numerator = 0;
        double denominator = 0;

        for (int i = 2; i < x.length; i++) {
            numerator += (x[i] - xMean) * (y[i] - yMean);
            denominator += Math.pow(x[i] - xMean, 2);
        }

        if (denominator == 0) {
            throw new ArithmeticException("Denominator in slope calculation is zero, likely due to insufficient variability in the data.");
        }

        return numerator / denominator;
    }
}
