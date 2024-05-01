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
                        System.out.println("Data read: " + priceData);
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
