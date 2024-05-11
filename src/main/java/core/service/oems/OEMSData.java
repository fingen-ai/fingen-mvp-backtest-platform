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
    public double previousClose;
    public double atr;
    public double currRiskPercent;
    public double currVolRiskPercent;
    public int orderQtyPerRisk;
    public int orderQtyPerVol;
    public String orderType;
    public String orderSide;

    public int openOrderQty;
    public String openOrderSide;
    public double openOrderPrice;

    // oems q real-time data
    public long openOrderId;
    public long openOrderTimestamp;
    public String openOrderExpiry;
    public String openOrderState;

    public double openOrderSLPrice;
    public String prevBassoOrderIdea;

    public long closeOrderId;
    public long closeOrderTimestamp;
    public String closeOrderExpiry;
    public String closeOrderState;
    public int closeOrderQty;
    public String closeOrderSide;
    public double closeOrderPrice;
}