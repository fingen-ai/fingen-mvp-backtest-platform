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

    int recCount = 0;
    OrderMappingService orderMS = new OrderMappingService();

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
        Thread.sleep(10000); // Adjust time as needed based on your system's performance

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
        assertEquals("Mismatch in some Price field", expected.open, actual.open, 0.001);
        assertEquals("Mismatch in some Price field", expected.high, actual.high, 0.001);
        assertEquals("Mismatch in some Price field", expected.low, actual.low, 0.001);
        assertEquals("Mismatch in some Price field", expected.close, actual.close, 0.001);
        assertEquals("Mismatch in some Price field", expected.volume, actual.volume, 0.001);
        assertEquals("Mismatch in some Price field", expected.marketCap, actual.marketCap, 0.001);

        // Strategy data
        assertEquals("Mismatch in some Strategy field", expected.lhcAvgPrice, actual.lhcAvgPrice, 0.001);
        assertEquals("Mismatch in some Strategy field", expected.bassoOrderIdea, actual.bassoOrderIdea);

        // NEUTRAL
        if(recCount < 49) {
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.orderType);
            assertEquals("Mismatch in some Insight field", null, actual.orderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.openOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 49) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 30, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertNotEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertNotEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Init  New Order Single", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // NEUTRAL
        if(recCount == 404) {
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.orderType);
            assertEquals("Mismatch in some Insight field", null, actual.orderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.openOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 405) {
            System.out.println("EXPECTED: " + expected + " - " + recCount);
            //System.out.println("ACTUAL: " + actual + " - " + recCount);
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0029733, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.9911, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 407) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0029942999999999997, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.9981, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 443) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.00299985, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.99995, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 444) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.00299985, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.99995, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1066) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1067) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0029900596114223964, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.9966865371407988, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1068) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.003005159735413451, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.0017199118044837, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1069) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.00300476427215574, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.00158809071858, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // NEUTRAL
        if(recCount == 1073) {
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.orderType);
            assertEquals("Mismatch in some Insight field", null, actual.orderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1075) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0030054025945269514, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.0018008648423171, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1082) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.002977751576540899, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.9925838588469663, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1083) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.003001533550334979, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.0005111834449931, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1141) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0030004093020000004, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.000136434, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1142) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.003001184013, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.000394671, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // NEUTRAL
        if(recCount == 1153) {
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.orderType);
            assertEquals("Mismatch in some Insight field", null, actual.orderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1154) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.00299821959, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.99940653, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // NEUTRAL
        if(recCount == 1162) {
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.orderType);
            assertEquals("Mismatch in some Insight field", null, actual.orderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1163) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0029991378779999998, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 70, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 39, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 39, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.999712626, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        // NEUTRAL
        if(recCount == 1173) {
            assertEquals("Mismatch in some Strategy field", "Neutral", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.orderType);
            assertEquals("Mismatch in some Insight field", null, actual.orderSide);
            assertEquals("Mismatch in some Insight field", 0, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Hold", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1174) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.00300008628, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 69, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 38, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 38, actual.openOrderQty);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 1.00002876, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Ongoing Insight", actual.openOrderState);

            // OEMS data
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderId, 0.001);
            assertEquals("Mismatch in some OEMS field", 0, actual.closeOrderTimestamp, 0.001);
            assertNull("Mismatch in some OEMS field", actual.closeOrderExpiry);
            assertNull("Mismatch in some OEMS field", actual.closeOrderState);
        }

        recCount++;
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