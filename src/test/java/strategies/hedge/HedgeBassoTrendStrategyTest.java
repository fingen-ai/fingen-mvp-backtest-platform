package strategies.hedge;

public class HedgeBassoTrendStrategyTest {

    public static void main(String[] args) {
        // Prices initially triggering a bearish trend
        double[] bearishPrices = {110, 108, 106, 104, 102, 100, 98, 96, 94, 92, 90, 88, 86, 84, 82, 80, 78, 76, 74, 72, 70,
                110, 108, 106, 104, 102, 100, 98, 96, 94, 92, 90, 88, 86, 84, 82, 80, 78, 76, 74, 72, 70,
                110, 108, 106, 104, 102, 100, 98, 96, 94, 92, 90, 88, 86, 84, 82, 80, 78, 76, 74, 72, 70};

        double[] high = new double[bearishPrices.length];
        double[] low = new double[bearishPrices.length];
        double[] close = bearishPrices.clone();

        for (int i = 0; i < bearishPrices.length; i++) {
            high[i] = bearishPrices[i] + 2; // Slightly above the closing
            low[i] = bearishPrices[i] - 2; // Slightly below the closing
        }

        // Prices crossing above the 21-day EMA
        double[] recoveringPrices = {70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96, 98, 100, 102, 104, 106, 108, 110,
                70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96, 98, 100, 102, 104, 106, 108, 110,
                70, 72, 74, 76, 78, 80, 82, 84, 86, 88, 90, 92, 94, 96, 98, 100, 102, 104, 106, 108, 110};

        double[] highRecovery = new double[recoveringPrices.length];
        double[] lowRecovery = new double[recoveringPrices.length];
        double[] closeRecovery = recoveringPrices.clone();

        for (int i = 0; i < recoveringPrices.length; i++) {
            highRecovery[i] = recoveringPrices[i] + 2;
            lowRecovery[i] = recoveringPrices[i] - 2;
        }

        HedgeBassoTrendStrategy hedgeStrategy = new HedgeBassoTrendStrategy(10000);

        // Apply bearish prices and check if hedge is placed
        hedgeStrategy.updateHedge(bearishPrices, high, low, close);
        assert hedgeStrategy.isHedged() : "Should be hedged due to bearish trend";
        assert hedgeStrategy.getHedgePosition() == 10000 : "Hedge position should match the portfolio";

        // Apply recovering prices and check if hedge is lifted
        hedgeStrategy.updateHedge(recoveringPrices, highRecovery, lowRecovery, closeRecovery);
        assert !hedgeStrategy.isHedged() : "Should not be hedged as price crossed above the 21-day EMA";
        assert hedgeStrategy.getHedgePosition() == 0 : "Hedge position should be zero after recovery";

        System.out.println("All tests passed.");
    }
}
