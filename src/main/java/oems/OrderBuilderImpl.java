/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems;

import core.service.oems.OEMSData;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.wire.converter.Base85;
import oems.api.OrderBuilder;
import oems.dto.BuySell;
import oems.dto.NewOrderSingle;
import oems.dto.OrderType;

import java.io.IOException;

public class OrderBuilderImpl implements OrderBuilder {

    public NewOrderSingle buildNOS(OEMSData oems) {

        // Add NewOrderSingle class to the alias pool
        ClassAliasPool.CLASS_ALIASES.addAlias(NewOrderSingle.class);

        // Create a new order single
        NewOrderSingle nos = new NewOrderSingle()
                .sender(toLong("sender"))
                .target(toLong("target"))
                .transactTime(now())
                .sendingTime(now())
                .clOrdID(String.valueOf(now()))
                .orderQty(1)
                .ordType(OrderType.limit)
                .price(oems.close)
                .side(BuySell.buy)
                .symbol(toLong("BTC_USD"));


        return nos;
    }

    static long now() {
        return SystemTimeProvider.INSTANCE.currentTimeMicros();
    }

    static long toLong(String s) {
        return Base85.INSTANCE.parse(s);
    }
}
