package core.service.oems;

import account.AccountData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    int recCount = 0;

    OEMSData nosOEMSData = new OEMSData();
    OEMSData coaOEMSData = new OEMSData();
    AccountData accountData = new AccountData();

    Risk risk = new RiskImpl();
    OrderMappingService orderMS = new OrderMappingService();

    String prevBassoOrderIdea = "";
    long[] openOrdersIDArray =  new long[0];
    long[] updateOpenOrdersIDArray =  new long[0];
    long[] closeOrdersIDArray =  new long[0];
    double volRiskPercentAvail = 0.0;
    double riskPercentAvail = 0.0;

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

        getStopLoss(oemsData);

        if (!oemsData.bassoOrderIdea.equals("Neutral")) {

            openOrdersIDArray = orderMS.getFromNOSIDArray(oemsData.symbol);
            if (openOrdersIDArray != null) {

                // coa, trend reversal
                if(!oemsData.bassoOrderIdea.equals(prevBassoOrderIdea)) {
                    placeCOAOrder(oemsData);

                    if(coaOEMSData.closeOrderState.equals("Close Orders All")) {
                        placeNOSInitOrder(oemsData);
                    }

                // coa on bullish sl - tho no reversal, yet
                } else if (oemsData.openOrderSide.equals("Buy")) {
                    if (oemsData.close < oemsData.openOrderSLPrice) {
                        placeCOAOrder(oemsData);
                    } else {
                        placeNOSOngoingOrder(oemsData);
                    }

                // coa on bearish sl - tho no reversal, yet
                } else if (oemsData.openOrderSide.equals("Sell")) {
                    if (oemsData.close > oemsData.openOrderSLPrice) {
                        placeCOAOrder(oemsData);
                    }else {
                        placeNOSOngoingOrder(oemsData);
                    }
                }

            } else {

                placeNOSInitOrder(oemsData);
            }
        }

        prevBassoOrderIdea = oemsData.bassoOrderIdea;

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;

        /*
        System.out.println("rec count:" + recCount);
        System.out.println("open order ID: " + oemsData.openOrderId);
        System.out.println("prev Basso Idea: " + oemsData.prevBassoOrderIdea);
        System.out.println("basso Idea: " + oemsData.bassoOrderIdea);

        System.out.println("open order side: " + oemsData.openOrderSide);
        System.out.println("open order qty: " + oemsData.openOrderQty);
        System.out.println("curr carry qty: " + oemsData.currCarryQty);
        System.out.println("open order expiry: " + oemsData.openOrderExpiry);
        System.out.println("open order state: " + oemsData.openOrderState);

        System.out.println("curr risk %: " + oemsData.currRiskPercent);
        System.out.println("curr vol %: " + oemsData.currVolRiskPercent);
        System.out.println("close: " + oemsData.close);
        System.out.println("open order SL price: " + oemsData.openOrderSLPrice);

        System.out.println("close order side: " + oemsData.closeOrderSide);
        System.out.println("close order state: " + oemsData.closeOrderState);
        System.out.println("order confirm state: " + oemsData.orderConfirmationState);
        System.out.println("\n");
         */

        recCount++;

        openOrdersIDArray = null;
        updateOpenOrdersIDArray = null;
        nosOEMSData = null;

        output.simpleCall(oemsData);
    }

    private void placeNOSInitOrder(OEMSData oemsData) {

        getInitCurrRiskVolOrderQty(oemsData);

        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderExpiry = "GTC";
        oemsData.openOrderState = "Init New Order Single";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);

        updateOpenOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, updateOpenOrdersIDArray);

        getInitNOSCompleteConfirmation(oemsData);
    }

    private void placeNOSOngoingOrder(OEMSData oemsData) {

        getOngoingCurrRiskVolOrderQty(oemsData);

        // no ongoing order
        if(oemsData.currRiskPercent > risk.getOngoingRiskPercentThreshold()) {
            oemsData.openOrderId = 0;
            oemsData.openOrderTimestamp = System.nanoTime();
            oemsData.openOrderExpiry = "NA";
            oemsData.openOrderState = "Hold: Ongoing New Order Single > Ongoing Risk %";
        }

        // no ongoing order
        if(oemsData.currVolRiskPercent > risk.getOngoingVolPercentThreshold() ) {
            oemsData.openOrderId = 0;
            oemsData.openOrderTimestamp = System.nanoTime();
            oemsData.openOrderExpiry = "NA";
            oemsData.openOrderState = "Hold: Ongoing New Order Single > Ongoing Vol %";
        }

        if(oemsData.currRiskPercent <= risk.getOngoingRiskPercentThreshold() ) {

            oemsData.openOrderId = System.nanoTime();
            oemsData.openOrderTimestamp = System.nanoTime();

            if(oemsData.openOrderQty > 0) {
                oemsData.openOrderExpiry = "GTC";
                oemsData.openOrderState = "Ongoing New Order Single";

                orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);

                updateOpenOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
                orderMS.addToNOSIDArray(oemsData.symbol, updateOpenOrdersIDArray);

                getOngoingNOSCompleteConfirmation(oemsData);

            } else {
                oemsData.openOrderExpiry = "NA";
                oemsData.openOrderState = "Hold: Ongoing New Order Single >= Ongoing Risk %";
            }
        }
    }

    /**
     * Exit is 3X Average True Range (10 day period) subtracted from the close.
     * The trailing stop can only get closer to the current market price, not further away.
     * @param oemsData
     */
    private void getStopLoss(OEMSData oemsData) {
        if(oemsData.openOrderSide.equals("Buy")) {
            oemsData.openOrderSLPrice = oemsData.close - (oemsData.atr * 3);
            oemsData.openOrderSLPrice = roundingWithPrecision(oemsData.openOrderSLPrice, 5);
        } else {
            oemsData.openOrderSLPrice = oemsData.close + (oemsData.atr * 3);
            oemsData.openOrderSLPrice = roundingWithPrecision(oemsData.openOrderSLPrice, 5);
        }
    }

    private void placeCOAOrder(OEMSData oemsData) {

        for(int i=0; i < openOrdersIDArray.length; i++) {

            coaOEMSData = orderMS.getNOS(openOrdersIDArray[i]);

            coaOEMSData.openOrderId = openOrdersIDArray[i];

            coaOEMSData.closeOrderId = coaOEMSData.openOrderId;
            coaOEMSData.closeOrderTimestamp = System.nanoTime();
            coaOEMSData.closeOrderPrice = oemsData.close;
            coaOEMSData.closeOrderExpiry = "GTC";
            coaOEMSData.closedOrderType ="LMT";
            coaOEMSData.closeOrderQty = coaOEMSData.openOrderQty;
            coaOEMSData.closeOrderState = "Close Orders All";

            if(coaOEMSData.bassoOrderIdea.equals("Bullish")) {
                coaOEMSData.closeOrderSide = "Sell"; // sell to close bullish pos
            } else {
                coaOEMSData.closeOrderSide = "Buy"; // buy to close bearish pos
            }

            orderMS.deleteNOS(coaOEMSData);
            orderMS.deleteFromNOSIDArray(coaOEMSData.symbol);

            orderMS.addUpdateCOA(coaOEMSData.openOrderId, coaOEMSData);

            closeOrdersIDArray = ArrayUtils.add(openOrdersIDArray, coaOEMSData.closeOrderId);

            // assume success
            coaOEMSData.orderConfirmationState = "COA Complete Success - Confirmed";

            // but verify
            getCOACompleteConfirmation(coaOEMSData);
        }

        orderMS.addToCOAIDArray(coaOEMSData.symbol, closeOrdersIDArray);

        closeOrdersIDArray = null;
        openOrdersIDArray = null;
    }

    private void getOngoingCurrRiskVolOrderQty(OEMSData oemsData) {

        riskPercentAvail = 0;
        volRiskPercentAvail = 0;

        for(int i=0; i < openOrdersIDArray.length; i++) {
            nosOEMSData = orderMS.getNOS(openOrdersIDArray[i]);
            oemsData.currCarryQty += nosOEMSData.openOrderQty;
        }

        // curr risk % and qty
        oemsData.currRiskPercent = (oemsData.currCarryQty * oemsData.close) / accountData.nav;
        oemsData.currRiskPercent = roundingWithPrecision(oemsData.currRiskPercent, 3);

        riskPercentAvail = risk.getOngoingRiskPercentThreshold() - oemsData.currRiskPercent;
        if(riskPercentAvail > 0) {

            oemsData.orderQtyPerRisk = (riskPercentAvail * accountData.nav) / oemsData.close;
            oemsData.orderQtyPerRisk = roundingWithPrecision(oemsData.orderQtyPerRisk, 5);

            // curr vol % and qty
            oemsData.currVolRiskPercent = (oemsData.atr * oemsData.close) / accountData.nav;
            oemsData.currVolRiskPercent = roundingWithPrecision(oemsData.currVolRiskPercent, 3);

            volRiskPercentAvail =  risk.getOngoingVolPercentThreshold() - oemsData.currVolRiskPercent;
            if(volRiskPercentAvail > 0) {
                oemsData.orderQtyPerVol = (volRiskPercentAvail * accountData.nav) / oemsData.close;
                oemsData.orderQtyPerVol = roundingWithPrecision(oemsData.orderQtyPerVol, 5);
            } else {
                oemsData.orderQtyPerVol = 0;
            }

        } else {
            oemsData.orderQtyPerRisk = 0;
            oemsData.orderQtyPerVol = 0;
        }

        // finalize open order qty
        // finalize curr risk % - dependent on risk or vol qty chosen
        oemsData.openOrderQty = Math.max(oemsData.orderQtyPerRisk, oemsData.orderQtyPerVol);
        oemsData.openOrderQty = roundingWithPrecision(oemsData.openOrderQty, 5);

        oemsData.currCarryQty += oemsData.openOrderQty;

        oemsData.currRiskPercent = (oemsData.currCarryQty * oemsData.close) / accountData.nav;
        oemsData.currRiskPercent = roundingWithPrecision(oemsData.currRiskPercent, 3);

        // risk test
        if(oemsData.openOrderQty < 1) {
            // no ongoing order
            oemsData.currCarryQty -= oemsData.openOrderQty;
            oemsData.currRiskPercent = (oemsData.currCarryQty * oemsData.close) / accountData.nav;
            oemsData.currRiskPercent = roundingWithPrecision(oemsData.currRiskPercent, 3);
        }
    }

    private void getInitCurrRiskVolOrderQty(OEMSData oemsData) {

        openOrdersIDArray = null;
        updateOpenOrdersIDArray = null;

        oemsData.orderQtyPerRisk = (risk.getInitRiskPercentThreshold() * accountData.nav) / oemsData.close;
        oemsData.orderQtyPerRisk = roundingWithPrecision(oemsData.orderQtyPerRisk, 5);

        oemsData.orderQtyPerVol = (risk.getInitVolPercentThreshold() * accountData.nav) / oemsData.close;
        oemsData.orderQtyPerVol = roundingWithPrecision(oemsData.orderQtyPerVol, 5);

        oemsData.currRiskPercent = risk.getInitRiskPercentThreshold();
        oemsData.currVolRiskPercent = 0;

        oemsData.openOrderQty = oemsData.orderQtyPerRisk;
        oemsData.openOrderQty = roundingWithPrecision(oemsData.openOrderQty, 5);

        oemsData.currCarryQty = oemsData.openOrderQty;
    }

    public static double roundingWithPrecision(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

    private void getInitNOSCompleteConfirmation(OEMSData oemsData) {
        OEMSData confirmOEMS = orderMS.getNOS(oemsData.openOrderId);
        long[] confirmNOSArray = orderMS.getFromNOSIDArray(oemsData.symbol);
        if(confirmOEMS != null && confirmNOSArray != null) {
            oemsData.orderConfirmationState = "Init NOS Complete Success - Confirmed";
        } else {
            oemsData.orderConfirmationState = "Init NOS Complete Failure - Confirmed";
        }
    }

    private void getOngoingNOSCompleteConfirmation(OEMSData oemsData) {
        OEMSData confirmOEMS = orderMS.getNOS(oemsData.openOrderId);
        long[] confirmNOSArray = orderMS.getFromNOSIDArray(oemsData.symbol);
        if(confirmOEMS != null && confirmNOSArray != null) {
            oemsData.orderConfirmationState = "Ongoing NOS Complete Success - Confirmed";
        } else {
            oemsData.orderConfirmationState = "Ongoing NOS Complete Failure - Confirmed";
        }
    }

    private void getCOACompleteConfirmation(OEMSData oemsData) {
        // coa recs not present in nos array or map?
        OEMSData confirmNOSOEMS = orderMS.getNOS(oemsData.openOrderId);
        long[] confirmNOSArray = orderMS.getFromNOSIDArray(oemsData.symbol);

        if (confirmNOSOEMS != null && confirmNOSArray != null) {
            oemsData.orderConfirmationState = "COA Complete Failure - Confirmed";
        }

        // coa recs are present in coa array and map?
        OEMSData confirmCOAOEMS = orderMS.getCOA(oemsData.openOrderId);
        long[] confirmCOAArray = orderMS.getFromCOAIDArray(oemsData.symbol);

        if (confirmCOAOEMS == null && confirmCOAArray == null) {
            oemsData.orderConfirmationState = "COA Complete Failure - Confirmed";
        }
    }
}