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

import java.io.IOException;

import static core.service.Orchestrator.priceData;
import static org.junit.Assert.assertEquals;

public class StrategyTest {

    @Before
    public void setup() throws IOException {
        // Initialize Orchestrator with test configurations, if needed
        //Orchestrator.init();
    }

    @Test
    public void testAllQueues() throws Exception {
        // Run the Orchestrator to populate the queues
        //Orchestrator.run();

        // Get expected data
        //PriceData expectedPriceData = new PriceData();

        //expectedPriceData = printRecord(record);

        // Validate data from each queue
        //PriceData actualPriceData = readDataFromQueue("priceQ", PriceData.class);

        // Perform validations
        //if(expectedPriceData.start.equals(actualPriceData.start)) {
        //validatePriceData(actualPriceData, expectedPriceData);
        //}
    }

    private <T> T readDataFromQueue(String queueName, Class<T> type) throws IOException {
        /*String queuePath = OS.TMP + "/HiveMain/Queues/" + queueName;
        try (SingleChronicleQueue queue = SingleChronicleQueueBuilder.binary(queuePath).build()) {
            ExcerptTailer tailer = queue.createTailer();
            try (DocumentContext dc = tailer.readingDocument()) {
                if (dc.isPresent() && dc.wire() != null) {
                    return dc.wire().read().object(type);
                }
            }
        }
        throw new IOException("Failed to read data from queue: " + queueName);
         */

        return null;
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
