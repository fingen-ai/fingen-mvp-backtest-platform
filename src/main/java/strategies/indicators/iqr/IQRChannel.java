package strategies.indicators.iqr;

import java.util.Arrays;

public class IQRChannel {
    private double[] prices;
    private int period;

    public IQRChannel(double[] prices, int period) {
        this.prices = prices;
        this.period = period;
    }

    public void calculateIQRChannel() {
        double[] window = new double[period];
        System.arraycopy(prices, prices.length - period, window, 0, period);
        Arrays.sort(window);

        double q1 = window[(int) (period * 0.25)];
        double q3 = window[(int) (period * 0.75)];
        double iqr = q3 - q1;
        double median = window[(int) (period * 0.5)];

        double upperBand = q3 + 1.5 * iqr;
        double lowerBand = q1 - 1.5 * iqr;

        System.out.println("Median: " + median);
        System.out.println("Upper Band: " + upperBand);
        System.out.println("Lower Band: " + lowerBand);
    }
}
