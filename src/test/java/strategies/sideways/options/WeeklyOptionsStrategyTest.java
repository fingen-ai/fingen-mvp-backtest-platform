package strategies.sideways.options;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeeklyOptionsStrategyTest {

    @Test
    public void testSidewaysBullishMarketExecution() {
        double[] sidewaysMarketPrices = {110, 110.5, 111, 110.5, 110, 110.5, 111}; // Minor fluctuations

        TrendStateMap trendStateMap = new TrendStateMap("Bullish");
        WeeklyOptionsStrategy strategy = new WeeklyOptionsStrategy(trendStateMap, sidewaysMarketPrices);

        String result = strategy.execute();
        assertEquals("Bull Put Spread executed for 7 days.", result);
    }

    @Test
    public void testSidewaysBearishMarketExecution() {
        double[] sidewaysMarketPrices = {111, 110.5, 110, 110.5, 111, 110.5, 110}; // Minor fluctuations

        TrendStateMap trendStateMap = new TrendStateMap("Bearish");
        WeeklyOptionsStrategy strategy = new WeeklyOptionsStrategy(trendStateMap, sidewaysMarketPrices);

        String result = strategy.execute();
        assertEquals("Bear Call Spread executed for 7 days.", result);
    }

    @Test
    public void testNoSpreadDueToTrendingMarket() {
        // Price data indicative of a strong uptrend or downtrend
        double[] trendingMarketPrices = {100, 102, 104, 106, 108, 110, 112};

        TrendStateMap trendStateMap = new TrendStateMap("Bullish"); // Trend state shouldn't matter here
        WeeklyOptionsStrategy strategy = new WeeklyOptionsStrategy(trendStateMap, trendingMarketPrices);

        String result = strategy.execute();
        assertEquals("No options spread executed this week.", result);
    }
}
