package oems.dto;

import java.io.Serializable;

public class Trade implements Serializable {
    private final String tradeId;
    private final String orderId;
    private final String accountId;
    private final String symbol;
    private final int quantity;
    private final double price;
    private final boolean isClosed;

    public Trade(String tradeId, String orderId, String accountId, String symbol, int quantity, double price, boolean isClosed) {
        this.tradeId = tradeId;
        this.orderId = orderId;
        this.accountId = accountId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.isClosed = isClosed;
    }

    public String getTradeId() {
        return tradeId;
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

    public boolean isClosed() {
        return isClosed;
    }
}
