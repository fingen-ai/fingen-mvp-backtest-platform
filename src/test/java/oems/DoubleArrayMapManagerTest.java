package oems;

import oems.map.DoubleArrayMapManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DoubleArrayMapManagerTest {
    private DoubleArrayMapManager mapManager;

    @BeforeEach
    void setUp() throws IOException {
        mapManager = new DoubleArrayMapManager(100, "sampleKey", 10);
    }

    @AfterEach
    void tearDown() {
        mapManager.close();
    }

    @Test
    void testAddAndGetNonZeroLengthArray() {
        CharSequence key = "testKey";
        double[] expectedValues = {1.5, 2.5, 3.5};
        mapManager.add(key, expectedValues);
        double[] actualValues = mapManager.get(key);
        assertArrayEquals(expectedValues, actualValues, 0.001, "The retrieved values should match the stored values.");
    }

    @Test
    void testAddAndGetZeroLengthArray() {
        CharSequence key = "emptyTestKey";
        double[] expectedValues = new double[0];
        mapManager.add(key, expectedValues);
        double[] actualValues = mapManager.get(key);
        assertNotNull(actualValues, "The retrieved array should not be null.");
        assertEquals(0, actualValues.length, "The retrieved values should be an empty array.");
    }

    @Test
    void testUpdateAndGet() {
        CharSequence key = "updateKey";
        double[] initialValues = {10.1, 11.2, 12.3};
        mapManager.add(key, initialValues);

        double[] updatedValues = {13.4, 14.5, 15.6};
        mapManager.update(key, updatedValues);

        double[] actualValues = mapManager.get(key);
        assertArrayEquals(updatedValues, actualValues, 0.001, "The retrieved values should match the updated values.");
    }

    @Test
    void testDelete() {
        CharSequence key = "deleteKey";
        double[] values = {16.7, 17.8, 18.9};
        mapManager.add(key, values);

        mapManager.delete(key);
        double[] actualValues = mapManager.get(key);
        assertNull(actualValues, "The values should be null after deletion.");
    }
}
