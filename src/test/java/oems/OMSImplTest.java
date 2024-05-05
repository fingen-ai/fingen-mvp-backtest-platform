package oems;

import oems.dto.NewOrderSingle;
import oems.dto.BuySell;
import oems.dto.OrderType;
import org.junit.jupiter.api.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class OMSImplTest {

    private OMSImpl oms;

    public OMSImplTest() throws IOException {
        oms = new OMSImpl();  // Initialize OMS implementation
    }

    @BeforeEach
    public void setUp() {
        // This method is called before each test
    }

    @Test
    public void testAddAndRetrieveOrder() {
        // Create a fully initialized NewOrderSingle DTO with test data
        NewOrderSingle nos = new NewOrderSingle();
        nos.symbol(12345L);  // Ensure symbol is set
        nos.transactTime(System.nanoTime());  // Set the transaction time
        nos.orderQty(10.0);  // Ensure quantity is set
        nos.price(250.0);  // Ensure price is set
        nos.side(BuySell.buy);  // Set the side to avoid null
        nos.clOrdID("Order123");  // Ensure client order ID is set
        nos.ordType(OrderType.limit);  // Set the order type to avoid null

        // Ensure no fields are null before proceeding
        assertNotNull(nos.symbol(), "Symbol should not be null");
        assertNotNull(nos.transactTime(), "Transaction time should not be null");
        assertNotNull(nos.orderQty(), "Order quantity should not be null");
        assertNotNull(nos.price(), "Price should not be null");
        assertNotNull(nos.side(), "Side should not be null");
        assertNotNull(nos.clOrdID(), "Client Order ID should not be null");
        assertNotNull(nos.ordType(), "Order Type should not be null");

        // Send the order to be processed by OMSImpl
        oms.addOrderMap(nos.clOrdID(), nos);

        // Use clOrdID as the key to retrieve the order from the map
        NewOrderSingle retrievedOrder = oms.getOrderMap(nos.clOrdID(), new NewOrderSingle());

        // Assert that the stored and retrieved order properties match
        assertNotNull(retrievedOrder, "The retrieved order should not be null");
        assertEquals(nos.symbol(), retrievedOrder.symbol(), "Symbols should match");
        assertEquals(nos.transactTime(), retrievedOrder.transactTime(), "Transaction times should match");
        assertEquals(nos.orderQty(), retrievedOrder.orderQty(), "Order quantities should match");
        assertEquals(nos.price(), retrievedOrder.price(), "Prices should match");
        assertEquals(nos.side(), retrievedOrder.side(), "Sides should match");
        assertEquals(nos.clOrdID(), retrievedOrder.clOrdID(), "Client Order IDs should match");
        assertEquals(nos.ordType(), retrievedOrder.ordType(), "Order types should match");
    }
}
