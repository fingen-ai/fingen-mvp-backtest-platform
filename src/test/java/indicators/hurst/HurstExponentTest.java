package indicators.hurst;

import org.junit.Assert;
import org.junit.Test;

public class HurstExponentTest {

    @Test
    public void testHurstExponentCalculation() {
        double[] data = generateTestData(1000);
        double hurst = HurstExponent.calculateHurstExponent(data);
        Assert.assertNotNull("Hurst exponent calculation should not return null", hurst);
        System.out.println("Calculated Hurst Exponent: " + hurst);

        if(hurst > 0.5) {
            System.out.println("Trending time series");
        } else if(hurst < 0.5) {
            System.out.println("Mean-Reverting time series");
        } else {
            System.out.println("Sideways time series");
        }
    }

    private double[] generateTestData(int size) {
        double[] data = new double[size];
        double value = 50.0;
        for (int i = 0; i < size; i++) {
            value += (Math.random() - 0.5) * 10; // Random walk simulation
            data[i] = value;
        }
        return data;
    }
}
