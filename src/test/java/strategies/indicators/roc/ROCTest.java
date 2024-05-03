package strategies.indicators.roc;

/**
 * Rate of Change (RoC) indicator is a momentum oscillator that measures the percentage change in price between the current price and the price a certain number of periods ago.
 *
 * The RoC indicator is used to identify rate of speed at which prices are changing; a higher RoC typically indicates a strong upward momentum, while a lower (or negative) RoC indicates a strong downward momentum.
 *
 * Where:
 *
 * "Current Price" is the latest closing price.
 * "Price n periods ago" is the closing price n periods before the current price.
 * Usage
 * The RoC can be interpreted in various ways:
 *
 * A positive RoC indicates an uptrend, while a negative RoC suggests a downtrend.
 * Extremely high or low RoC values may indicate overbought or oversold conditions, respectively.
 */
public class ROCTest {
    public static void main(String[] args) {
        testUpwardMomentum();
        testDownwardMomentum();
        testPriceStability();
    }

    private static void testUpwardMomentum() {
        ROC ROC = new ROC(5);
        double[] prices = {100, 105, 110, 115, 120, 125}; // Clear upward trend
        for (double price : prices) {
            ROC.update(price);
        }
        double lastRoC = ROC.getRoc();
        assert lastRoC > 0 : "Expected positive RoC indicating upward momentum, got " + lastRoC;
        System.out.println("Upward momentum test passed with RoC: " + lastRoC);
    }

    private static void testDownwardMomentum() {
        ROC ROC = new ROC(5);
        double[] prices = {120, 115, 110, 105, 100, 95}; // Clear downward trend
        for (double price : prices) {
            ROC.update(price);
        }
        double lastRoC = ROC.getRoc();
        assert lastRoC < 0 : "Expected negative RoC indicating downward momentum, got " + lastRoC;
        System.out.println("Downward momentum test passed with RoC: " + lastRoC);
    }

    private static void testPriceStability() {
        ROC ROC = new ROC(5);
        double[] prices = {100, 100, 100, 100, 100, 100}; // No change in prices
        for (double price : prices) {
            ROC.update(price);
        }
        double lastRoC = ROC.getRoc();
        assert lastRoC == 0 : "Expected RoC of zero indicating stable prices, got " + lastRoC;
        System.out.println("Price stability test passed with RoC: " + lastRoC);
    }
}
