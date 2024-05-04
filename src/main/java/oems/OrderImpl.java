/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems;

import core.service.oems.OEMSData;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.core.time.SystemTimeProvider;
import net.openhft.chronicle.core.util.Mocker;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.ExcerptAppender;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.queue.rollcycles.TestRollCycles;
import net.openhft.chronicle.wire.converter.Base85;
import oems.api.OMSIn;
import oems.dto.BuySell;
import oems.dto.NewOrderSingle;
import oems.dto.OrderType;

import java.io.IOException;

public class OrderImpl {

    public void openOrder(OEMSData oemsData) throws IOException {
        // Add NewOrderSingle class to the alias pool
        ClassAliasPool.CLASS_ALIASES.addAlias(NewOrderSingle.class);

        // Establish connection with the queue - Do we need this Try block?
        ChronicleQueue q = SingleChronicleQueueBuilder.binary("in")
                .rollCycle(TestRollCycles.TEST8_DAILY)
                .build();
        ExcerptAppender appender = q.createAppender();

        // Acquire the appender and write methods for OMSIn - Order History Reporting Source?
        OMSIn in = appender.methodWriter(OMSIn.class);

        // Create a logging mock for OMSIn - Order History Reporting Source?
        OMSIn in2 = Mocker.logging(OMSIn.class, "in - ", System.out);

        // Create a new order single
        NewOrderSingle nos = new NewOrderSingle()
                .sender(toLong("sender"))
                .target(toLong("target"))
                .transactTime(now())
                .sendingTime(now())
                .orderQty(1)
                .ordType(OrderType.limit)
                .price(oemsData.bid)
                .side(BuySell.buy)
                .symbol(toLong("BTC_USD"));

        System.out.println("\nNOS: " + nos);
    }

    private void openBuyLimitOrder() {

    }

    private void openSellLimitOrder() {

    }

    private void cancelOrder() {

    }

    private void cancelAllOrders() {

    }

    private static long now() {
        return SystemTimeProvider.INSTANCE.currentTimeMicros();
    }

    private static long toLong(String s) {
        return Base85.INSTANCE.parse(s);
    }
}
