package core;

import core.service.Orchestrator;
import core.service.insight.InsightData;
import core.service.oems.OEMSData;
import core.service.ops.OpsData;
import core.service.performance.PerfData;
import core.service.price.PriceData;
import core.service.publisher.PublisherData;
import core.service.strategy.StrategyData;
import core.util.CSVFileReader;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import static core.service.Orchestrator.priceData;
import static org.junit.Assert.*;
import java.io.IOException;

public class OrchestratorTest {

    @Before
    public void setup() throws IOException {
        // Initialize Orchestrator with test configurations, if needed
        String[] instruments = {"TestInstrument"}; // example instruments array
        Orchestrator.init(instruments);
    }

    @Test
    public void testAllQueues() throws Exception {
        // Run the Orchestrator to populate the queues
        Orchestrator.run();

        // Get expected data
        PriceData expectedPriceData = new PriceData();
        StrategyData expectedStrategyData = new StrategyData();
        InsightData expectedInsightData = new InsightData();
        OEMSData expectedOEMSData = new OEMSData();
        PerfData expectedPerfData = new PerfData();
        PublisherData expectedPubData = new PublisherData();
        OpsData expectedOpsData = new OpsData();

        String userHome = System.getProperty("user.home");
        String filePath = userHome + "/FinGen/Test_Data/usd-coin_2018-10-08_2024-04-21.csv";
        CSVFileReader csvFileReader = new CSVFileReader(filePath);
        try {
            csvFileReader.openStream();
            String[] headers = csvFileReader.readNext(); // Assuming the first row contains headers
            String[] record;
            while ((record = csvFileReader.readNext()) != null) {
                expectedPriceData = printRecord(record);
            }
        } catch (IOException e) {
            System.err.println("Error processing the CSV file: " + e.getMessage());
        } finally {
            try {
                csvFileReader.closeStream();
            } catch (IOException e) {
                System.err.println("Error closing the stream: " + e.getMessage());
            }
        }

        // Validate data from each queue
        PriceData actualPriceData = readDataFromQueue("priceQ", PriceData.class);
        StrategyData actualStrategyData = readDataFromQueue("stratQ", StrategyData.class);
        InsightData actualInsightData = readDataFromQueue("stratQ", InsightData.class);
        OEMSData actualOEMSData = readDataFromQueue("oemsQ", OEMSData.class);
        PerfData actualPerfData = readDataFromQueue("perfQ", PerfData.class);
        PublisherData actualPubData = readDataFromQueue("pubQ", PublisherData.class);
        OpsData actualOpsData = readDataFromQueue("opsQ", OpsData.class);

        // Perform validations
        if(expectedPriceData.start.equals(actualPriceData.start)) {
            validatePriceData(actualPriceData, expectedPriceData);
            validateStrategyData(actualStrategyData, expectedStrategyData);
            validateInsightData(actualInsightData, expectedInsightData);
            validateOEMSData(actualOEMSData, expectedOEMSData);
            validatePerfData(actualPerfData, expectedPerfData);
            validatePublishingData(actualPubData, expectedPubData);
            validateOpsData(actualOpsData, expectedOpsData);
        }
    }

    private <T> T readDataFromQueue(String queueName, Class<T> type) throws IOException {
        String queuePath = OS.TMP + "/HiveMain/Queues/" + queueName;
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
            ExcerptTailer tailer = queue.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                if (dc.isPresent() && dc.wire() != null) {
                    return dc.wire().read().object(type);
                }
            }
        }
        throw new IOException("Failed to read data from queue: " + queueName);
    }

    private void validatePriceData(PriceData actual, PriceData expected) {
        assertEquals("Mismatch in some price field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some price field", expected.start, actual.start);
        assertEquals("Mismatch in some price field", expected.end, actual.end);
        assertEquals("Mismatch in some price field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some price field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some price field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some price field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some price field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some price field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some price field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some price field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some price field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private void validateStrategyData(StrategyData actual, StrategyData expected) {
        assertEquals("Mismatch in some Strategy field", expected.start, actual.start);
        assertEquals("Mismatch in some Strategy field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.start, actual.start);
        assertEquals("Mismatch in some Strategy field", expected.end, actual.end);
        assertEquals("Mismatch in some Strategy field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private void validateInsightData(InsightData actual, InsightData expected) {
        assertEquals("Mismatch in some Insight field", expected.start, actual.start);
        assertEquals("Mismatch in some Insight field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some Insight field", expected.start, actual.start);
        assertEquals("Mismatch in some Insight field", expected.end, actual.end);
        assertEquals("Mismatch in some Insight field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Insight field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Insight field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Insight field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Insight field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Insight field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some Insight field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some Insight field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some Insight field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private void validateOEMSData(OEMSData actual, OEMSData expected) {
        assertEquals("Mismatch in some OEMS field", expected.start, actual.start);
        assertEquals("Mismatch in some OEMS field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.start, actual.start);
        assertEquals("Mismatch in some OEMS field", expected.end, actual.end);
        assertEquals("Mismatch in some OEMS field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private void validatePerfData(PerfData actual, PerfData expected) {
        assertEquals("Mismatch in some Perf field", expected.start, actual.start);
        assertEquals("Mismatch in some Perf field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some Perf field", expected.start, actual.start);
        assertEquals("Mismatch in some Perf field", expected.end, actual.end);
        assertEquals("Mismatch in some Perf field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Perf field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Perf field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Perf field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Perf field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Perf field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some Perf field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some Perf field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some Perf field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private void validatePublishingData(PublisherData actual, PublisherData expected) {
        assertEquals("Mismatch in some Publishing field", expected.start, actual.start);
        assertEquals("Mismatch in some Publishing field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.start, actual.start);
        assertEquals("Mismatch in some Publishing field", expected.end, actual.end);
        assertEquals("Mismatch in some Publishing field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some Publishing field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private void validateOpsData(OpsData actual, OpsData expected) {
        assertEquals("Mismatch in some Ops field", expected.start, actual.start);
        assertEquals("Mismatch in some Ops field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some Ops field", expected.start, actual.start);
        assertEquals("Mismatch in some Ops field", expected.end, actual.end);
        assertEquals("Mismatch in some Ops field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Ops field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Ops field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Ops field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Ops field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Ops field", expected.marketCap, actual.marketCap, 0.001);
        assertEquals("Mismatch in some Ops field", expected.svcStartTs, actual.svcStartTs, 0.001);
        assertEquals("Mismatch in some Ops field", expected.svcStopTs, actual.svcStopTs, 0.001);
        assertEquals("Mismatch in some Ops field", expected.svcLatency, actual.svcLatency, 0.001);
    }

    private static PriceData printRecord(String[] record) {
        if (record.length >= 8) { // Ensure there are enough elements in the record
            priceData.recId = System.nanoTime();
            priceData.svcStartTs = priceData.recId;

            // 8 data elements within the record array
            priceData.start = record[0];
            priceData.end = record[1];
            priceData.open = Double.parseDouble(record[2].trim());
            priceData.high = Double.parseDouble(record[3].trim());
            priceData.low = Double.parseDouble(record[4].trim());
            priceData.close = Double.parseDouble(record[5].trim());
            priceData.volume = Double.parseDouble(record[6].trim());
            priceData.marketCap = Double.parseDouble(record[7].trim());

            priceData.svcStopTs = System.nanoTime();
            priceData.svcLatency = priceData.svcStopTs - priceData.svcStartTs;
        }
        return priceData;
    }
}


/*
package core;

import core.service.price.PriceData;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Test;
import java.io.IOException;

public class OrchestratorTest {

    static String priceQ = OS.TMP + "/HiveMain/Queues/priceQ";
    private static final String PRICE_QUEUE_PATH = priceQ;

    @Test
    public void testReadAllPriceQueueRecords() throws IOException {
        String path = PRICE_QUEUE_PATH;
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(path).build()) {
            ExcerptTailer tailer = queue.createTailer();

            while (true) {
                try (DocumentContext dc = tailer.readingDocument()) {
                    if (!dc.isPresent()) {
                        break; // Break the loop when no more entries are present
                    }
                    PriceData priceData = dc.wire().read().object(PriceData.class);
                    if (priceData != null) {
                        System.out.println("PriceData read: " + priceData);
                    } else {
                        System.out.println("Reached an entry that couldn't be deserialized to PriceData.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}
 */
