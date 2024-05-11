package core.service.oems;

import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

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

                // before we look at new insights, and placing NOS
                // we need to update SL and exit if called to do so
                getStopLoss(oemsData);
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

        System.out.println("OEMS INIT: " + oemsData);
    }

    private void placeNOSOngoingOrder(OEMSData oemsData) {
        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderState = "Ongoing Order";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
        openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);

        System.out.println("OEMS ONGOING: " + oemsData);
    }

    /**
     * Exit is 3X Average True Range (10 day period) subtracted from the close.
     * The trailing stop can only get closer to the current market price, not further away.
     * @param oemsData
     */
    private void getStopLoss(OEMSData oemsData) {
        double stopPrice = 0;
        if(oemsData.openOrderSide.equals("Buy")) {
            stopPrice = oemsData.close - (oemsData.atr * 3);
            if (oemsData.close < stopPrice) {
                placeCOSOrder();
            } else {
                System.out.println("UPDT BUY SL");
                oemsData.openOrderSLPrice = stopPrice;
            }
        }

        if (oemsData.openOrderSide.equals("Sell")) {
            stopPrice = oemsData.close + (oemsData.atr * 3);
            if (oemsData.close > stopPrice) {
                placeCOSOrder();
            } else {
                System.out.println("UPDT SELL SL");
                oemsData.openOrderSLPrice = stopPrice;
            }
        }
    }

    private void placeCOSOrder() {
        System.out.println("COS");
        // build close order
        // close one position per SL or TP
    }

    private void placeCOAOrder(OEMSData oemsData) {
        // build close order
        // close all positions per symbol and insight re: trend reversal
    }
}