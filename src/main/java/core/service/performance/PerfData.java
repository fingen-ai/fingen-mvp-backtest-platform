package core.service.performance;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class PerfData extends SelfDescribingMarshallable {
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
    public String prevBassoOrderIdea;
    public long openOrderTimestamp;
    public long openOrderId;
    public String openOrderExpiry;
    public double openOrderQty;
    public double orderQtyPerRisk;
    public double orderQtyPerVol;
    public double openOrderSLPrice;
    public double previousClose;
    public double currCarryQty;
    public double atr;
    public double currRiskPercent;
    public double currVolRiskPercent;

    public long coaOpenOrderId;
    public long coaCloseOrderId;

    public double coaCloseOrderPrice;
    public double coaOpenOrderPrice;

    public long coaOpenOrderTimestamp;
    public long coaCloseOrderTimestamp;

    public String coaOpenOrderSide;
    public String coaCloseOrderSide;

    public String coaOpenOrderExpiry;
    public String coaCloseOrderExpiry;

    public double coaOpenOrderQty;
    public double coaCloseOrderQty;

    public String coaOpenOrderState;
    public String coaCloseOrderState;

    public String coaOpenOrderType;
    public String coaClosedOrderType;

    public long allRecCount;
    public long nosRecCount;
    public long coaRecCount;

    // perf q real-time data
    public double cagrPercentage;
    public double sharpeRatio;
    public double sortinoRatio;
    public double returnToAvgDrawdown;
    public double marRatio;
    public double maxDrawdownPercentage;
    public double winCount;
    public double lossCount;
    public double reliabilityPercentage;
    public double totalProfit;
    public double profitFactor;
    public double roi;
    public double netROI;
    public double initialInvestment;
    public double nav;
}
