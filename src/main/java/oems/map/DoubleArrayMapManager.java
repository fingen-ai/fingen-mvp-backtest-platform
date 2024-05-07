package oems.map;

import net.openhft.chronicle.map.ChronicleMap;
import java.io.IOException;

public class DoubleArrayMapManager {
    private ChronicleMap<CharSequence, byte[]> map;

    public DoubleArrayMapManager(int entries, CharSequence sampleKey, int arraySize) throws IOException {
        map = ChronicleMap
                .of(CharSequence.class, byte[].class)
                .averageKey(sampleKey)  // Using a sample key to estimate the average key size
                .averageValueSize(Double.BYTES * arraySize)  // Estimate based on number of doubles in the array
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
        map.remove(key);
    }

    public double[] get(CharSequence key) {
        byte[] value = map.get(key);
        return bytesToDoubles(value);
    }

    public void close() {
        map.close();
    }

    private byte[] doublesToBytes(double[] doubles) {
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
        if (bytes == null) return null;
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
