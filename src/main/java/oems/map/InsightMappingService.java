package oems.map;

import core.service.oems.OEMSData;
import net.openhft.chronicle.map.ChronicleMap;
import java.io.File;
import java.io.IOException;

public class InsightMappingService {
    private ChronicleMap<String, long[]> nosIDArrayMap;
    public ChronicleMap<Long, OEMSData> nosInsightMap;
    public static final String MAP_DIRECTORY = System.getProperty("user.home") + "/FinGen/Maps/OMS/Insights/";

    public InsightMappingService() throws IOException {
        ensureDirectoryExists(); // Ensure the directory exists
        initMaps();
    }

    private void ensureDirectoryExists() {
        new File(MAP_DIRECTORY).mkdirs(); // This will create the directory if it does not exist
    }

    private void initMaps() throws IOException {
        nosInsightMap = ChronicleMap
                .of(Long.class, OEMSData.class)
                .name("nos-insight-map")
                .averageValueSize(256) // Estimated average serialized size of OEMSData
                .entries(10_000)
                .createPersistedTo(new File(MAP_DIRECTORY + "nos.dat")); // Specific file for this map
    }

    public OEMSData getNOSInsight(long recId) {
        return nosInsightMap.get(recId);
    }

    public void addNOSInsight(long recId, OEMSData nosIdea) {
        nosInsightMap.put(recId, nosIdea);
    }

    public void close() {
        nosInsightMap.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
