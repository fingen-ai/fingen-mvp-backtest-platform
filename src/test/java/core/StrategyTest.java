package core;

import core.service.Orchestrator;
import core.service.price.PriceData;
import core.service.strategy.StrategyData;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.queue.ExcerptTailer;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.wire.DocumentContext;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StrategyTest {

    int recCount = 0;

    @Before
    public void setup() throws IOException {
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
        List<PriceData> priceDataList = readAllDataFromQueue("priceQ", PriceData.class);
        List<StrategyData> strategyDataList = readAllDataFromQueue("stratQ", StrategyData.class);

        // Assume equal number of records in both queues for simplicity
        assertEquals("Mismatch in number of records", priceDataList.size(), strategyDataList.size());

        // Validate each record pair
        for (int i = 0; i < priceDataList.size(); i++) {
            PriceData actualPriceData = priceDataList.get(i);
            StrategyData actualStrategyData = strategyDataList.get(i);
            if (actualPriceData.recId == actualStrategyData.recId) {
                validateStrategyData(actualPriceData, actualStrategyData);
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
    private void validateStrategyData(PriceData expected, StrategyData actual) {
        // Price data, sans start, stop, and latency, being diff. services and all ;)
        assertEquals("Mismatch in some Price field", expected.recId, actual.recId);
        assertEquals("Mismatch in some Price field", expected.start, actual.start);
        assertEquals("Mismatch in some Price field", expected.end, actual.end);
        assertEquals("Mismatch in some Price field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Price field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Price field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Price field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Price field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Price field", expected.marketCap, actual.marketCap, 0.001);

        // BDD validation
        if(recCount < 49) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);
        }

        if((recCount > 49) && (recCount < 404)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if(recCount == 404) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);
        }

        if((recCount == 405) || (recCount == 406)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        if((recCount > 406) && (recCount < 443)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if(recCount == 443) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        if((recCount > 443) && (recCount < 1066)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if(recCount == 1066) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        if(recCount == 1067) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if(recCount == 1068) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        if((recCount > 1068) && (recCount < 1073)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if((recCount > 1072) && (recCount < 1075)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);
        }

        if((recCount > 1074) && (recCount < 1082)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if(recCount == 1082) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        if((recCount > 1082) && (recCount < 1141)) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        if(recCount == 1141) {
            System.out.println(recCount + " recs");
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }



        recCount++;
    }
}
