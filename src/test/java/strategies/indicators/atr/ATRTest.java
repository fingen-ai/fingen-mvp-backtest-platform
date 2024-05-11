package strategies.indicators.atr;

import core.service.insight.InsightData;
import org.junit.Test;

public class ATRTest {

    @Test
    public void testATR() {
        ATR atrCalculator = new ATRImpl();
        InsightData[] dataPoints = generateTestData();

        for (InsightData data : dataPoints) {
            double atr = atrCalculator.calculateATR(data, 14);
            System.out.println("Day " + data.recId + " ATR: " + atr);
        }
    }

    private static InsightData[] generateTestData() {
        // Assume this generates 14 days of data with realistic high, low, and previous close values
        InsightData[] dataPoints = new InsightData[14];
        for (int i = 0; i < dataPoints.length; i++) {
            InsightData data = new InsightData();
            data.recId = i + 1;
            data.high = 120 + Math.random() * 10;  // Simulated high
            data.low = 115 + Math.random() * 5;   // Simulated low
            data.previousClose = 117 + Math.random() * 8; // Simulated previous close
            dataPoints[i] = data;
        }
        return dataPoints;
    }
}
