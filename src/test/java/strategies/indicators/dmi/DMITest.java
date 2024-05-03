package strategies.indicators.dmi;

/**
 * The Directional Movement Index (DMI) is a technical analysis indicator used to determine the overall direction of the market as well as the strength of trends.
 *
 * The DMI is part of a system that also includes the Average Directional Index (ADX), which helps to measure the strength of the trend, regardless of whether it is up or down.
 *
 * The DMI itself consists of two lines:
 *
 * 1. **Positive Directional Indicator (+DI)**: Measures the upward trend movement. It's calculated by comparing the current high with the previous high and is considered stronger when the current high is significantly above the previous high.
 *
 * 2. **Negative Directional Indicator (-DI)**: Measures the downward trend movement. It's calculated by comparing the current low with the previous low and is stronger when the current low is significantly below the previous low.
 *
 * The calculations for these indicators involve smoothing mechanisms typically using an exponential moving average, similar to how you would calculate an EMA. The underlying formulas for the +DI and -DI are as follows:
 *
 * - **Positive Directional Movement (+DM)**: This is generally the difference between the current high and the previous high, provided it is positive. If it's negative, +DM is zero.
 *
 * - **Negative Directional Movement (-DM)**: This is the difference between the previous low and the current low, provided it is positive. If it's negative, -DM is zero.
 *
 * For each period (e.g., day, week):
 * - If +DM > -DM and +DM > 0, then +DI (normalized by average true range) is positive, and -DI is zero.
 * - If -DM > +DM and -DM > 0, then -DI (normalized by average true range) is positive, and +DI is zero.
 *
 * The **Average Directional Index (ADX)** is then calculated from these smoothed values of +DI and -DI to measure the strength of the trend. The ADX itself does not indicate the direction of the trend, just how strong the trend is. A high ADX value typically indicates a strong trend, while a low ADX value can suggest a weak trend or no trend.
 *
 * These indicators are widely used in trading systems to assess whether a given market is likely to continue in its current trend or not, helping traders make decisions about entering or exiting trades.
 */
public class DMITest {
    public static void main(String[] args) {
        testInitialTrend();
        testTrendReversal();
    }

    private static void testInitialTrend() {
        DMI dmi = new DMI(14);
        // Assuming we start with a series of increasing highs and decreasing lows
        dmi.update(100, 90, 95);
        dmi.update(110, 100, 105);
        dmi.update(120, 110, 115);
        assert dmi.getPlusDI() > dmi.getMinusDI() : "Expected an upward trend, +DI should be greater than -DI.";
        System.out.println("Initial trend test passed.");
    }

    private static void testTrendReversal() {
        DMI dmi = new DMI(14);
        // Initial upward trend
        dmi.update(100, 90, 95);
        dmi.update(110, 100, 105);
        dmi.update(120, 110, 115);
        // Reversal to a downward trend
        dmi.update(115, 105, 110);
        dmi.update(110, 95, 100);
        assert dmi.getMinusDI() > dmi.getPlusDI() : "Expected a downward trend, -DI should be greater than +DI.";
        System.out.println("Trend reversal test passed.");
    }
}
