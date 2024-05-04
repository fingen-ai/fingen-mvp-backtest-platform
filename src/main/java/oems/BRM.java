package oems;

import net.openhft.chronicle.map.ChronicleMap;
import oems.dto.NewOrderSingle;
import oems.dto.Order;
import oems.dto.Trade;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Broker Report Manager:
 * Gets order and trade information from Chronicle Maps
 * Persists order and trade information in Chronicle Maps
 * Updates existing order and trade information based on market positions
 */
public class BRM {
    ChronicleMap<String, Order> ordersMap;
    ChronicleMap<String, Trade> tradesMap;

    /*
    public BRM() {
        ordersMap = ChronicleMap
                .of(String.class, Order.class)
                .averageKey("order123")
                .averageValue(new Order("order123", "account123", "AAPL", 100, 150.0, true))
                .entries(100)
                .create();

        tradesMap = ChronicleMap
                .of(String.class, Trade.class)
                .averageKey("trade123")
                .averageValue(new Trade("trade123", "order123", "account123", "AAPL", 50, 150.0, false))
                .entries(100)
                .create();
    }

    public void addOrder(NewOrderSingle order) {
        ordersMap.put(order.getOrderId(), order);
    }

    public void updateOrder(String orderId, double newPrice, int newQuantity) {
        ordersMap.computeIfPresent(orderId, (id, order) -> new Order(order.getOrderId(), order.getAccountId(), order.getSymbol(), newQuantity, newPrice, order.isOpen()));
    }

    public void addTrade(Trade trade) {
        tradesMap.put(trade.getTradeId(), trade);
    }

    public void updateTrade(String tradeId, double newPrice, int newQuantity) {
        tradesMap.computeIfPresent(tradeId, (id, trade) -> new Trade(trade.getTradeId(), trade.getOrderId(), trade.getAccountId(), trade.getSymbol(), newQuantity, newPrice, trade.isClosed()));
    }

    public List<Order> getOpenOrders() {
        return ordersMap.values().stream().filter(Order::isOpen).collect(Collectors.toList());
    }

    public List<Order> getClosedOrders() {
        return ordersMap.values().stream().filter(order -> !order.isOpen()).collect(Collectors.toList());
    }

    public List<Trade> getOpenTrades() {
        return tradesMap.values().stream().filter(trade -> !trade.isClosed()).collect(Collectors.toList());
    }

    public List<Trade> getClosedTrades() {
        return tradesMap.values().stream().filter(Trade::isClosed).collect(Collectors.toList());
    }

    public void close() {
        ordersMap.close();
        tradesMap.close();
    }

     */
}
