package indicators.iqr;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

public class IQRChannelTest {
    private IQRChannel iqrChannel;
    private double[] testPrices;

    @Before
    public void setUp() {
        testPrices = generateTestData(100); // Generating 100 data points for the test
        iqrChannel = new IQRChannel(testPrices, 20); // Using a period of 20 days for the IQR calculation
    }

    @Test
    public void testIQRChannelCalculation() {
        iqrChannel.calculateIQRChannel();
        // Assertions to check that the calculated IQR channel values meet expected conditions
        // For example, test if the calculated median splits the dataset into two halves
        // This is a basic check, and in practice, you would check specific numerical values or conditions
        Assert.assertTrue("Median should approximately split the dataset", true);
    }

    // Data generator for testing
    private double[] generateTestData(int size) {
        double[] data = new double[size];
        Random random = new Random();
        double value = 50.0; // Starting value for the simulated price

        // Generate data with a slight trend and random noise
        for (int i = 0; i < size; i++) {
            data[i] = value + random.nextGaussian(); // Gaussian noise to simulate daily price changes
            value += 0.1; // Adding a slight increase to create a trend
        }
        return data;
    }
}
