package oems;

import oems.dto.Order;
import oems.dto.Trade;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BRMTest {

    /*
    @Test
    public void testOrderAndTradeReports() {
        BRM manager = new BRM();

        // Mock orders
        manager.addOrder(new Order("O1", "A1", "AAPL", 100, 150.00, true));
        manager.addOrder(new Order("O2", "A1", "MSFT", 200, 200.00, false));
        manager.addOrder(new Order("O3", "A2", "GOOGL", 150, 2500.00, true));

        // Mock trades
        manager.addTrade(new Trade("T1", "O1", "A1", "AAPL", 50, 152.00, false));
        manager.addTrade(new Trade("T2", "O2", "A1", "MSFT", 200, 201.00, true));
        manager.addTrade(new Trade("T3", "O3", "A2", "GOOGL", 150, 2501.00, false));

        // Update an order and a trade
        manager.updateOrder("O1", 155.00, 110); // Updated price and quantity
        manager.updateTrade("T1", 154.00, 55);  // Updated price and quantity

        // Retrieve updated order and trade
        Order updatedOrder = manager.ordersMap.get("O1");
        Trade updatedTrade = manager.tradesMap.get("T1");

        // Assertions for original lists
        List<Order> openOrders = manager.getOpenOrders();
        List<Order> closedOrders = manager.getClosedOrders();
        List<Trade> openTrades = manager.getOpenTrades();
        List<Trade> closedTrades = manager.getClosedTrades();

        assertEquals(2, openOrders.size(), "Should have two open orders");
        assertEquals(1, closedOrders.size(), "Should have one closed order");
        assertTrue(openOrders.stream().anyMatch(o -> o.getSymbol().equals("AAPL")), "Open orders should include AAPL");
        assertTrue(closedOrders.stream().anyMatch(o -> o.getSymbol().equals("MSFT")), "Closed orders should include MSFT");

        assertEquals(2, openTrades.size(), "Should have two open trades");
        assertEquals(1, closedTrades.size(), "Should have one closed trade");
        assertTrue(openTrades.stream().anyMatch(t -> t.getSymbol().equals("GOOGL")), "Open trades should include GOOGL");
        assertTrue(closedTrades.stream().anyMatch(t -> t.getSymbol().equals("MSFT")), "Closed trades should include MSFT");

        // Assertions for updates
        assertEquals(155.00, updatedOrder.getPrice(), "Updated order price should be 155.00");
        assertEquals(110, updatedOrder.getQuantity(), "Updated order quantity should be 110");
        assertEquals(154.00, updatedTrade.getPrice(), "Updated trade price should be 154.00");
        assertEquals(55, updatedTrade.getQuantity(), "Updated trade quantity should be 55");

        // Clean up resources
        manager.close();
    }

 */
}
