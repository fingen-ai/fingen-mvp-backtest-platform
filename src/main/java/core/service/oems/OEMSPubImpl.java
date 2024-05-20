package core.service.oems;

import account.AccountData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    int recCount = 0;

    OEMSData mapOpenOrderOEMS = new OEMSData();
    AccountData accountData = new AccountData();

    Risk risk = new RiskImpl();
    OrderMappingService orderMS = new OrderMappingService();

    String prevBassoOrderIdea = "";
    long[] openOrdersIDArray =  new long[0];
    double totalCurrOpenPositionQty = 0;

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        accountData.nav = 25000;
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        oemsData.prevBassoOrderIdea = prevBassoOrderIdea;
        getStopLoss(oemsData); // for any curr "running" positions during all signal moments - incl "neutral"

        if (!oemsData.bassoOrderIdea.equals("Neutral")) {

            openOrdersIDArray = orderMS.getFromNOSIDArray(oemsData.symbol);
            if (openOrdersIDArray != null) {

                // coa upon trend reversal
                if(!oemsData.bassoOrderIdea.equals(prevBassoOrderIdea)) {

                    placeCOAOrder(oemsData, openOrdersIDArray);
                    System.out.println("COA");

                // cos upon sl exceeded long
                } else if (oemsData.openOrderSide.equals("Buy")) {

                    if (oemsData.close < oemsData.openOrderSLPrice) {
                        placeCOSOrder(oemsData);
                        System.out.println("COS: SL 1");
                    } else {
                        // nos ongoing
                        System.out.println("NOS ONGOING");
                        placeNOSOngoingOrder(oemsData);
                    }

                // cos upon sl exceeded short
                } else if (oemsData.openOrderSide.equals("Sell")) {
                    if (oemsData.close > oemsData.openOrderSLPrice) {
                        placeCOSOrder(oemsData);
                        System.out.println("COS: SL 2");
                    } else {
                        // nos ongoing
                        System.out.println("NOS ONGOING");
                        placeNOSOngoingOrder(oemsData);
                    }
                }

            } else {

                System.out.println("NOS INIT");
                // nos init for new trend
                placeNOSInitOrder(oemsData);
            }

        } else {

            //  neutral
            oemsData.openOrderSide = "Hold";
        }

        prevBassoOrderIdea = oemsData.bassoOrderIdea;

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;


        if((recCount >= 49) && (recCount < 404)) {
            System.out.println("OEMS:" + recCount);
            System.out.println("OEMS: " + oemsData.openOrderId);
            System.out.println("OEMS: " + oemsData.prevBassoOrderIdea);
            System.out.println("OEMS: " + oemsData.bassoOrderIdea);
            System.out.println("OEMS: " + oemsData.openOrderSide);
            System.out.println("OEMS: " + oemsData.openOrderQty);
            System.out.println("OEMS: " + oemsData.currCarryQty);
            System.out.println("OEMS: " + oemsData.openOrderExpiry);
            System.out.println("OEMS: " + oemsData.openOrderState);
            System.out.println("OEMS: " + oemsData.currRiskPercent);
            System.out.println("OEMS: " + oemsData.currVolRiskPercent);
            System.out.println("OEMS: " + oemsData.close);
            System.out.println("OEMS: " + oemsData.openOrderSLPrice);
            System.out.println("\n");
        }
        recCount++;

        output.simpleCall(oemsData);
    }

    private void placeNOSInitOrder(OEMSData oemsData) {

        getInitCurrRiskVolOrderQty(oemsData);

        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderExpiry = "GTC";
        oemsData.openOrderState = "Init New Order Single";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
        openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);
    }

    private void placeNOSOngoingOrder(OEMSData oemsData) {

        getOngoingCurrRiskVolOrderQty(oemsData);

        OEMSData currOEMSData = new OEMSData();

        for(int i=0; i<openOrdersIDArray.length; i++) {
            currOEMSData = orderMS.getNOS(openOrdersIDArray[i]);
            oemsData.currCarryQty += currOEMSData.openOrderQty;
        }

        // no ongoing order
        if(oemsData.currRiskPercent >= risk.getOngoingRiskPercentThreshold()) {

            oemsData.openOrderId = 0;
            oemsData.openOrderTimestamp = System.nanoTime();
            oemsData.openOrderExpiry = "NA";
            oemsData.openOrderState = "Hold: Ongoing New Order Single > Ongoing Risk %";
            oemsData.openOrderQty = 0;
        }

        // no ongoing order
        if(oemsData.currRiskPercent >= risk.getOngoingRiskPercentThreshold()) {

            oemsData.openOrderId = 0;
            oemsData.openOrderTimestamp = System.nanoTime();
            oemsData.openOrderExpiry = "NA";
            oemsData.openOrderState = "Hold: Ongoing New Order Single > Ongoing Vol %";
            oemsData.openOrderQty = 0;
        }

        // ongoing order
        if(oemsData.currRiskPercent < risk.getOngoingRiskPercentThreshold() ||
                oemsData.currVolRiskPercent < risk.getOngoingVolPercentThreshold() ) {

            oemsData.openOrderId = System.nanoTime();
            oemsData.openOrderTimestamp = System.nanoTime();
            oemsData.openOrderExpiry = "GTC";
            oemsData.openOrderState = "Ongoing New Order Single";
            oemsData.currCarryQty += oemsData.openOrderQty;

            orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);
            openOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
            orderMS.addToNOSIDArray(oemsData.symbol, openOrdersIDArray);
        }
    }

    /**
     * Exit is 3X Average True Range (10 day period) subtracted from the close.
     * The trailing stop can only get closer to the current market price, not further away.
     * @param oemsData
     */
    private void getStopLoss(OEMSData oemsData) {
        oemsData.openOrderSLPrice = oemsData.close - (oemsData.atr * 3);
        oemsData.openOrderSLPrice = roundingWithPrecision(oemsData.openOrderSLPrice, 5);
    }

    private void placeCOSOrder(OEMSData oemsData) {
        // delete the ID from the ID array
        for(int i=0; i < openOrdersIDArray.length; i++) {
            if(oemsData.openOrderId == openOrdersIDArray[i]) {
                ArrayUtils.remove(openOrdersIDArray, i);
            }
        }

        oemsData.closeOrderId = System.nanoTime();
        oemsData.closeOrderTimestamp = System.nanoTime();
        oemsData.closeOrderExpiry = "GTC";
        oemsData.closeOrderState = "Close Order Single";
        orderMS.addUpdateCOS(oemsData.openOrderId, oemsData);
    }

    private void placeCOAOrder(OEMSData oemsData, long[] openOrdersIDArray) {

        for(int i=0; i < openOrdersIDArray.length; i++) {
            oemsData.closeOrderId = System.nanoTime();
            oemsData.closeOrderTimestamp = System.nanoTime();
            oemsData.closeOrderExpiry = "GTC";
            oemsData.closeOrderState = "Close Orders All";
            orderMS.addUpdateCOS(oemsData.openOrderId, oemsData);
        }

        orderMS.deleteFromNOSIDArray(oemsData.symbol);
    }

    private void getOngoingCurrRiskVolOrderQty(OEMSData oemsData) {

        totalCurrOpenPositionQty = 0;

        for(int i=0; i < openOrdersIDArray.length; i++) {
            mapOpenOrderOEMS = orderMS.getNOS(openOrdersIDArray[i]);
            totalCurrOpenPositionQty = mapOpenOrderOEMS.openOrderQty;
        }

        oemsData.currRiskPercent = (totalCurrOpenPositionQty * oemsData.close) / accountData.nav;
        double riskPercentAvail = risk.getOngoingRiskPercentThreshold() - oemsData.currRiskPercent;
        if(riskPercentAvail > 0) {
            oemsData.orderQtyPerRisk = (riskPercentAvail * accountData.nav) / oemsData.close;
            oemsData.orderQtyPerRisk = roundingWithPrecision(oemsData.orderQtyPerRisk, 5);
        }

        oemsData.currVolRiskPercent = (oemsData.atr * oemsData.close) / accountData.nav;
        double volRiskPercentAvail =  risk.getOngoingVolPercentThreshold() - oemsData.currVolRiskPercent;
        if(volRiskPercentAvail > 0) {
            oemsData.orderQtyPerVol = (volRiskPercentAvail * accountData.nav) / oemsData.close;
            oemsData.orderQtyPerVol = roundingWithPrecision(oemsData.orderQtyPerVol, 5);
        }

        if(Math.min(oemsData.orderQtyPerRisk, oemsData.orderQtyPerVol) > 0) {
            oemsData.openOrderQty = Math.min(oemsData.orderQtyPerRisk, oemsData.orderQtyPerVol);
            oemsData.openOrderQty = roundingWithPrecision(oemsData.openOrderQty, 5);

        } else {
            oemsData.openOrderQty = 0;
        }
    }

    private void getInitCurrRiskVolOrderQty(OEMSData oemsData) {

        oemsData.orderQtyPerRisk = (risk.getInitRiskPercentThreshold() * accountData.nav) / oemsData.close;
        oemsData.orderQtyPerRisk = roundingWithPrecision(oemsData.orderQtyPerRisk, 5);

        oemsData.orderQtyPerVol = (risk.getInitVolPercentThreshold() * accountData.nav) / oemsData.close;
        oemsData.orderQtyPerVol = roundingWithPrecision(oemsData.orderQtyPerVol, 5);

        oemsData.currRiskPercent = risk.getInitRiskPercentThreshold();
        oemsData.currVolRiskPercent = 0;

        oemsData.openOrderQty = oemsData.orderQtyPerRisk;
        oemsData.openOrderQty = roundingWithPrecision(oemsData.openOrderQty, 5);
    }

    public static double roundingWithPrecision(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}