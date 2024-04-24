package strategies.indicators.emax;

import org.junit.jupiter.api.Test;
import strategies.indicators.emax.FiftyDayEMA;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FiftyDayEMATest {

    @Test
    void testCalculateEMA() {
        FiftyDayEMA fiftyDayEMA = new FiftyDayEMA();
        double previousEMA = 100;
        double currentPrice = 105;
        double calculatedEMA = fiftyDayEMA.calculateEMA(previousEMA, currentPrice);
        assertEquals(100.19607843137256, calculatedEMA, 0.0001);
    }
}
