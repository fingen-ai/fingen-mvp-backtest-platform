package oems.api;

import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

public interface OMS {

    void newOrderSingle(NewOrderSingle nos);

    void closeOrderSingle(CancelOrderRequest cor);

    void closeOrderAll(CancelAll cancelAll);
}
