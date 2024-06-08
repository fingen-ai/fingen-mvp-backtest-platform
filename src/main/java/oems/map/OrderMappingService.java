package oems.map;

import core.service.oems.OEMSData;
import net.openhft.chronicle.map.ChronicleMap;

import java.io.File;
import java.io.IOException;

public class OrderMappingService {
    private ChronicleMap<String, long[]> nosIDArrayMap;
    public ChronicleMap<Long, OEMSData> nosMap;
    private ChronicleMap<String, long[]> coaIDArrayMap;
    public ChronicleMap<Long, OEMSData> cosMap;
    public static final String MAP_DIRECTORY = System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/";

    public OrderMappingService() throws IOException {
        ensureDirectoryExists(); // Ensure the directory exists
        initMaps();
    }

    private void ensureDirectoryExists() {
        new File(MAP_DIRECTORY).mkdirs(); // This will create the directory if it does not exist
    }

    private void initMaps() throws IOException {
        nosIDArrayMap = ChronicleMap
                .of(String.class, long[].class)
                .name("nos-id-array-map")
                .averageKeySize(10) // Average size of a stock symbol, adjust as necessary
                .averageValueSize(100) // Estimated average size of an array of ints
                .entries(50_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "nosIDArray.dat")); // Specific file for this map

        nosMap = ChronicleMap
                .of(Long.class, OEMSData.class)
                .name("nos-map")
                .averageValueSize(256) // Estimated average serialized size of OEMSData
                .entries(10_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "nos.dat")); // Specific file for this map

        coaIDArrayMap = ChronicleMap
                .of(String.class, long[].class)
                .name("coa-id-array-map")
                .averageKeySize(10) // Average size of a stock symbol, adjust as necessary
                .averageValueSize(100) // Estimated average size of an array of ints
                .entries(50_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "coaIDArray.dat")); // Specific file for this map

        cosMap = ChronicleMap
                .of(Long.class, OEMSData.class)
                .name("coa-map")
                .averageValueSize(256) // Estimated average serialized size of OEMSData
                .entries(10_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "coa.dat")); // Specific file for this map
    }

    // ARRAYS REC MGT
    public long[] getFromNOSIDArray(String symbol) {
        return nosIDArrayMap.get(symbol);
    }
    public void addToNOSIDArray(String symbol, long[] orderIds) {
        nosIDArrayMap.put(symbol, orderIds);
    }
    public void deleteFromNOSIDArray(String symbol) {
        nosIDArrayMap.remove(symbol);
    }

    public long[] getFromCOAIDArray(String symbol) {
        return coaIDArrayMap.get(symbol);
    }
    public void addToCOAIDArray(String symbol, long[] orderIds) {
        coaIDArrayMap.put(symbol, orderIds);
    }
    public void deleteFromCOAIDArray(String symbol) {
        coaIDArrayMap.remove(symbol);
    }

    // DTO REC MGT
    public void addUpdateNOS(long orderId, OEMSData newData) {
        nosMap.put(orderId, newData);
    }

    public OEMSData getNOS(long orderId) {
        return nosMap.get(orderId);
    }

    public void deleteNOS(OEMSData newData) {
        nosMap.remove(newData.openOrderId);
    }

    public void addUpdateCOA(long orderId, OEMSData newData) {
        cosMap.put(orderId, newData);
    }

    public OEMSData getCOA(long orderId) {
        System.out.println("COA: " + orderId);
        return cosMap.get(orderId);
    }

    public void deleteCOA(OEMSData newData) {
        cosMap.remove(newData.openOrderId);
    }

    // SYS MGT
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
