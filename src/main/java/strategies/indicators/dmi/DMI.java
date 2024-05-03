package strategies.indicators.dmi;

public class DMI {
    private final int period;
    private final MovingAverage trueRangeEMA;
    private final MovingAverage plusDIEMA;
    private final MovingAverage minusDIEMA;
    private double prevHigh = 0;
    private double prevLow = 0;
    private double prevClose = 0;

    public DMI(int period) {
        this.period = period;
        this.trueRangeEMA = new MovingAverage(period);
        this.plusDIEMA = new MovingAverage(period);
        this.minusDIEMA = new MovingAverage(period);
    }

    public void update(double high, double low, double close) {
        if (prevHigh == 0 && prevLow == 0 && prevClose == 0) {
            prevHigh = high;
            prevLow = low;
            prevClose = close;
            return;
        }

        double tr = Math.max(Math.max(high - low, Math.abs(high - prevClose)), Math.abs(low - prevClose));
        double plusDM = (high - prevHigh) > (prevLow - low) && (high - prevHigh) > 0 ? (high - prevHigh) : 0;
        double minusDM = (prevLow - low) > (high - prevHigh) && (prevLow - low) > 0 ? (prevLow - low) : 0;

        trueRangeEMA.update(tr);
        plusDIEMA.update(plusDM);
        minusDIEMA.update(minusDM);

        prevHigh = high;
        prevLow = low;
        prevClose = close;
    }

    public double getPlusDI() {
        return 100 * (plusDIEMA.getAverage() / trueRangeEMA.getAverage());
    }

    public double getMinusDI() {
        return 100 * (minusDIEMA.getAverage() / trueRangeEMA.getAverage());
    }

    public double getADX() {
        double diDiff = Math.abs(getPlusDI() - getMinusDI());
        double diSum = getPlusDI() + getMinusDI();
        return 100 * (diDiff / diSum);
    }

    private class MovingAverage {
        private final int period;
        private final double[] window;
        private int index = 0;
        private double sum = 0;
        private int count = 0;

        public MovingAverage(int period) {
            this.period = period;
            this.window = new double[period];
        }

        public void update(double value) {
            sum -= window[index];
            sum += value;
            window[index] = value;
            index = (index + 1) % period;
            if (count < period) count++;
        }

        public double getAverage() {
            return sum / count;
        }
    }
}
