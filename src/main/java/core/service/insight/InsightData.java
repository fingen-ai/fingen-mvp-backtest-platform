package core.service.insight;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class InsightData extends SelfDescribingMarshallable {
    public long recId;
    public long svcStartTs;
    public long svcStopTs;
    public long svcLatency;
    public double open;
    public double high;
    public double low;
    public double close;
    public double volume;
    public double marketCap;
    public CharSequence start;
    public CharSequence end;
    public double lhcAvgPrice;
    public String bassoOrderIdea;
}