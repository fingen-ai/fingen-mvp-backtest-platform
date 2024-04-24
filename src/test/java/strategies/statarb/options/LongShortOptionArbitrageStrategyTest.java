package strategies.statarb.options;

import static org.junit.Assert.*;
import org.junit.Test;

public class LongShortOptionArbitrageStrategyTest {

    @Test
    public void testUnderpricedOption() {
        LongShortOptionArbitrageStrategy strategy = new LongShortOptionArbitrageStrategy(100, 100, 1, 0.05, 0.2);
        assertEquals("Buy Call - Market price 10.0 is underpriced compared to theoretical price 10.450583572185565", strategy.evaluateOptionTrade(10.0));
    }

    @Test
    public void testOverpricedOption() {
        LongShortOptionArbitrageStrategy strategy = new LongShortOptionArbitrageStrategy(100, 100, 1, 0.05, 0.2);
        assertEquals("Sell Call - Market price 11.5 is overpriced compared to theoretical price 10.450583572185565", strategy.evaluateOptionTrade(11.5));
    }

    @Test
    public void testCorrectlyPricedOption() {
        LongShortOptionArbitrageStrategy strategy = new LongShortOptionArbitrageStrategy(100, 100, 1, 0.05, 0.2);
        assertEquals("Buy Call - Market price 10.45 is underpriced compared to theoretical price 10.450583572185565", strategy.evaluateOptionTrade(10.45));
    }
}
