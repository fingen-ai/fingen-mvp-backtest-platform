package publish;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class LandingPageData extends SelfDescribingMarshallable {
    public Object avgWinAmt;
    public Object avgLossAmt;
    public double avgWinPercent;
    public double avgLossPercent;
    public double edge;
}
