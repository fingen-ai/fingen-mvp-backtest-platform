package service.base;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class SimpleData extends SelfDescribingMarshallable {
    public String text;
    public long number;
    public long ts0, ts;
}
