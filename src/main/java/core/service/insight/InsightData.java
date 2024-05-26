package core.service.insight;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class InsightData extends SelfDescribingMarshallable {
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


    // oems q real-time data used in insight svc
    public String prevBassoOrderIdea;
    public double previousClose;
    public double atr;
    public String openOrderExpiry;
}