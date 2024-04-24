package indicators.macd;

public class MACD {
    //private double[] price = new double[]{22.27, 22.19, 22.08, 22.17, 22.18, 22.13, 22.23, 22.43, 22.24, 22.29, 22.15, 22.39, 22.38, 22.61, 23.36, 24.05, 23.75, 23.83, 23.95, 23.63, 23.82, 23.87, 23.65, 23.19, 23.10, 23.33, 22.68, 23.10, 22.40, 22.17};
    private int shortPeriod;
    private int longPeriod;
    private int signalPeriod;
    private double shortEma;
    private double longEma;
    private double signalEma;
    private boolean isInitialized = false;

    public MACD(int shortPeriod, int longPeriod, int signalPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
    }

    public void updatePrice(double newPrice) {
        if (!isInitialized) {
            initialize(newPrice);
            return;
        }
        updateEMAs(newPrice);
    }

    private void initialize(double initialPrice) {
        shortEma = initialPrice;
        longEma = initialPrice;
        signalEma = 0;
        isInitialized = true;
    }

    private void updateEMAs(double newPrice) {
        shortEma = shortEma + (2.0 / (shortPeriod + 1)) * (newPrice - shortEma);
        longEma = longEma + (2.0 / (longPeriod + 1)) * (newPrice - longEma);
        double macd = shortEma - longEma;
        signalEma = signalEma + (2.0 / (signalPeriod + 1)) * (macd - signalEma);
    }

    public double getMACD() {
        return shortEma - longEma;
    }

    public double getSignalLine() {
        return signalEma;
    }
}
