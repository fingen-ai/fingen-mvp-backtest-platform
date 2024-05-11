package strategies.indicators.atr;

import core.service.insight.InsightData;

public class ATRImpl implements ATR {
    private static final int PERIOD = 14;
    private double[] trueRanges = new double[PERIOD];
    private double currentATR = 0.0;
    private int dayCount = 0;

    @Override
    public double calculateATR(InsightData data, int period) {
        double trueRange = calculateTrueRange(data);
        trueRanges[dayCount % PERIOD] = trueRange;
        dayCount++;

        // Calculate EMA for ATR
        if (dayCount <= PERIOD) {
            // Initially, just compute the simple average
            double sum = 0;
            for (int i = 0; i < dayCount; i++) {
                sum += trueRanges[i];
            }
            currentATR = sum / dayCount;
        } else {
            // Compute EMA
            double multiplier = 2.0 / (PERIOD + 1);
            currentATR = (trueRange - currentATR) * multiplier + currentATR;
        }

        return currentATR;
    }

    private double calculateTrueRange(InsightData data) {
        double high = data.high;
        double low = data.low;
        double previousClose = data.previousClose;
        double term1 = high - low;
        double term2 = Math.abs(high - previousClose);
        double term3 = Math.abs(low - previousClose);
        return Math.max(term1, Math.max(term2, term3));
    }
}
