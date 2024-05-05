package oems.api;

import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

import java.io.IOException;

public interface OMS {

    void newOrderSingle(NewOrderSingle nos) throws IOException;

    void closeOrderSingle(CancelOrderRequest cor);

    void closeOrderAll(CancelAll cancelAll);
}
