package oems;

import oems.api.OMS;
import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

public class OMSImpl implements OMS {

    BRM brm = new BRM();

    @Override
    public void newOrderSingle(NewOrderSingle nos) {
        System.out.println("\nNOS: " + nos);
        // add to open orders map
    }

    @Override
    public void closeOrderSingle(CancelOrderRequest cor) {
        System.out.println("\nCAN: " + cor);
        // add to  orders map
    }

    @Override
    public void closeOrderAll(CancelAll cancelAll) {
        System.out.println("\nCANALL: " + cancelAll);
    }
}
