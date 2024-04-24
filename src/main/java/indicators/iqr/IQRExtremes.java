package indicators.iqr;

import java.util.Arrays;

public class IQRExtremes {
    private double[] prices;
    private int period;
    private double q1 = 0;
    private double q3 = 0;
    private double q1Minus = 0;  // 12.5th percentile
    private double q3Plus = 0;  // 87.5th percentile
    private double midpoint = 0;

    public IQRExtremes(double[] prices, int period) {
        this.prices = prices;
        this.period = period;
    }

    public void calculateQuartiles() {
        double[] window = Arrays.copyOfRange(prices, prices.length - period, prices.length);
        Arrays.sort(window);

        midpoint = window[period / 2];
        q1 = window[period / 4];
        q3 = window[(3 * period) / 4];
        q1Minus = window[period / 8];  // 12.5th percentile
        q3Plus = window[(7 * period) / 8];  // 87.5th percentile

        //System.out.println("Q1- (Lower Extreme): " + q1Minus);
        //System.out.println("Q1 (Lower Quartile): " + q1);
        //System.out.println("Median (Q2): " + window[period / 2]);
        //System.out.println("Q3 (Upper Quartile): " + q3);
        //System.out.println("Q3+ (Upper Extreme): " + q3Plus);
    }

    public double getLowerExtreme() {
        return q1Minus;
    }

    public double getUpperExtreme() {
        return q3Plus;
    }

    public double getMidPoint() {
        return midpoint;
    }
}
