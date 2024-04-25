package strategies.trend.basso;

import strategies.indicators.bollinger.BollingerBands;
import strategies.indicators.donchian.DonchianChannel;
import strategies.indicators.keltner.*;

public class BassoTrendStrategyImpl implements BassoTrendStrategy {

    private String strategyDecision = null;

    MovingAverageCalculator maCalculator = new ExponentialMovingAverageCalculator(50);
    AverageTrueRangeCalculator atrCalculator = new SimpleAverageTrueRangeCalculator(50);

    @Override
    public String getStrategyDecision(double[] prices, double[] high, double[] low, double[] close) {

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
        return strategyDecision;
    }
}
