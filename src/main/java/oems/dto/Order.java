package oems.dto;

import java.io.Serializable;

public class Order implements Serializable {
    private final String orderId;
    private final String accountId;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final boolean isOpen;

    public Order(String orderId, String accountId, String symbol, int quantity, double price, boolean isOpen) {
        this.orderId = orderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.isOpen = isOpen;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
