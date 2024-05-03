package core.service.insight;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

import java.math.BigDecimal;
import java.util.Date;

public class InsightData extends SelfDescribingMarshallable {
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
    public BigDecimal bid;
    public BigDecimal ask;
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
    public double equity;
    public double positionRisk;
    double atr;
    double priorClose;
    public double riskInitPercentThreshold;
    public double volInitPercentThreshold;
    public double riskOngoingPercentThreshold;
    public double volOngoingPercentThreshold;
    public double currentTotalPercentRiskPercent;
    public double currentTotalPercentVolRiskPercent;
    public String tradeInstructionStatus;
    public int tradeCount;
}