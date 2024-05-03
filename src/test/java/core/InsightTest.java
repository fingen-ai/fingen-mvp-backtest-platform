package core;

import core.service.Orchestrator;
import core.service.insight.InsightData;
import core.service.strategy.StrategyData;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import java.io.IOException;

public class InsightTest {

    @Before
    public void setup() throws IOException {
        // Initialize Orchestrator with test configurations, if needed
        Orchestrator.init();
    }

    @Test
    public void testAllQueues() throws Exception {
        // Run the Orchestrator to populate the queues
        Orchestrator.run();

        // Read data from each queue
        StrategyData actualStrategyData = readDataFromQueue("stratQ", StrategyData.class);
        InsightData actualInsightData = readDataFromQueue("insightQ", InsightData.class);

        // Perform validations of each queue vs source file
        // IF statement prevents race-condition that was compromising test-integrity
        // EACH service (Price, Strategy, etc) is pinned to it's own CPU core
        // SO race-conditions are likley
        if(actualStrategyData.start.equals(actualStrategyData.start)) {
            validateInsightData(actualInsightData, actualStrategyData);
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

    // Validate data from strategy svc matches data passed to insight svc
    private void validateInsightData(InsightData actual, StrategyData expected) {
        // Price data, sans start, stop and latency, being diff. svcs and all ;)
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

        // Strategy data
        assertEquals("Mismatch in some Insight field", expected.lhcAvgPrice, actual.lhcAvgPrice, 0.001);
        assertEquals("Mismatch in some Insight field", expected.bassoOrderIdea, actual.bassoOrderIdea);
    }
}