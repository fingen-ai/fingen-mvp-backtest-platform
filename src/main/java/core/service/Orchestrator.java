package core.service;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import core.service.insight.InsightPubImpl;
import core.service.insight.InsightWrapper;
import core.service.oems.OEMSPubImpl;
import core.service.oems.OEMSWrapper;
import core.service.ops.OpsPubImpl;
import core.service.ops.OpsWrapper;
import core.service.performance.PerfPubImpl;
import core.service.performance.PerfWrapper;
import core.service.price.PriceData;
import core.service.price.PricePub;
import core.service.publisher.PublisherPubImpl;
import core.service.publisher.PublisherWrapper;
import core.service.strategy.StrategyPubImpl;
import core.service.strategy.StrategyWrapper;
import core.util.CSVFileReader;

import java.io.IOException;

public class Orchestrator {

    public static PriceData priceData = new PriceData();

    private static PricePub pricePubIn;

    public static void init() throws IOException {

        String priceQ = OS.TMP + "/HiveMain/Queues/priceQ";
        String stratQ = OS.TMP + "/HiveMain/Queues/stratQ";
        String insightQ = OS.TMP + "/HiveMain/Queues/insightQ";
        String oemsQ = OS.TMP + "/HiveMain/Queues/oemsQ";
        String perfQ = OS.TMP + "/HiveMain/Queues/perfQ";
        String pubQ = OS.TMP + "/HiveMain/Queues/pubQ";
        String opsQ = OS.TMP + "/HiveMain/Queues/opsQ";

        // Cleanup queues
        IOTools.deleteDirWithFiles(priceQ, 2);
        IOTools.deleteDirWithFiles(stratQ, 2);
        IOTools.deleteDirWithFiles(insightQ, 2);
        IOTools.deleteDirWithFiles(oemsQ, 2);
        IOTools.deleteDirWithFiles(perfQ, 2);
        IOTools.deleteDirWithFiles(pubQ, 2);
        IOTools.deleteDirWithFiles(opsQ, 2);

        pricePubIn = SingleChronicleQueueBuilder.binary(priceQ).build().acquireAppender().methodWriter(PricePub.class);
        StrategyWrapper<StrategyPubImpl> strategyPublisherIn = new StrategyWrapper<>(priceQ, stratQ, new StrategyPubImpl());
        InsightWrapper<InsightPubImpl> insightPublisherIn = new InsightWrapper<>(stratQ, insightQ, new InsightPubImpl());
        OEMSWrapper<OEMSPubImpl> oemsPublisherIn = new OEMSWrapper<>(insightQ, oemsQ, new OEMSPubImpl());
        PerfWrapper<PerfPubImpl> perfPublisherIn = new PerfWrapper<>(oemsQ, perfQ, new PerfPubImpl());
        PublisherWrapper<PublisherPubImpl> publisherPublisherIn = new PublisherWrapper<>(perfQ, pubQ, new PublisherPubImpl());
        OpsWrapper<OpsPubImpl> opsPublisherIn = new OpsWrapper<>(pubQ, opsQ, new OpsPubImpl());
    }

    public static void run() {
        String userHome = System.getProperty("user.home");
        String filePath = userHome + "/FinGen/Test_Data/usd-coin_2018-10-08_2024-04-21.csv";

        CSVFileReader csvFileReader = new CSVFileReader(filePath);

        try {
            csvFileReader.openStream();
            String[] headers = csvFileReader.readNext(); // Assuming the first row contains headers
            String[] record;
            while ((record = csvFileReader.readNext()) != null) {
                priceData = printRecord(record);
                pricePubIn.simpleCall(priceData);
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
    }

    public static void main(String[] args) throws IOException {
        init();
        run();
    }

    /**
     * Parses and prints the structured data from a CSV record.
     * @param record A string array containing comma-separated values of one record.
     */
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
