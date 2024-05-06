package core.service.oems;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

import java.math.BigDecimal;
import java.util.Date;

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
    public double bid;
    public double ask;
    public BigDecimal bidSize;
    public BigDecimal askSize;
    public BigDecimal vwap;
    public BigDecimal percentageChange;
    public BigDecimal quoteVolume;
    public Date timestamp;
    public BigDecimal last;
    public CharSequence start;
    public CharSequence end;

    // strategy q real-time data
    public double lhcAvgPrice;
    public String bassoOrderIdea;

    // insight q real-time data
    public double nav;
    public double positionRisk;
    public int tradeCount;
    public double atr;
    public double priorClose;
    public double riskInitPercentThreshold;
    public double volInitPercentThreshold;
    public double riskOngoingPercentThreshold;
    public double volOngoingPercentThreshold;
    public double currentTotalPercentRiskPercent;
    public double currentTotalPercentVolRiskPercent;
    public String tradeDecisionInstruction;
    public double tradeQtyPerRiskInstruction;
    public double tradeQtyPerVolInstruction;
    public double tradeQtyInstruction;

    // oems q real-time data
    public String openOrderId;
    public double openOrderTimestamp;
    public double openOrderState;
    public int openOrderQty;
    public String openOrderSide;
    public double openOrderPrice;
    public double openOrderExpiry;

    public double closeOrderId;
    public double closeOrderTimestamp;
    public double closeOrderState;
    public double closeOrderQty;
    public String closeOrderSide;
    public double closeOrderPrice;
    public double closeOrderExpiry;

}
