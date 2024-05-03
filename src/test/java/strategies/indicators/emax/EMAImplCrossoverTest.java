package strategies.indicators.emax;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EMAImplCrossoverTest {

    @Test
    public void testCrossoverBuySignal() {
        double[] prices = {100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
                110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
                120, 121, 122, 123, 124, 125, 126, 127, 128, 129,
                130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
                140, 141, 142, 143, 144, 145, 146, 147, 148, 149,
                150, 151, 152, 153, 154, 155, 156, 157, 158, 159}; // 60 days of increasing prices

        double tenDayEMA = calculateEMA(prices, 10);
        double fiftyDayEMA = calculateEMA(prices, 50);

        String action = "";
        if (tenDayEMA > fiftyDayEMA) {
            action = "open-buy";
        } else if (tenDayEMA < fiftyDayEMA) {
            action = "open-sell";
        } else {
            action = "no-action";
        }

        assertEquals("open-buy", action);
    }

    @Test
    public void testCrossoverSellSignal() {
        double[] sellPrices = {160, 159, 158, 157, 156, 155, 154, 153, 152, 151,
                150, 149, 148, 147, 146, 145, 144, 143, 142, 141,
                140, 139, 138, 137, 136, 135, 134, 133, 132, 131,
                130, 129, 128, 127, 126, 125, 124, 123, 122, 121,
                120, 119, 118, 117, 116, 115, 114, 113, 112, 111,
                110, 109, 108, 107, 106, 105, 104, 103, 102, 101}; // 60 days of decreasing prices

        double tenDayEMA = calculateEMA(sellPrices, 10);
        double fiftyDayEMA = calculateEMA(sellPrices, 50);

        String action = "";
        if (tenDayEMA > fiftyDayEMA) {
            action = "open-buy";
        } else if (tenDayEMA < fiftyDayEMA) {
            action = "open-sell";
        } else {
            action = "no-action";
        }

        assertEquals("open-sell", action);
    }

    private double calculateEMA(double[] prices, int period) {
        double ema = prices[0]; // Start EMA with the first price
        double smoothingFactor = 2.0 / (period + 1);
        for (int i = 1; i < prices.length; i++) {
            ema = (prices[i] - ema) * smoothingFactor + ema;
        }
        return ema;
    }
}
