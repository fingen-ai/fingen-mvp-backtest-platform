package oems;

import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class OrderMappingServiceTest {

    private OrderMappingService orderMappingService;

    @Before
    public void setUp() throws IOException {
        // Initialize the service which automatically creates map files if they don't exist
        orderMappingService = new OrderMappingService();
    }

    @After
    public void tearDown() {
        // Close the maps and delete the test files to clean up after tests
        if (orderMappingService != null) {
            orderMappingService.close();
            new File(orderMappingService.MAP_DIRECTORY + "nosIDArray.dat").delete();
            new File(orderMappingService.MAP_DIRECTORY + "nos.dat").delete();
        }
    }

    @Test
    public void testAddAndGetFromNOSIDArray() {
        String symbol = "AAPL";
        long[] expectedOrderIds = {1, 2, 3};
        orderMappingService.addToNOSIDArray(symbol, expectedOrderIds);

        long[] retrievedOrderIds = orderMappingService.getFromNOSIDArray(symbol);
        assertArrayEquals("The retrieved order IDs should match the expected values.", expectedOrderIds, retrievedOrderIds);
    }

    @Test
    public void testAddUpdateNOS() {
        long orderId = 1;
        OEMSData initialData = new OEMSData();
        initialData.symbol = "AAPL";
        orderMappingService.addUpdateNOS(orderId, initialData);

        OEMSData retrievedData = orderMappingService.nosMap.get(orderId);
        assertNotNull("The retrieved data should not be null.", retrievedData);
        assertEquals("The symbol of the retrieved data should match the expected symbol.", "AAPL", retrievedData.symbol);
    }

    @Test
    public void testClosePosition() {
        long orderId = 2;
        OEMSData data = new OEMSData();
        data.symbol = "GOOGL";
        orderMappingService.addUpdateNOS(orderId, data);

        assertNotNull("The data should be present before deletion.", orderMappingService.nosMap.get(orderId));
        orderMappingService.closePosition(data);
        assertNull("The data should be null after deletion.", orderMappingService.nosMap.get(orderId));
    }
}
