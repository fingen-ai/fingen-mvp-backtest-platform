package core.service.oems;

import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    OrderMappingService orderMS = new OrderMappingService();
    String prevBassoOrderIdea = "";
    int j = 0;
    long[] openOrdersIDArray =  new long[0];

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        oemsData.prevBassoOrderIdea = prevBassoOrderIdea;

        if (!oemsData.bassoOrderIdea.equals("Neutral")) {

            openOrdersIDArray = orderMS.getFromNOSIDArray(oemsData.symbol);

            if (openOrdersIDArray != null) {
                placeCOAOrder(oemsData, openOrdersIDArray);
                getStopLoss(oemsData);
                placeNOSOngoingOrder(oemsData);
            } else {
                getStopLoss(oemsData);
                placeNOSInitOrder(oemsData);
            }
        } else {
            oemsData.openOrderSide = "Hold";
        }

        prevBassoOrderIdea = oemsData.bassoOrderIdea;
        j++;

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;

        //System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }

    private void placeNOSInitOrder(OEMSData oemsData) {
        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderExpiry = "GTC";
        oemsData.openOrderState = "Init New Order Single";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
        openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);

        if(j <= 50) {
            //System.out.println("REC: " + j);
            //System.out.println("OEMS NOS INIT: " + oemsData);
        }
    }

    private void placeNOSOngoingOrder(OEMSData oemsData) {
        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderExpiry = "GTC";
        oemsData.openOrderState = "Ongoing New Order Single";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
        openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);

        if(j <= 50) {
            //System.out.println("REC: " + j);
            //System.out.println("OEMS NOS ONGOING: " + oemsData);
        }
    }

    /**
     * Exit is 3X Average True Range (10 day period) subtracted from the close.
     * The trailing stop can only get closer to the current market price, not further away.
     * @param oemsData
     */
    private void getStopLoss(OEMSData oemsData) {
        oemsData.openOrderSLPrice = oemsData.close - (oemsData.atr * 3);
        if (oemsData.openOrderSide.equals("Buy")) {
            if (oemsData.close < oemsData.openOrderSLPrice) {
                placeCOSOrder(oemsData);
            }
        }
        if (oemsData.openOrderSide.equals("Sell")) {
            if (oemsData.close > oemsData.openOrderSLPrice) {
                placeCOSOrder(oemsData);
            }
        }
    }

    private void placeCOSOrder(OEMSData oemsData) {
        // delete the ID from the ID array
        for(int i=0; i < openOrdersIDArray.length; i++) {
            if(oemsData.openOrderId == openOrdersIDArray[i]) {
                long[] updateOpenOrderIDArray = ArrayUtils.remove(openOrdersIDArray, i);
            }
        }

        oemsData.closeOrderId = System.nanoTime();
        oemsData.closeOrderTimestamp = System.nanoTime();
        oemsData.closeOrderExpiry = "GTC";
        oemsData.closeOrderState = "Close Order Single";
        orderMS.addUpdateCOS(oemsData.openOrderId, oemsData);

        if(j <= 50) {
            //System.out.println("REC: " + j);
            //System.out.println("OEMS COS: " + oemsData);
        }
    }

    private void placeCOAOrder(OEMSData oemsData, long[] openOrdersIDArray) {

        if(prevBassoOrderIdea != null) {
            oemsData.prevBassoOrderIdea = prevBassoOrderIdea;
            if (!oemsData.bassoOrderIdea.equals(oemsData.prevBassoOrderIdea)) {

                orderMS.deleteFromNOSIDArray(oemsData.symbol);

                for(int i=0; i < openOrdersIDArray.length; i++) {
                    oemsData.closeOrderId = System.nanoTime();
                    oemsData.closeOrderTimestamp = System.nanoTime();
                    oemsData.closeOrderExpiry = "GTC";
                    oemsData.closeOrderState = "Close Orders All";
                    orderMS.addUpdateCOS(oemsData.openOrderId, oemsData);

                    if(j <= 50) {
                        //System.out.println("REC: " + j);
                        //System.out.println("OEMS COA: " + oemsData);
                    }
                }
            }
        }
    }
}