package core.service.oems;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class OEMSData extends SelfDescribingMarshallable {
    // Price data
    public long recId;
    public double marketCap;
    public long svcStartTs;
    public long svcStopTs;
    public long svcLatency;
    public String symbol;
    public double open;
    public double high;
    public double low;
    public double close;
    public double volume;
    public CharSequence start;
    public CharSequence end;

    // strategy q real-time data
    public double lhcAvgPrice;
    public String bassoOrderIdea;

    // insight q real-time data
    public String openOrderState;
    public String orderType;
    public String orderSide;
    public double openOrderPrice;
    public String openOrderSide;

    // oems q real-time data
    public long openOrderTimestamp;
    public long openOrderId;
    public String openOrderExpiry;
    public String prevBassoOrderIdea;
    public double previousClose;
    public double openOrderQty;
    public double currCarryQty;
    public double atr;
    public double currRiskPercent;
    public double currVolRiskPercent;
    public double orderQtyPerRisk;
    public double orderQtyPerVol;
    public double openOrderSLPrice;

    public long closeOrderTimestamp;
    public long closeOrderId;
    public String closeOrderExpiry;
    public double closeOrderQty;

    public String closeOrderState;
    public String closedOrderType;
    public double closeOrderPrice;
    public String closeOrderSide;
    public String orderConfirmationState;

    public long allRecCount;
    public long nosRecCount;
    public long coaRecCount;
}