package strategies.indicators.emax;

import strategies.indicators.emax.EMA;
import strategies.indicators.emax.EMAImpl;

public class EMATest {
    public static void main(String[] args) {
        testEMA();
    }

    private static void testEMA() {
        EMA ema = new EMAImpl(10);
        // Test that the first EMA is the same as the first price input
        double firstEma = ema.update(100);
        assert firstEma == 100 : "First EMA should be equal to the first price";

        // Apply more prices and calculate EMA
        ema.update(101);
        ema.update(102);
        double emaUpdate = ema.update(103);

        // Output the computed EMA for debugging
        System.out.println("Computed EMA: " + ema);

        // Assert that the computed EMA is within an acceptable range
        // This is just an example, in real tests, use more precise calculations
        double expectedEma = 101.73; // This should be the expected value based on the formula
        assert Math.abs(emaUpdate - expectedEma) < 0.1 : "EMA calculation error: Expected EMA close to " + expectedEma + ", but got " + ema;
    }
}
