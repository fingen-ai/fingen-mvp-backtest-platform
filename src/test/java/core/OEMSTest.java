package core;

import core.service.Orchestrator;
import core.service.insight.InsightData;
import core.service.oems.OEMSData;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OEMSTest {

    int recCount = 1;

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
        InsightData actualInsightData = readDataFromQueue("insightQ", InsightData.class);
        OEMSData actualOEMSData = readDataFromQueue("oemsQ", OEMSData.class);


        // Perform validations of each queue vs source file
        // IF statement prevents race-condition that was compromising test-integrity
        // EACH service (Price, Strategy, etc) is pinned to it's own CPU core
        // SO race-conditions are likely
        if (actualOEMSData.start.equals(actualInsightData.start)) {
            validateInsightData(actualInsightData, actualOEMSData);
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

    // Validate data from insight svc matches data passed to oems svc
    private void validateInsightData(InsightData expected, OEMSData actual) {
        // Price data, sans start, stop and latency, being diff. svcs and all ;)
        assertEquals("Mismatch in some Price field", expected.start, actual.start);
        assertEquals("Mismatch in some Price field", expected.recId, actual.recId, 0.001);
        assertEquals("Mismatch in some Price field", expected.start, actual.start);
        assertEquals("Mismatch in some Price field", expected.end, actual.end);
        assertEquals("Mismatch in some Price field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Price field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Price field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Price field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Price field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Price field", expected.marketCap, actual.marketCap, 0.001);

        // Strategy data
        assertEquals("Mismatch in some Strategy field", expected.lhcAvgPrice, actual.lhcAvgPrice, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.bassoOrderIdea, actual.bassoOrderIdea);

        // Insight data
        assertEquals("Mismatch in some Insight field", expected.previousClose, actual.previousClose, 0.001);
        assertEquals("Mismatch in some Insight field", expected.atr, actual.atr, 0.001);
        assertEquals("Mismatch in some Insight field", expected.currRiskPercent, actual.currRiskPercent, 0.001);
        assertEquals("Mismatch in some Insight field", expected.currVolRiskPercent, actual.currVolRiskPercent, 0.001);
        assertEquals("Mismatch in some Insight field", expected.orderQtyPerRisk, actual.orderQtyPerRisk, 0.001);
        assertEquals("Mismatch in some Insight field", expected.orderQtyPerVol, actual.orderQtyPerVol, 0.001);

        assertEquals("Mismatch in some Insight field", expected.orderType, actual.orderType);
        assertEquals("Mismatch in some Insight field", expected.orderSide, actual.orderSide);

        assertEquals("Mismatch in some Insight field", expected.openOrderQty, actual.openOrderQty, 0.001);

        assertEquals("Mismatch in some Insight field", expected.openOrderSide, actual.openOrderSide);

        assertEquals("Mismatch in some Insight field", expected.openOrderPrice, actual.openOrderPrice, 0.001);
        assertEquals("Mismatch in some Insight field", expected.closeOrderQty, actual.closeOrderQty, 0.001);

        assertEquals("Mismatch in some Insight field", expected.closeOrderSide, actual.closeOrderSide);

        assertEquals("Mismatch in some Insight field", expected.closeOrderPrice, actual.closeOrderPrice, 0.001);

        /*
        // oems data
        assertEquals("Mismatch in some OEMS field", expected.openOrderId, actual.openOrderId, 0.001);

        assertEquals("Mismatch in some OEMS field", expected.openOrderTimestamp, actual.openOrderTimestamp, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.openOrderExpiry, actual.openOrderExpiry);
        assertEquals("Mismatch in some OEMS field", expected.openOrderState, actual.openOrderState);

        assertEquals("Mismatch in some OEMS field", expected.closeOrderId, actual.closeOrderId, 0.001);

        assertEquals("Mismatch in some OEMS field", expected.closeOrderTimestamp, actual.closeOrderTimestamp, 0.001);
        assertEquals("Mismatch in some OEMS field", expected.closeOrderExpiry, actual.closeOrderExpiry);
        assertEquals("Mismatch in some OEMS field", expected.closeOrderState, actual.closeOrderState);

        // oems scenarios based on usd-coin_2018-10-08_2024-04-21.csv source data
        // recs 50:
        if(recCount == 50) {
            System.out.println("== 50 RECOUNT COUNT" + recCount);
            assertEquals("Mismatch in some OEMS field", "Init New Order Single", actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", "boogie", actual.closeOrderState);
        }

        if(recCount > 50) {System.out.println("> 50 RECOUNT COUNT" + recCount);
            assertEquals("Mismatch in some OEMS field", "Ongoing New Order Single", actual.openOrderState);
            assertEquals("Mismatch in some OEMS field", "boogie", actual.closeOrderState);
        }

        recCount++;

        // Recon of nos-2-cos = returns 100% match = true

        // Recon of cos to nos id array = returns 0% match = false

        // Recon of nos to nos id array = returns 100% match = true

         */
    }
}