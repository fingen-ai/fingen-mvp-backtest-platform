package core.service.oems;

import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    OrderMappingService orderMS = new OrderMappingService();

    long[] openOrdersIDArray =  new long[0];

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        if(!oemsData.bassoOrderIdea.equals("Neutral")) {

            openOrdersIDArray = orderMS.getFromNOSIDArray(oemsData.symbol);
            if(openOrdersIDArray != null) {
                placeNOSOngoingOrder(oemsData);
            } else {
                placeNOSInitOrder(oemsData);
            }
        }

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        //System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }

    private void placeNOSInitOrder(OEMSData oemsData) {
        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderState = "Init Order";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
        openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);

        System.out.println("OEMS: " + oemsData);
    }

    private void placeNOSOngoingOrder(OEMSData oemsData) {
        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderState = "Ongoing Order";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
        openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);

        System.out.println("ONGOING ORDER: " + openOrdersIDArray.length);
    }

    private void getStopLoss(OEMSData oemsData) {
        // if(oemsData.close) exceeds SL price, close this order
        // else update SL price
    }

    private void getTakeProfit(OEMSData oemsData) {
        // if(oemsData.close) exceeds TP price, close this order
        // else update TP price
    }
}