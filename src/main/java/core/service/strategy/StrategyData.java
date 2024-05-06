package core.service.strategy;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

import java.math.BigDecimal;
import java.util.Date;

public class StrategyData extends SelfDescribingMarshallable {
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
}
