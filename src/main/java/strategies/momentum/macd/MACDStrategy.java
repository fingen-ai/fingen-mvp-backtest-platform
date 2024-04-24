package strategies.momentum.macd;

import indicators.macd.MACD;

public class MACDStrategy {
    private MACD macd;
    private double lastMacdValue = 0;

    public MACDStrategy(int shortPeriod, int longPeriod, int signalPeriod) {
        this.macd = new MACD(shortPeriod, longPeriod, signalPeriod);
    }

    public String updateAndGetTradingSignal(double newPrice) {
        macd.updatePrice(newPrice);
        double currentMacd = macd.getMACD();
        double currentSignal = macd.getSignalLine();

        // Determine the trading signal based on the crossing of MACD and signal line
        String signal = "Hold"; // Default no-action signal
        if (lastMacdValue <= currentSignal && currentMacd > currentSignal) {
            signal = "Buy"; // MACD crosses above the signal line
        } else if (lastMacdValue >= currentSignal && currentMacd < currentSignal) {
            signal = "Sell"; // MACD crosses below the signal line
        }

        // Update lastMacdValue for the next price update
        lastMacdValue = currentMacd;
        return signal;
    }
}
