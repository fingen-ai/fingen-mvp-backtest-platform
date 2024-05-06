/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems.dto;

import net.openhft.chronicle.wire.*;

public class NewOrderSingle extends SelfDescribingMarshallable {
    public String sender;
    public String target;
    public String transactTime;
    public String sendingTime;
    public String clOrdID;
    public int orderQty;
    public String ordType;
    public double price;
    public String side;
    public String symbol;
}
