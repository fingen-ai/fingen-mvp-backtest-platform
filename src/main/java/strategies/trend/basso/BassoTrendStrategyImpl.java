package strategies.trend.basso;

import strategies.indicators.bollinger.BollingerBands;
import strategies.indicators.donchian.DonchianChannel;
import strategies.indicators.keltner.*;

public class BassoTrendStrategyImpl implements BassoTrendStrategy {

    private double[] prices = new double[0];//{22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39};
    private double[] high = new double[0];//{22.35, 22.28, 22.16, 22.26, 22.30, 22.21, 22.32, 22.60, 22.25, 22.42, 22.34, 22.63};
    private double[] low = new double[0];//{22.17, 22.10, 22.07, 22.03, 22.07, 22.05, 22.07, 22.17, 22.02, 22.22, 22.07, 22.22};
    private double[] close = new double[0];//{22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39};

    private String strategyDecision = null;

    MovingAverageCalculator maCalculator = new ExponentialMovingAverageCalculator(50);
    AverageTrueRangeCalculator atrCalculator = new SimpleAverageTrueRangeCalculator(50);

    @Override
    public String getStrategyDecision() {

        if(prices.length >= 50) {
            KeltnerChannel kc = new KeltnerChannel(maCalculator, atrCalculator, 1.5);
            double[] keltnerChannel = kc.calculateChannel(prices, high, low, close);

            DonchianChannel dc = new DonchianChannel(prices, 50);
            double[] donchianChannel = dc.calculateChannel(49);

            BollingerBands bb = new BollingerBands(prices, 50);
            double[] bollingerBands = bb.calculateBollingerBands(49);

            int buySignals = 0;
            int sellSignals = 0;

            // Logic to count the signals
            for (int i = 0; i < prices.length; i++) {
                // Keltner Channel logic
                if (prices[i] > keltnerChannel[1]) buySignals++;
                if (prices[i] < keltnerChannel[2]) sellSignals++;

                // Donchian Channel logic
                if (prices[i] > donchianChannel[0]) buySignals++;
                if (prices[i] < donchianChannel[1]) sellSignals++;

                // Bollinger Bands logic
                if (prices[i] > bollingerBands[1]) buySignals++;
                if (prices[i] < bollingerBands[2]) sellSignals++;
            }


            // Making a decision based on the signals
            if (buySignals > sellSignals) {
                strategyDecision = "Bullish";
            } else if (sellSignals > buySignals) {
                strategyDecision = "Bearish";
            } else {
                strategyDecision = "Neutral";
            }
        } else {
            strategyDecision = "Prices Array Length < 50";
        }

        return strategyDecision;
    }
}
