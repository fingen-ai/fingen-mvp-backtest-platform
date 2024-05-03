package account;

import net.openhft.chronicle.wire.SelfDescribingMarshallable;

public class AccountData extends SelfDescribingMarshallable {

    public double nav;
    public double positionAmt;
}
