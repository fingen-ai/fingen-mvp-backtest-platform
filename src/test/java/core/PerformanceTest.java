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

        // ENTRY: Bullish
        if(recCount == 49) {
            assertNotNull("Mismatch in some OEMS field", actual.openOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.openOrderTimestamp);
            assertEquals("Mismatch in some OEMS field", "Neutral", actual.prevBassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Bullish", actual.bassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some OEMS field", "LMT", actual.orderType);
            assertEquals("Mismatch in some OEMS field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.005, actual.currRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currVolRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.0, actual.orderQtyPerRisk, 0.0);
            assertEquals("Mismatch in some OEMS field", 75.0, actual.orderQtyPerVol, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.0, actual.openOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.0, actual.currCarryQty, 0.0);
            assertEquals("Mismatch in some OEMS field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some OEMS field", "Init New Order Single", actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", 1.0, actual.openOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.99769, actual.openOrderSLPrice, 0.0);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderTimestamp);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", null, actual.closedOrderType);
            assertEquals("Mismatch in some OEMS field", "Init NOS Complete Success - Confirmed", actual.orderConfirmationState);
        }

        // EXIT: Bullish
        // ENTRY: Neutral
        if(recCount == 404) {
            assertNotNull("Mismatch in some OEMS field", actual.openOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.openOrderTimestamp);
            assertEquals("Mismatch in some OEMS field", "Bullish", actual.prevBassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Neutral", actual.bassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", null, actual.orderSide);
            assertEquals("Mismatch in some OEMS field", null, actual.orderType);
            assertEquals("Mismatch in some OEMS field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currVolRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.orderQtyPerRisk, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.orderQtyPerVol, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.openOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currCarryQty, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.openOrderExpiry);
            assertEquals("Mismatch in some OEMS field", null, actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.openOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", 1.0089, actual.openOrderSLPrice, 0.0);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderTimestamp);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", null, actual.closedOrderType);
            assertNull("Mismatch in some OEMS field", actual.orderConfirmationState);
        }

        // EXIT: Neutral
        // ENTRY: Bearish
        if(recCount == 405) {
            assertNotNull("Mismatch in some OEMS field", actual.openOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.openOrderTimestamp);
            assertEquals("Mismatch in some OEMS field", "Neutral", actual.prevBassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Bearish", actual.bassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some OEMS field", "LMT", actual.orderType);
            assertEquals("Mismatch in some OEMS field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.005, actual.currRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currVolRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 126.12249, actual.orderQtyPerRisk, 0.0);
            assertEquals("Mismatch in some OEMS field", 75.67349, actual.orderQtyPerVol, 0.0);
            assertEquals("Mismatch in some OEMS field", 126.12249, actual.openOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", 126.12249, actual.currCarryQty, 0.0);
            assertEquals("Mismatch in some OEMS field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some OEMS field", "Init New Order Single", actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", 0.9911, actual.openOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", 1.02726, actual.openOrderSLPrice, 0.0);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderTimestamp);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderState);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", null, actual.closedOrderType);
            assertEquals("Mismatch in some OEMS field", "Init NOS Complete Success - Confirmed", actual.orderConfirmationState);
        }

        // EXIT: Bearish
        // ENTRY: Bullish
        if(recCount == 407) {
            assertNotNull("Mismatch in some OEMS field", actual.openOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.openOrderTimestamp);
            assertEquals("Mismatch in some OEMS field", "Bearish", actual.prevBassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Bullish", actual.bassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some OEMS field", "LMT", actual.orderType);
            assertEquals("Mismatch in some OEMS field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.005, actual.currRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currVolRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.23795, actual.orderQtyPerRisk, 0.0);
            assertEquals("Mismatch in some OEMS field", 75.14277, actual.orderQtyPerVol, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.23795, actual.openOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.23795, actual.currCarryQty, 0.0);
            assertEquals("Mismatch in some OEMS field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some OEMS field", "Init New Order Single", actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", 0.9981, actual.openOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.90664, actual.openOrderSLPrice, 0.0);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderTimestamp);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderState);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", null, actual.closedOrderType);
            assertEquals("Mismatch in some OEMS field", "Init NOS Complete Success - Confirmed", actual.orderConfirmationState);
        }

        // EXIT: Bullish
        // ENTRY: Bearish
        if(recCount == 443) {
            assertNotNull("Mismatch in some OEMS field", actual.openOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.openOrderTimestamp);
            assertEquals("Mismatch in some OEMS field", "Bullish", actual.prevBassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Bearish", actual.bassoOrderIdea);
            assertEquals("Mismatch in some OEMS field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some OEMS field", "LMT", actual.orderType);
            assertEquals("Mismatch in some OEMS field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.005, actual.currRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.currVolRiskPercent, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.00625, actual.orderQtyPerRisk, 0.0);
            assertEquals("Mismatch in some OEMS field", 75.00375, actual.orderQtyPerVol, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.00625, actual.openOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", 125.00625, actual.currCarryQty, 0.0);
            assertEquals("Mismatch in some OEMS field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some OEMS field", "Init New Order Single", actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", 0.99995, actual.openOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", 1.07047, actual.openOrderSLPrice, 0.0);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderId);
            assertNotNull("Mismatch in some OEMS field", actual.closeOrderTimestamp);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderState);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderQty, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", 0.0, actual.closeOrderPrice, 0.0);
            assertEquals("Mismatch in some OEMS field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some OEMS field", null, actual.closedOrderType);
            assertEquals("Mismatch in some OEMS field", "Init NOS Complete Success - Confirmed", actual.orderConfirmationState);
        }

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