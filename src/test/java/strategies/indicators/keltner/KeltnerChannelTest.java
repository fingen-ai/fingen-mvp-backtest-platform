package strategies.indicators.keltner;

import strategies.indicators.keltner.*;

public class KeltnerChannelTest {
    public static void main(String[] args) {
        double[] prices = {22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39};
        double[] high = {22.35, 22.28, 22.16, 22.26, 22.30, 22.21, 22.32, 22.60, 22.25, 22.42, 22.34, 22.63};
        double[] low = {22.17, 22.10, 22.07, 22.03, 22.07, 22.05, 22.07, 22.17, 22.02, 22.22, 22.07, 22.22};
        double[] close = {22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39};

        MovingAverageCalculator maCalculator = new ExponentialMovingAverageCalculator(10);
        AverageTrueRangeCalculator atrCalculator = new SimpleAverageTrueRangeCalculator(10);

        KeltnerChannel kc = new KeltnerChannel(maCalculator, atrCalculator, 1.5);
        double[] channel = kc.calculateChannel(prices, high, low, close);

        System.out.println("EMA: " + channel[0]);
        System.out.println("Upper Band: " + channel[1]);
        System.out.println("Lower Band: " + channel[2]);
    }
}
