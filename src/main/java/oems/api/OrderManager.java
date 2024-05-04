package oems.api;

import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

public interface OrderManager {

    void newOrderSingle(NewOrderSingle nos);

    void cancelOrderRequest(CancelOrderRequest cor);

    void cancelAll(CancelAll cancelAll);
}
