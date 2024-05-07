package oems;

import oems.map.DoubleArrayMapManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleArrayMapManagerTest {
    private DoubleArrayMapManager mapManager;

    @BeforeEach
    public void setUp() throws Exception {
        // Create a map manager with a sample key and an estimated array size for testing.
        mapManager = new DoubleArrayMapManager(100, "sampleKey", 5);
    }

    @AfterEach
    public void tearDown() {
        mapManager.close();
    }

    @Test
    public void testAddAndGet() {
        CharSequence key = "testKey-BTC";
        double[] expectedValues = {1.5, 2.5, 3.5, 4.5, 5.5};
        mapManager.add(key, expectedValues);

        double[] actualValues = mapManager.get(key);
        assertArrayEquals(expectedValues, actualValues, 0.001, "The retrieved values should match the stored values.");
    }

    @Test
    public void testUpdateAndGet() {
        CharSequence key = "testKey2";
        double[] initialValues = {6.0, 7.0, 8.0, 9.0, 10.0};
        mapManager.add(key, initialValues);

        double[] updatedValues = {10.0, 9.0, 8.0, 7.0, 6.0};
        mapManager.update(key, updatedValues);

        double[] actualValues = mapManager.get(key);
        assertArrayEquals(updatedValues, actualValues, 0.001, "The retrieved values should match the updated values.");
    }

    @Test
    public void testDelete() {
        CharSequence key = "testKey3";
        double[] values = {11.0, 12.0, 13.0, 14.0, 15.0};
        mapManager.add(key, values);

        mapManager.delete(key);
        double[] actualValues = mapManager.get(key);
        assertNull(actualValues, "The values should be null after deletion.");
    }
}
