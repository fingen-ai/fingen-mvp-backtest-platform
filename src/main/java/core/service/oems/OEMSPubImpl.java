package core.service.oems;

import oems.BRM;
import oems.api.OMSIn;
import oems.api.OMSOut;
import oems.dto.*;

import java.io.IOException;
import java.util.List;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private NewOrderSingle nos = new NewOrderSingle();
    OMSIn omsIn;
    OMSOut omsOut;
    BRM brm = new BRM();

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }
    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) {
        oemsData.svcStartTs = System.nanoTime();

        // New signal response
        if(oemsData.bassoOrderIdea != null) {

            // Curr pos in the symbol
                // Get orders map. Keyed by openOrderId. Returns OrdersMap
                // Risk % check
                // Vol % check
                // Add to  position or not

            // No curr pos in the symbol
            if(oemsData.bassoOrderIdea.equals("Bullish")) {
                nos.symbol(1); // just have a long to symbol identification map
                nos.ordType(OrderType.limit);
                nos.price(oemsData.bid);

            } else if(oemsData.bassoOrderIdea.equals("Bearish")) {
                nos.symbol(1); // just have a long to symbol identification map
                nos.ordType(OrderType.limit);
                nos.price(oemsData.ask);
                omsIn.newOrderSingle(nos);
            }
        }

        // BRM methods
        //List<Order> openOrders = brm.getOpenOrders();
        //openOrders.forEach(order -> System.out.println("Open Order: " + order));

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}