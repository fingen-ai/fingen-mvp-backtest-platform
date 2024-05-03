package strategies.indicators.atr;

public class ATRTest {
    public static void main(String[] args) {
        testATRCalculator();
    }

    private static void testATRCalculator() {
        ATR atr = new ATRImpl(14); // using a common period of 14 for ATR

        // Simulating a series of high, low, and close values along with previous close
        // These values would typically be derived from actual market data
        atr.update(120, 115, 118, 117); // First data point
        atr.update(122, 116, 119, 118);
        atr.update(123, 117, 120, 119);
        atr.update(125, 120, 123, 120);
        double atrUpdate = atr.update(126, 122, 124, 123);

        System.out.println("Computed ATR: " + atr);

        // The expected ATR here is hypothetical; the correct value should be pre-calculated or known from a reliable source
        double expectedATR = 3.0; // This is just an example value
        assert Math.abs(atrUpdate - expectedATR) < 0.1 : "ATR calculation error: Expected ATR close to " + expectedATR + ", but got " + atr;
    }
}
