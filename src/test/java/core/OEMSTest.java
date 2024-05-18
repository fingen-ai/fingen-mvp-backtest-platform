package core;

import core.service.Orchestrator;
import core.service.insight.InsightData;
import core.service.oems.OEMSData;
import core.service.strategy.StrategyData;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import oems.map.OrderMappingService;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class OEMSTest {

    public OEMSTest() throws IOException {
    }

    @Before
    public void setup() throws IOException {
        // Cleanup maps and queues
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/nosIDArray.dat"));
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/nos.dat"));
        deleteFileOrDirectory(Paths.get(System.getProperty("user.home") + "/FinGen/Maps/OMS/Orders/cos.dat"));

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
        List<InsightData> insightDataList = readAllDataFromQueue("insightQ", InsightData.class);
        List<OEMSData> oemsDataList = readAllDataFromQueue("oemsQ", OEMSData.class);

        // Assume equal number of records in both queues for simplicity
        assertEquals("Mismatch in number of records", insightDataList.size(), oemsDataList.size());

        // Validate each record pair
        for (int i = 0; i < insightDataList.size(); i++) {
            InsightData actualInsightData = insightDataList.get(i);
            OEMSData actualOEMSData = oemsDataList.get(i);
            if (actualInsightData.recId == actualOEMSData.recId) {
                validateDTOAndQueuesIntegration(actualInsightData, actualOEMSData);
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
    private void validateDTOAndQueuesIntegration(InsightData expected, OEMSData actual) {
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
        assertEquals("Mismatch in some Insight field", expected.currRiskPercent, actual.currRiskPercent, 0.0);
        assertEquals("Mismatch in some Insight field", expected.currVolRiskPercent, actual.currVolRiskPercent, 0.0);

        assertEquals("Mismatch in some Insight field", expected.orderQtyPerRisk, actual.orderQtyPerRisk, 0.0);
        assertEquals("Mismatch in some Insight field", expected.orderQtyPerVol, actual.orderQtyPerVol, 0.0);

        assertEquals("Mismatch in some Insight field", expected.orderType, actual.orderType);
        assertEquals("Mismatch in some Insight field", expected.orderSide, actual.orderSide);

        assertEquals("Mismatch in some Insight field", expected.openOrderQty, actual.openOrderQty);
        assertEquals("Mismatch in some Insight field", expected.openOrderSide, actual.openOrderSide);
        assertEquals("Mismatch in some Insight field", expected.openOrderPrice, actual.openOrderPrice, 0.0);

        assertEquals("Mismatch in some Insight field", expected.closeOrderQty, actual.closeOrderQty);
        assertEquals("Mismatch in some Insight field", expected.closeOrderSide, actual.closeOrderSide);
        assertEquals("Mismatch in some Insight field", expected.closeOrderPrice, actual.closeOrderPrice, 0.0);
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