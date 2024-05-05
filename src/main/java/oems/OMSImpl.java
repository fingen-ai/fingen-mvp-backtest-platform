package oems;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.values.LongValue;
import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.values.Values;
import oems.api.OMS;
import oems.dto.CancelAll;
import oems.dto.CancelOrderRequest;
import oems.dto.NewOrderSingle;

import java.io.File;
import java.io.IOException;

public class OMSImpl implements OMS {

    File openOrderMap = new File(OS.USER_HOME + "/FinGen/Maps/OMS/openOrderMap");
    static ChronicleMap<CharSequence, NewOrderSingle> NOS;
    static NewOrderSingle nos = new NewOrderSingle();

    public OMSImpl() throws IOException {
        if (!openOrderMap.exists()) {
            NOS = ChronicleMap
                    .of(CharSequence.class, NewOrderSingle.class)
                    .name("openOrder.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(nos)
                    .createPersistedTo(openOrderMap);
        } else {
            NOS = ChronicleMap
                    .of(CharSequence.class, NewOrderSingle.class)
                    .name("openOrder.map")
                    .entries(1_000)
                    .averageKey("mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm-mmm")
                    .averageValue(nos)
                    .recoverPersistedTo(openOrderMap, false);
        }
    }

    public void addOrderMap(CharSequence setKey, NewOrderSingle nos) {

        NOS.put(setKey, nos);
    }
    public NewOrderSingle getOrderMap(CharSequence getKey, NewOrderSingle nos) {

        return NOS.getUsing(getKey, nos);
    }

    @Override
    public void newOrderSingle(NewOrderSingle nos) throws IOException {
        addOrderMap(nos.clOrdID(), nos);
        System.out.println("New order placed: " + nos);
    }

    @Override
    public void closeOrderSingle(CancelOrderRequest cor) {
        System.out.println("\nCAN: " + cor);
        getOrderMap(nos.clOrdID(), nos);
    }

    @Override
    public void closeOrderAll(CancelAll cancelAll) {
        System.out.println("\nCANALL: " + cancelAll);
        // Add logic to close all orders
    }
}
