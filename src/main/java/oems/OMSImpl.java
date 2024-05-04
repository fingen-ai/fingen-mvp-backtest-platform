/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems;

import oems.api.OMSIn;
import oems.dto.*;

public class OMSImpl {
    // The outbound interface for sending execution reports and order cancel rejections
    //private final OMSOut out;

    // Reusable instance of ExecutionReport for creating new orders
    //private final ExecutionReport er = new ExecutionReport();

    // Reusable instance of OrderCancelReject for cancelling orders
    //private final OrderCancelReject ocr = new OrderCancelReject();

    public void newOrderSingle(NewOrderSingle nos) {
        //er.reset();
        //final long orderID = SystemTimeProvider.CLOCK.currentTimeNanos(); // Generate unique order ID

        /*
        // Populate the ExecutionReport with request details
        er.sender(nos.target())
                .target(nos.sender())
                .symbol(nos.symbol())
                .clOrdID(nos.clOrdID())
                .ordType(nos.ordType())
                .orderQty(nos.orderQty())
                .price(nos.price())
                .side(nos.side())
                .sendingTime(nos.sendingTime())
                .transactTime(nos.transactTime())
                .leavesQty(0)
                .orderID(orderID)
                .text("Not ready");
        */

        // Send execution report
        //out.executionReport(er);
    }

    public void cancelOrderRequest(CancelOrderRequest cor) {
        /*
        // Populate OrderCancelReject with request details
        ocr.sender(cor.target())
                .target(cor.sender())
                .symbol(cor.symbol())
                .clOrdID(cor.clOrdID())
                .sendingTime(cor.sendingTime())
                .reason("No such order");
         */

        // Send order cancellation rejection
        //out.orderCancelReject(ocr);
    }

    public void cancelAll(CancelAll cancelAll) {
        /*
        // Populate OrderCancelReject with request details
        ocr.sender(cancelAll.target())
                .target(cancelAll.sender())
                .symbol(cancelAll.symbol())
                .clOrdID("")
                .sendingTime(cancelAll.sendingTime())
                .reason("No such orders");
         */

        // Send order cancellation rejection
        //out.orderCancelReject(ocr);
    }
}
