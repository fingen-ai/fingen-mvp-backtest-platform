package core.service.oems;

import account.AccountData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    int recCount = 0;

    OEMSData oemsNOSMap = new OEMSData();
    AccountData accountData = new AccountData();

    Risk risk = new RiskImpl();
    OrderMappingService orderMS = new OrderMappingService();

    String prevBassoOrderIdea = "";
    long[] openOrdersIDArray =  new long[0];
    long[] updateOpenOrdersIDArray =  new long[0];
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

                    placeCOAOrder(oemsData, openOrdersIDArray);

                    if(oemsData.closeOrderState.equals("Close Orders All")) {
                        System.out.println("NOS INIT via COA");
                        placeNOSInitOrder(oemsData);
                    }

                // sl sell cos or nos ongoing, still trending
                } else if (oemsData.openOrderSide.equals("Buy")) {
                    if (oemsData.close < oemsData.openOrderSLPrice) {
                        System.out.println("COS BUY");
                        placeCOSOrder(oemsData);
                    } else {
                        System.out.println("NOS ONGOING");
                        placeNOSOngoingOrder(oemsData);
                    }

                    // sl buy cos or nos ongoing, still trending
                } else if (oemsData.openOrderSide.equals("Sell")) {
                    if (oemsData.close > oemsData.openOrderSLPrice) {
                        System.out.println("COS SELL");
                        placeCOSOrder(oemsData);
                    }else {
                        System.out.println("NOS ONGOING");
                        placeNOSOngoingOrder(oemsData);
                    }
                }

            } else {
                System.out.println("NOS INIT");
                placeNOSInitOrder(oemsData);
            }

        } else {

            //  neutral
            oemsData.openOrderSide = "Hold";
        }

        prevBassoOrderIdea = oemsData.bassoOrderIdea;

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;


        //if((recCount >= 49) && (recCount < 407)) {
            System.out.println("OEMS:" + recCount);
            System.out.println("OEMS: " + oemsData.openOrderId);
            System.out.println("OEMS: " + oemsData.prevBassoOrderIdea);
            System.out.println("OEMS: " + oemsData.bassoOrderIdea);
            System.out.println("OEMS: " + oemsData.openOrderSide);
            System.out.println("OEMS: " + oemsData.openOrderQty);
            System.out.println("OEMS: " + oemsData.currCarryQty);
            System.out.println("OEMS: " + oemsData.openOrderExpiry);
            System.out.println("OEMS: " + oemsData.openOrderState);
            System.out.println("OEMS: " + oemsData.orderConfirmationState);
            System.out.println("OEMS: " + oemsData.currRiskPercent);
            System.out.println("OEMS: " + oemsData.currVolRiskPercent);
            System.out.println("OEMS: " + oemsData.close);
            System.out.println("OEMS: " + oemsData.openOrderSLPrice);
            System.out.println("\n");
        //}
        recCount++;

        openOrdersIDArray = null;
        updateOpenOrdersIDArray = null;
        oemsNOSMap = null;

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

    private void placeCOSOrder(OEMSData oemsData) {
        // delete the ID from the ID array
        for(int i=0; i < openOrdersIDArray.length; i++) {
            if(oemsData.openOrderId == openOrdersIDArray[i]) {

                oemsData.closeOrderId = System.nanoTime();
                oemsData.closeOrderTimestamp = System.nanoTime();
                oemsData.closeOrderExpiry = "GTC";
                oemsData.closeOrderState = "Close Order Single";
                orderMS.addUpdateCOS(oemsData.openOrderId, oemsData);

                orderMS.deleteNOS(oemsData);

                updateOpenOrdersIDArray = ArrayUtils.remove(openOrdersIDArray, i);
                orderMS.addToNOSIDArray(oemsData.symbol, updateOpenOrdersIDArray);
            }
        }

        getCOSAndCOACompleteConfirmation(oemsData, "COS");
    }

    private void placeCOAOrder(OEMSData oemsData, long[] openOrdersIDArray) {

        for(int i=0; i < openOrdersIDArray.length; i++) {
            oemsData.closeOrderId = System.nanoTime();
            oemsData.closeOrderTimestamp = System.nanoTime();
            oemsData.closeOrderExpiry = "GTC";
            oemsData.closeOrderState = "Close Orders All";

            orderMS.addUpdateCOS(oemsData.openOrderId, oemsData);

            orderMS.deleteNOS(oemsData);

            orderMS.deleteFromNOSIDArray(oemsData.symbol);

            getCOSAndCOACompleteConfirmation(oemsData, "COA");
        }
    }

    private void getOngoingCurrRiskVolOrderQty(OEMSData oemsData) {

        riskPercentAvail = 0;
        volRiskPercentAvail = 0;

        for(int i=0; i < openOrdersIDArray.length; i++) {
            oemsNOSMap = orderMS.getNOS(openOrdersIDArray[i]);
            oemsData.currCarryQty += oemsNOSMap.openOrderQty;
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

    private void getCOSAndCOACompleteConfirmation(OEMSData oemsData, String cosOrCOA) {
        OEMSData confirmOEMS = orderMS.getNOS(oemsData.openOrderId);
        long[] confirmNOSArray = orderMS.getFromNOSIDArray(oemsData.symbol);

        if(cosOrCOA.equals("COS")) {
            if (confirmOEMS == null && confirmNOSArray == null) {
                oemsData.orderConfirmationState = "COS Complete Success - Confirmed";
            } else {
                oemsData.orderConfirmationState = "COS Complete Failure - Confirmed";
            }
        } else {
            if (confirmOEMS == null && confirmNOSArray == null) {
                oemsData.orderConfirmationState = "COA Complete Success - Confirmed";
            } else {
                oemsData.orderConfirmationState = "COA Complete Failure - Confirmed";
            }
        }

    }
}