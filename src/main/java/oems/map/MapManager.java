package oems.map;

import net.openhft.chronicle.map.ChronicleMap;
import java.io.IOException;

public class MapManager<K, V> {
    private ChronicleMap<K, V> map;

    public MapManager(Class<K> keyClass, Class<V> valueClass, int entries) throws IOException {
        map = ChronicleMap
                .of(keyClass, valueClass)
                .averageKeySize(100)  // Customize based on expected key size
                .averageValueSize(100)  // Customize based on expected value size
                .entries(entries)
                .create();
    }

    public void add(K key, V value) {
        map.put(key, value);
    }

    public void update(K key, V value) {
        map.replace(key, value);
    }

    public void delete(K key) {
        map.remove(key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public void close() {
        map.close();
    }
}
