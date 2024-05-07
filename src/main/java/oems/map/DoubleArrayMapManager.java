package oems.map;

import net.openhft.chronicle.map.ChronicleMap;
import java.io.IOException;

public class DoubleArrayMapManager {
    private ChronicleMap<CharSequence, byte[]> map;

    public DoubleArrayMapManager(int entries, CharSequence sampleKey, int estimatedMaxArraySize) throws IOException {
        map = ChronicleMap
                .of(CharSequence.class, byte[].class)
                .averageKey(sampleKey)  // Using a sample key to estimate the average key size
                .averageValueSize(Double.BYTES * estimatedMaxArraySize)  // Estimation with max expected array size
                .entries(entries)
                .create();
    }

    public void add(CharSequence key, double[] value) {
        map.put(key, doublesToBytes(value));
    }

    public void update(CharSequence key, double[] value) {
        map.replace(key, doublesToBytes(value));
    }

    public void delete(CharSequence key) {
        map.remove(key); // Ensure this actually removes the entry
    }

    public double[] get(CharSequence key) {
        byte[] value = map.get(key);
        if (value == null) {
            return null;  // This will correctly handle cases where the key does not exist.
        }
        if (value.length == 0) {
            return new double[0];  // Ensure zero-length arrays are returned as empty arrays, not null.
        }
        return bytesToDoubles(value);
    }

    public void close() {
        map.close();
    }

    private byte[] doublesToBytes(double[] doubles) {
        if (doubles == null || doubles.length == 0) {
            return new byte[0];
        }
        byte[] bytes = new byte[doubles.length * Double.BYTES];
        for (int i = 0; i < doubles.length; i++) {
            long bits = Double.doubleToLongBits(doubles[i]);
            for (int j = 0; j < Double.BYTES; j++) {
                bytes[i * Double.BYTES + j] = (byte) ((bits >> (j * 8)) & 0xff);
            }
        }
        return bytes;
    }

    private double[] bytesToDoubles(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return new double[0];
        }
        double[] doubles = new double[bytes.length / Double.BYTES];
        for (int i = 0; i < doubles.length; i++) {
            long bits = 0;
            for (int j = 0; j < Double.BYTES; j++) {
                bits |= (((long) bytes[i * Double.BYTES + j] & 0xff) << (j * 8));
            }
            doubles[i] = Double.longBitsToDouble(bits);
        }
        return doubles;
    }
}
