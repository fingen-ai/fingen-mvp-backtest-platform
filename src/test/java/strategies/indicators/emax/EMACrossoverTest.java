package strategies.indicators.emax;

public class EMACrossoverTest {
    public static void main(String[] args) {
        testUptrendCrossover();
        testDowntrendCrossover();
    }

    private static void testUptrendCrossover() {
        EMACrossover emaCrossover = new EMACrossoverImpl(10, 50);

        // Simulating price updates to ensure an uptrend crossover
        // Prices are arranged to cause the short-term EMA to rise above the long-term EMA
        double[] prices = {95, 96, 97, 98, 99, 100, 101, 102, 103, 104,
                105, 106, 107, 108, 109, 110, 111, 112, 113, 114};
        boolean crossoverDetected = false;

        for (double price : prices) {
            if (emaCrossover.update(price)) {
                crossoverDetected = true;
                break;
            }
        }

        assert crossoverDetected : "Expected an uptrend crossover where the short-term EMA rises above the long-term EMA.";
        System.out.println("Uptrend crossover test passed.");
    }

    private static void testDowntrendCrossover() {
        EMACrossover emaCrossover = new EMACrossoverImpl(10, 50);

        // Simulating price updates to ensure a downtrend crossover
        // Prices are arranged to cause the short-term EMA to fall below the long-term EMA
        double[] prices = {114, 113, 112, 111, 110, 109, 108, 107, 106, 105,
                104, 103, 102, 101, 100, 99, 98, 97, 96, 95};
        boolean crossoverDetected = false;

        for (double price : prices) {
            if (emaCrossover.update(price)) {
                crossoverDetected = true;
                break;
            }
        }

        assert crossoverDetected : "Expected a downtrend crossover where the short-term EMA falls below the long-term EMA.";
        System.out.println("Downtrend crossover test passed.");
    }
}
