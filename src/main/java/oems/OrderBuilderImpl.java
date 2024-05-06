/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems;

import core.service.oems.OEMSData;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.wire.converter.Base85;
import oems.api.OrderBuilder;
import oems.dto.CloseOrderAll;
import oems.dto.NewOrderSingle;
import oems.dto.OrderType;

public class OrderBuilderImpl implements OrderBuilder {

    public NewOrderSingle buildNOS(OEMSData oems) {

        // Create a new order single
        NewOrderSingle nos = new NewOrderSingle();
        nos.sender = "sender";
        nos.target = "target";
        nos.transactTime = String.valueOf(System.nanoTime());
        nos.sendingTime = String.valueOf(System.nanoTime());
        nos.clOrdID = String.valueOf(System.nanoTime());
        nos.orderQty = oems.openOrderQty;
        nos.ordType = String.valueOf(OrderType.limit);
        nos.price = oems.close;
        nos.side = oems.openOrderSide;;
        nos.symbol = oems.symbol;

        return nos;
    }

    public CloseOrderAll buildCOA(OEMSData oems) {

        // Create a new order single
        CloseOrderAll coa = new CloseOrderAll();
        coa.sender = "sender";
        coa.target = "target";
        coa.transactTime = String.valueOf(System.nanoTime());
        coa.sendingTime = String.valueOf(System.nanoTime());
        coa.clOrdID = String.valueOf(System.nanoTime());
        coa.orderQty = oems.openOrderQty;
        coa.ordType = String.valueOf(OrderType.limit);
        coa.price = oems.close;
        coa.side = oems.closeOrderSide;;
        coa.symbol = oems.symbol;

        return coa;
    }

    static long now() {
        return SystemTimeProvider.INSTANCE.currentTimeMicros();
    }

    static long toLong(String s) {
        return Base85.INSTANCE.parse(s);
    }
}
