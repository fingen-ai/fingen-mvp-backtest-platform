package core;

import core.service.Orchestrator;
import core.service.insight.InsightData;
import core.service.oems.OEMSData;
import core.service.price.PriceData;
import core.service.strategy.StrategyData;
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
import java.util.ArrayList;
import java.util.List;

public class InsightTest {

    int recCount = 0;
    OrderMappingService orderMS = new OrderMappingService();

    public InsightTest() throws IOException {
    }

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
        List<StrategyData> strategyDataList = readAllDataFromQueue("stratQ", StrategyData.class);
        List<InsightData> insightDataList = readAllDataFromQueue("insightQ", InsightData.class);

        // Assume equal number of records in both queues for simplicity
        assertEquals("Mismatch in number of records", strategyDataList.size(), insightDataList.size());

        // Validate each record pair
        for (int i = 0; i < strategyDataList.size(); i++) {
            StrategyData actualStrategyData = strategyDataList.get(i);
            InsightData actualInsightData = insightDataList.get(i);
            if (actualStrategyData.recId == actualInsightData.recId) {
                validateDTOAndQueuesIntegration(actualStrategyData, actualInsightData);
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
    private void validateDTOAndQueuesIntegration(StrategyData expected, InsightData actual) {
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

        // BDD validation
        System.out.println("REC: " + recCount);
        System.out.println("IDEA DE BASSO: " + actual.bassoOrderIdea);

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

            // neutral means no records
            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertNull("Mismatch in some Order Map Svc field", oemsData);
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
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Init Insight", actual.openOrderState);

            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertEquals("Mismatch in some Order Map Svc field", 0, actual.openOrderId);
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

            // neutral means no records
            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertNull("Mismatch in some Order Map Svc field", oemsData);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 405) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);

            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Sell", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 30, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Sell", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.9911, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Init Insight", actual.openOrderState);

            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertEquals("Mismatch in some Order Map Svc field", 0, actual.openOrderId);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 407) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);

            System.out.println("ACTUAL" + actual);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0.0, actual.currVolRiskPercent, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerRisk, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.orderQtyPerVol, 0.001);
            assertEquals("Mismatch in some Insight field", "Limit", actual.orderType);
            assertEquals("Mismatch in some Insight field", "Buy", actual.orderSide);
            assertEquals("Mismatch in some Insight field", 30, actual.openOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", "Buy", actual.openOrderSide);
            assertEquals("Mismatch in some Insight field", 0.9981, actual.openOrderPrice, 0.001);
            assertEquals("Mismatch in some Insight field", 0, actual.closeOrderQty, 0.001);
            assertEquals("Mismatch in some Insight field", null, actual.closeOrderSide);
            assertEquals("Mismatch in some Insight field", 0.0, actual.closeOrderPrice, 0.001);

            // OEMS data created as Insights via Order Mapping and InsightPub services
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderId);
            assertEquals("Mismatch in some OEMS field", 0, actual.openOrderTimestamp, 0.001);
            assertEquals("Mismatch in some Insight field", "GTC", actual.openOrderExpiry);
            assertEquals("Mismatch in some Insight field", "Init Insight", actual.openOrderState);

            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertEquals("Mismatch in some Order Map Svc field", 0, actual.openOrderId);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 443) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 444) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1066) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1067) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1068) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1069) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
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

            // neutral means no records
            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertNull("Mismatch in some Order Map Svc field", oemsData);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1075) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1082) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1083) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        // BEARISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1141) {
            assertEquals("Mismatch in some Strategy field", "Bearish", actual.bassoOrderIdea);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1142) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
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

            // neutral means no records
            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertNull("Mismatch in some Order Map Svc field", oemsData);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1154) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
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

            // neutral means no records
            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertNull("Mismatch in some Order Map Svc field", oemsData);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1163) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
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

            // neutral means no records
            long[] nosIDArray = orderMS.getFromNOSIDArray(actual.symbol);
            assertNull("Mismatch in some Order Map Svc field", nosIDArray);
            OEMSData oemsData = orderMS.getNOS(actual.openOrderId);
            assertNull("Mismatch in some Order Map Svc field", oemsData);
        }

        // BULLISH: BEG OF TREND: CONFIRM OPEN & MAPS
        if(recCount == 1174) {
            assertEquals("Mismatch in some Strategy field", "Bullish", actual.bassoOrderIdea);
        }

        recCount++;
    }
}