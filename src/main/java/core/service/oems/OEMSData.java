package core.service.oems;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

import java.math.BigDecimal;
import java.util.Date;

public class OEMSData extends SelfDescribingMarshallable {
    public long recId;
    public double marketCap;
    public long svcStartTs;
    public long svcStopTs;
    public long svcLatency;
    public String instrument;
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
    public double tradeAmtPerRiskInstruction;
    public double tradeAmtPerVolInstruction;
    public double tradeAmtInstruction;

    // oems q real-time data
    public String openOrderId;
    public double openOrderTimestamp;
    public double openOrderState;
    public double openOrderAmt;
    public double openOrderPrice;
    public double openOrderExpiry;

    public double closeOrderId;
    public double closeOrderTimestamp;
    public double closeOrderState;
    public double closeOrderAmt;
    public double closeOrderPrice;
    public double closeOrderExpiry;

    public double openTradeId;
    public double openTradeTimestamp;
    public double openTradeState;
    public double openTradeAmt;
    public double openTradePrice;
    public double openTradeExpiry;

    public double closeTradeId;
    public double closeTradeTimestamp;
    public double closeTradeState;
    public double closeTradeAmt;
    public double closeTradePrice;
    public double closeTradeExpiry;

    public double openPositionId;
    public double openPositionTimestamp;
    public double openPositionState;
    public double openPositionAmt;
    public double openPositionPrice;
    public double openPositionExpiry;

    public double closePositionId;
    public double closePositionTimestamp;
    public double closePositionState;
    public double closePositionAmt;
    public double closePositionPrice;
    public double closePositionExpiry;
}
