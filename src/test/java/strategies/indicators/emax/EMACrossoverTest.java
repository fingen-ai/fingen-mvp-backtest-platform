package strategies.indicators.emax;

public class EMACrossoverTest {
    public static void main(String[] args) {
        EMACrossover emax = new EMACrossoverImpl(10, 50);

        // Simulate daily price updates
        double[] prices = {100, 102, 105, 107, 106, 104, 103, 105, 107, 109,
                110, 111, 115, 117, 116, 113, 112, 110, 109, 105,
                103, 101, 99, 98, 95, 93, 91, 89, 88, 86};

        for (double price : prices) {
            boolean hasCrossover = emax.update(price);
            if (hasCrossover) {
                System.out.println("Crossover detected at price: " + price);
            }
        }
    }
}
