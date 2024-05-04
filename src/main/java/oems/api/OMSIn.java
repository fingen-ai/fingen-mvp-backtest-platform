/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems.api;

import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

public interface OMSIn {

    void newOrderSingle(NewOrderSingle nos);

    void cancelOrderRequest(CancelOrderRequest cor);

    void cancelAll(CancelAll cancelAll);
}
