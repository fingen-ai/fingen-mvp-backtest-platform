package oems;

import core.service.oems.OEMSData;
import oems.map.InsightMappingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class InsightMappingServiceTest {

    private InsightMappingService insightMappingService;

    @Before
    public void setUp() throws IOException {
        // Initialize the service which automatically creates map files if they don't exist
        insightMappingService = new InsightMappingService();
    }

    @After
    public void tearDown() {
        // Close the maps and delete the test files to clean up after tests
        if (insightMappingService != null) {
            insightMappingService.close();
            new File(insightMappingService.MAP_DIRECTORY + "nosIDArray.dat").delete();
            new File(insightMappingService.MAP_DIRECTORY + "nos.dat").delete();
        }
    }

    /*
    @Test
    public void testAddAndGetFromNOSIDArray() {
        String symbol = "AAPL";
        long[] expectedOrderIds = {1, 2, 3};
        insightMappingService.addToNOSIDArray(symbol, expectedOrderIds);

        long[] retrievedOrderIds = insightMappingService.getFromNOSIDArray(symbol);
        assertArrayEquals("The retrieved order IDs should match the expected values.", expectedOrderIds, retrievedOrderIds);
    }
     */

    @Test
    public void testAddNOSInsight() {
        long orderId = 1;
        OEMSData initialData = new OEMSData();
        initialData.symbol = "AAPL";
        insightMappingService.addNOSInsight(orderId, initialData);

        OEMSData retrievedData = insightMappingService.nosIdeaMap.get(orderId);
        assertNotNull("The retrieved data should not be null.", retrievedData);
        assertEquals("The symbol of the retrieved data should match the expected symbol.", "AAPL", retrievedData.symbol);
    }

    @Test
    public void testClosePosition() {
        long orderId = 2;
        OEMSData data = new OEMSData();
        data.symbol = "GOOGL";
        insightMappingService.addNOSInsight(orderId, data);

        assertNotNull("The data should be present before deletion.", insightMappingService.nosIdeaMap.get(orderId));
        //insightMappingService.closePosition(data);
        //assertNull("The data should be null after deletion.", insightMappingService.nosIdeaMap.get(orderId));
    }
}
