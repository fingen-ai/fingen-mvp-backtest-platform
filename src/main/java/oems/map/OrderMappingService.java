package oems.map;

import core.service.oems.OEMSData;
import net.openhft.chronicle.map.ChronicleMap;
import java.io.File;
import java.io.IOException;

public class OrderMappingService {
    private ChronicleMap<String, int[]> nosIDArrayMap;
    public ChronicleMap<Integer, OEMSData> nosMap;
    public static final String MAP_DIRECTORY = System.getProperty("user.home") + "/FinGen/Maps/";

    public OrderMappingService() throws IOException {
        ensureDirectoryExists(); // Ensure the directory exists
        initMaps();
    }

    private void ensureDirectoryExists() {
        new File(MAP_DIRECTORY).mkdirs(); // This will create the directory if it does not exist
    }

    private void initMaps() throws IOException {
        nosIDArrayMap = ChronicleMap
                .of(String.class, int[].class)
                .name("nos-id-array-map")
                .averageKeySize(10) // Average size of a stock symbol, adjust as necessary
                .averageValueSize(100) // Estimated average size of an array of ints
                .entries(50_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "nosIDArray.dat")); // Specific file for this map

        nosMap = ChronicleMap
                .of(Integer.class, OEMSData.class)
                .name("nos-map")
                .averageValueSize(256) // Estimated average serialized size of OEMSData
                .entries(10_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "nos.dat")); // Specific file for this map
    }

    public int[] getPositions(String symbol) {
        return nosIDArrayMap.get(symbol);
    }

    public void addOrder(String symbol, int[] orderIds) {
        nosIDArrayMap.put(symbol, orderIds);
    }

    public void updateOrder(int orderId, OEMSData newData) {
        nosMap.put(orderId, newData);
    }

    public void closePosition(String symbol) {
        nosMap.remove(symbol);
    }

    public void close() {
        nosIDArrayMap.close();
        nosMap.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
