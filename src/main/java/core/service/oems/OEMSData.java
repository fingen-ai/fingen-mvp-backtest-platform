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

    public long closeOrderId;
    public long closeOrderTimestamp;
    public String closeOrderExpiry;
    public String closeOrderState;
    public String closeOrderSide;
    public double closeOrderPrice;
    public String orderConfirmationState;
}