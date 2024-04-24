package strategies.indicators.emax;

import org.junit.jupiter.api.Test;
import strategies.indicators.emax.TenDayEMA;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TenDayEMATest {

    @Test
    void testCalculateEMA() {
        TenDayEMA tenDayEMA = new TenDayEMA();
        double previousEMA = 100;
        double currentPrice = 105;
        double calculatedEMA = tenDayEMA.calculateEMA(previousEMA, currentPrice);
        assertEquals(100.9090909090909, calculatedEMA, 0.0001);
    }
}
