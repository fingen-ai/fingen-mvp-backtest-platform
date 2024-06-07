package core;

import core.service.Orchestrator;
import core.service.oems.OEMSData;
import core.service.performance.PerfData;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import oems.map.OrderMappingService;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {

    OrderMappingService orderMS = new OrderMappingService();

    int recCount = 0;

    public PerformanceTest() throws IOException {
    }

    @Before
    public void setup() throws IOException {
        // Cleanup maps and queues
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/nosIDArray.dat"));
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/coaIDArray.dat"));
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/nos.dat"));
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/coa.dat"));

        // Initialize Orchestrator with test configurations, if needed
        Orchestrator.init();
    }

    @Test
    public void testAllQueues() throws Exception {
        // Run the Orchestrator to populate the queues
        Orchestrator.run();

        // Introducing a delay
        Thread.sleep(5000); // Adjust time as needed based on your system's performance

        // Read all data from the queues
        List<OEMSData> oemsDataList = readAllDataFromQueue("oemsQ", OEMSData.class);
        List<PerfData> perfDataList = readAllDataFromQueue("perfQ", PerfData.class);

        // Assume equal number of records in both queues for simplicity
        assertEquals("Mismatch in number of records", oemsDataList.size(), perfDataList.size());

        // Validate each record pair
        for (int i = 0; i < oemsDataList.size(); i++) {
            PerfData actualPerfData = perfDataList.get(i);
            OEMSData actualOEMSData = oemsDataList.get(i);
            if (actualPerfData.recId == actualOEMSData.recId) {
                validateDTOAndQueuesIntegration(actualOEMSData, actualPerfData);
            }
        }
    }

    // Read all data from a queue
    private <T> List<T> readAllDataFromQueue(String queueName, Class<T> type) throws IOException {
        List<T> dataList = new ArrayList<>();
        String queuePath = OS.TMP + "/HiveMain/Queues/" + queueName;
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
            ExcerptTailer tailer = queue.createTailer();
            while (true) {
                try (DocumentContext dc = tailer.readingDocument()) {
                    if (dc.isPresent() && dc.wire() != null) {
                        T data = dc.wire().read().object(type);
                        dataList.add(data);
                    } else {
                        break;
                    }
                }
            }
        }
        return dataList;
    }

    // Validate data from strategy service matches data passed to insight service
    private void validateDTOAndQueuesIntegration(OEMSData expected, PerfData actual) {
        // Price data, sans start, stop, and latency, being diff. services and all ;)
        assertEquals("Mismatch in some Price field", expected.recId, actual.recId);
        assertEquals("Mismatch in some Price field", expected.start, actual.start);
        assertEquals("Mismatch in some Price field", expected.end, actual.end);
        assertEquals("Mismatch in some Price field", expected.open, actual.open, 0.0);
        assertEquals("Mismatch in some Price field", expected.high, actual.high, 0.0);
        assertEquals("Mismatch in some Price field", expected.low, actual.low, 0.0);
        assertEquals("Mismatch in some Price field", expected.close, actual.close, 0.0);
        assertEquals("Mismatch in some Price field", expected.volume, actual.volume, 0.0);
        assertEquals("Mismatch in some Price field", expected.marketCap, actual.marketCap, 0.0);

        // Strategy data
        assertEquals("Mismatch in some Strategy field", expected.lhcAvgPrice, actual.lhcAvgPrice, 0.0);
        assertEquals("Mismatch in some Strategy field", expected.bassoOrderIdea, actual.bassoOrderIdea);
        assertEquals("Mismatch in some Strategy field", expected.bassoOrderIdea, actual.bassoOrderIdea);

        // Insight data
        assertEquals("Mismatch in some Insight field", expected.previousClose, actual.previousClose, 0.0);
        assertEquals("Mismatch in some Insight field", expected.atr, actual.atr, 0.0);

        // performance report data - tearsheet, landing & blog pages.

        recCount++;

        // END OF BDD SCENARIOS
    }

    private void deleteFileOrDirectory(Path path) throws IOException {
        if (Files.exists(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("Deleted " + path);
        } else {
            System.out.println("File or directory does not exist: " + path);
        }
    }
}