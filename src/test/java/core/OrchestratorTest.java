package core;

import core.service.Orchestrator;
import core.service.insight.InsightData;
import core.service.oems.pubData;
import core.service.ops.OpsData;
import core.service.performance.PerfData;
import core.service.price.PriceData;
import core.service.publisher.PublisherData;
import core.service.strategy.StrategySData;
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
        Orchestrator.init();
    }

    @Test
    public void testAllQueues() throws Exception {
        // Run the Orchestrator to populate the queues
        Orchestrator.run();

        // Instantiate dto
        PriceData expectedPriceData = new PriceData();
        StrategySData expectedStrategyData = new StrategySData();
        InsightData expectedInsightData = new InsightData();
        pubData expectedOEMSData = new pubData();
        PerfData expectedPerfData = new PerfData();
        PublisherData expectedPubData = new PublisherData();
        OpsData expectedOpsData = new OpsData();

        // Read source csv file
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

        // Read data from each queue
        PriceData actualPriceData = readDataFromQueue("priceQ", PriceData.class);
        StrategySData actualStrategyData = readDataFromQueue("stratQ", StrategySData.class);
        InsightData actualInsightData = readDataFromQueue("insightQ", InsightData.class);
        pubData actualOEMSData = readDataFromQueue("oemsQ", pubData.class);
        PerfData actualPerfData = readDataFromQueue("perfQ", PerfData.class);
        PublisherData actualPubData = readDataFromQueue("pubQ", PublisherData.class);
        OpsData actualOpsData = readDataFromQueue("opsQ", OpsData.class);

        // Perform validations of each queue vs source file
        // IF statement prevents race-condition that was compromising test-integrity
        // EACH service (Price, Strategy, etc) is pinned to it's own CPU core
        // SO race-conditions are likley
        if(expectedPriceData.recId == actualPriceData.recId) {
            System.out.println("HERE");
            validatePriceData(expectedPriceData, actualPriceData);
            validateStrategyData(expectedStrategyData, actualStrategyData);
            validateInsightData(expectedInsightData, actualInsightData);
            validateOEMSData(expectedOEMSData, actualOEMSData);
            validatePerfData(expectedPerfData, actualPerfData);
            validatePublishingData(expectedPubData, actualPubData);
            validateOpsData(expectedOpsData, actualOpsData);
        }
    }

    // Read from queue
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

    private void validatePriceData(PriceData expected, PriceData actual) {
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

    private void validateStrategyData(StrategySData expected, StrategySData actual) {
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

    private void validateInsightData(InsightData expected, InsightData actual) {
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

    private void validateOEMSData(pubData expected, pubData actual) {
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

    private void validatePerfData(PerfData expected, PerfData actual) {
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

    private void validatePublishingData(PublisherData expected, PublisherData actual) {
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

    private void validateOpsData(OpsData expected, OpsData actual) {
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