/*
 * Copyright (c) 2016-2019 Chronicle Software Ltd
 */

package oems.api;

import core.service.oems.OEMSData;
import net.openhft.chronicle.bytes.MethodId;
import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

public interface OMSIn {

    //@MethodId(1)
    void newOrderSingle(NewOrderSingle nos);

    //@MethodId(2)
    void cancelOrderRequest(CancelOrderRequest cor);

    void cancelAll(CancelAll cancelAll);
}
