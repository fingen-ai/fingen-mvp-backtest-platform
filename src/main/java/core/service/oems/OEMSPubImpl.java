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
    double prevSLPrice = 0;
    double maxSLPrice = 0;

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
                    placeCoaOrder(oemsData);

                    if(coaOEMSData.coaCloseOrderState.equals("Close Orders All")) {
                        placeNosInitOrder(oemsData);
                    }

                // coa on bullish sl - tho no reversal, yet
                } else if (oemsData.openOrderSide.equals("Buy")) {

                    if (oemsData.close <= prevSLPrice) {
                        oemsData.openOrderState = "SL Sell Triggered";
                        placeCoaOrder(oemsData);
                    } else {
                        placeNosOngoingOrder(oemsData);
                    }

                // coa on bearish sl - tho no reversal, yet
                } else if (oemsData.openOrderSide.equals("Sell")) {
                    if (oemsData.close >= prevSLPrice) {
                        oemsData.openOrderState = "SL Buy Triggered";
                        placeCoaOrder(oemsData);
                    }else {
                        placeNosOngoingOrder(oemsData);
                    }
                }

            } else {

                placeNosInitOrder(oemsData);
            }
        }

        prevBassoOrderIdea = oemsData.bassoOrderIdea;
        prevSLPrice = oemsData.openOrderSLPrice;

        getAllRecCount(oemsData);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;

        recCount++;

        openOrdersIDArray = null;
        updateOpenOrdersIDArray = null;
        closeOrdersIDArray =  null;
        volRiskPercentAvail = 0.0;
        riskPercentAvail = 0.0;

        output.simpleCall(oemsData);
    }

    private void placeNosInitOrder(OEMSData oemsData) {

        getInitCurrRiskVolOrderQty(oemsData);

        oemsData.openOrderId = System.nanoTime();
        oemsData.openOrderTimestamp = System.nanoTime();
        oemsData.openOrderExpiry = "GTC";
        oemsData.openOrderState = "Init New Order Single";

        orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);

        updateOpenOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
        orderMS.addToNOSIDArray(oemsData.symbol, updateOpenOrdersIDArray);
    }

    private void placeNosOngoingOrder(OEMSData oemsData) {

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

        // ongoing order
        if(oemsData.currRiskPercent <= risk.getOngoingRiskPercentThreshold() ) {

            oemsData.openOrderId = System.nanoTime();
            oemsData.openOrderTimestamp = System.nanoTime();

            if(oemsData.openOrderQty > 0) {
                oemsData.openOrderExpiry = "GTC";
                oemsData.openOrderState = "Ongoing New Order Single";

                orderMS.addUpdateNOS(oemsData.openOrderId, oemsData);

                updateOpenOrdersIDArray = ArrayUtils.add(openOrdersIDArray, oemsData.openOrderId);
                orderMS.addToNOSIDArray(oemsData.symbol, updateOpenOrdersIDArray);

                oemsData.nosRecCount = openOrdersIDArray.length;

            } else {
                oemsData.openOrderExpiry = "NA";
                oemsData.openOrderState = "Hold: Ongoing New Order Single >= Ongoing Risk %";
            }
        }
    }

    private void placeCoaOrder(OEMSData oemsData) {

        closeOrdersIDArray = orderMS.getFromNOSIDArray(oemsData.symbol);

        for(int i=0; i < openOrdersIDArray.length; i++) {

            coaOEMSData = orderMS.getNOS(openOrdersIDArray[i]);

            coaOEMSData.coaOpenOrderId = openOrdersIDArray[i];
            coaOEMSData.coaOpenOrderPrice = coaOEMSData.openOrderPrice;
            coaOEMSData.coaCloseOrderId = coaOEMSData.openOrderId;
            coaOEMSData.coaCloseOrderTimestamp = System.nanoTime();
            coaOEMSData.coaCloseOrderPrice = oemsData.close;
            coaOEMSData.coaCloseOrderExpiry = "GTC";
            coaOEMSData.coaClosedOrderType ="LMT";
            coaOEMSData.coaCloseOrderQty = coaOEMSData.openOrderQty;
            coaOEMSData.coaCloseOrderState = "Close Orders All";

            oemsData.openOrderId = coaOEMSData.coaOpenOrderId;
            oemsData.coaOpenOrderPrice = coaOEMSData.coaOpenOrderPrice;
            oemsData.coaCloseOrderId = coaOEMSData.coaCloseOrderId;
            oemsData.coaCloseOrderTimestamp = coaOEMSData.coaCloseOrderTimestamp;
            oemsData.coaCloseOrderPrice = coaOEMSData.coaCloseOrderPrice;

            if(coaOEMSData.bassoOrderIdea.equals("Bullish")) {
                coaOEMSData.coaCloseOrderSide = "Sell"; // sell to close bullish pos
            } else {
                coaOEMSData.coaCloseOrderSide = "Buy"; // buy to close bearish pos
            }

            orderMS.deleteNOS(coaOEMSData);
            orderMS.deleteFromNOSIDArray(coaOEMSData.symbol);

            orderMS.addUpdateCOA(coaOEMSData.openOrderId, coaOEMSData);

            closeOrdersIDArray = ArrayUtils.add(closeOrdersIDArray, coaOEMSData.openOrderId);
        }

        orderMS.addToCOAIDArray(coaOEMSData.symbol, closeOrdersIDArray);
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

        maxSLPrice = oemsData.close * 1.01;
        if(oemsData.openOrderSLPrice > maxSLPrice) {
            oemsData.openOrderSLPrice = maxSLPrice;
        }
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

    private void getAllRecCount(OEMSData oemsData) {
        long[] coaArray = orderMS.getFromCOAIDArray(oemsData.symbol);
        long[] nosArray = orderMS.getFromNOSIDArray(oemsData.symbol);
        if(coaArray != null) {
            oemsData.allRecCount += coaArray.length;
            oemsData.coaRecCount = coaArray.length;
        }
        if(nosArray != null) {
            oemsData.allRecCount += nosArray.length;
            oemsData.nosRecCount = nosArray.length;
        }
    }
}