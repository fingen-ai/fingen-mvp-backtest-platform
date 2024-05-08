package util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class AppendArrayTest {

    @Test
    public void testAppendDoubleArray() {
        AppendArray appendArray = new AppendArray();

        double[] array = {1.0, 2.0, 3.0};
        double x = 4.0;
        int length = 4;

        double[] expected = {1.0, 2.0, 3.0, 4.0}; // Adjusted expected array length
        assertArrayEquals(expected, appendArray.appendDoubleArray(array, x, length));
    }
}
