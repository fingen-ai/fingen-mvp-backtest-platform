/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems.api;

import core.service.oems.OEMSData;
import net.openhft.chronicle.bytes.MethodId;
import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

/**
 * The {@code OMSIn} interface defines the operations that can be performed by an Order Management System (OMS).
 * <p>
 * It includes methods to handle new single orders, cancel order requests, and cancel all orders.
 * Each method receives an instance of a data transfer object that represents the details of the operation.
 */
public interface OMSIn {

    @MethodId(1)
    void newOrderSingle(NewOrderSingle nos);

    /**
     * Handles a cancel order request.
     *
     * @param cor The {@link CancelOrderRequest} object representing the details of the cancel order request.
     */
    @MethodId(2)
    void cancelOrderRequest(CancelOrderRequest cor);

    /**
     * Handles a cancel all orders request.
     *
     * @param cancelAll The {@link CancelAll} object representing the details of the cancel all orders request.
     */
    void cancelAll(CancelAll cancelAll);
}
