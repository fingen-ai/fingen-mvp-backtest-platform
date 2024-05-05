package oems;

import oems.api.OrderManager;
import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

public class OrderManagerImpl implements OrderManager {

    @Override
    public void newOrderSingle(NewOrderSingle nos) {
        System.out.println("\nNOS: " + nos);
    }

    @Override
    public void cancelOrderRequest(CancelOrderRequest cor) {
        System.out.println("\nCAN: " + cor);
    }

    @Override
    public void cancelAll(CancelAll cancelAll) {
        System.out.println("\nCANALL: " + cancelAll);
    }
}
