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
    /*
    public BigDecimal bid;
    public BigDecimal ask;
    public BigDecimal bidSize;
    public BigDecimal askSize;
    public BigDecimal vwap;
    public BigDecimal percentageChange;
    public BigDecimal quoteVolume;
    public Date timestamp;
    public BigDecimal last;

     */
    public CharSequence start;
    public CharSequence end;

    // strategy q real-time data
    public double lhcAvgPrice;
    public String bassoOrderIdea;

    // insight q real-time data
    public double nav;
    public double atr;
    public double priorClose;
    public double initRiskPercentThreshold;
    public double initVolPercentThreshold;
    public double ongoingRiskPercentThreshold;
    public double ongoingVolPercentThreshold;
    public double currRiskPercent;
    public double currVolRiskPercent;
    public double orderQtyPerRisk;
    public double orderQtyPerVol;
    public double orderQty;
    public String orderSide;
    public String orderType;
    public String nosOrderInsight;
    public String updOrderInsight;
    public String coaOrderInsight;

    // oems q real-time data
    public long openOrderId;
    public double openOrderTimestamp;
    public double openOrderState;
    public int openOrderQty;
    public String openOrderSide;
    public double openOrderPrice;
    public double openOrderExpiry;

    public long closeOrderId;
    public double closeOrderTimestamp;
    public double closeOrderState;
    public double closeOrderQty;
    public String closeOrderSide;
    public double closeOrderPrice;
    public double closeOrderExpiry;

}
