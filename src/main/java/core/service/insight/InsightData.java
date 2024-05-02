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

    // insights q real-time data
    public String outlook;
    public long outlookDurationInDays;
    public String outlookDirection;
    public String outlookMomentum;  // MACD, RSI
    public String outlookHealth;  // Glicko2
    public double outlookGainPosterior;
    public double outlookLossPosterior;
    public double outlookAvgExpectedGain;
    public double outlookAvgExpectedLoss;
    public double outlookEdge;
    public double trendHighPrice;
    public double trendLowPrice;
    public long trendNewHighCount;
    public long trendNewLowCount;
    public long trendUpDaysCount;
    public long trendDownDaysCount;
    public long trendInsideDaysCount;
    public long trendOutsideDaysCount;
    public long trendTotalDaysCount;
    public double trapezoidRule;

    // insights q iqr data
    // build arrays of each q data above
    // calc stats
    // calc physics
    // calc deltas
    // calc delta stats
    // calc delta physics
    // calc edge
    // calc posterior - active inference
}