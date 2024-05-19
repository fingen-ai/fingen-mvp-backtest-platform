package core.service.insight;

import account.AccountData;
import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    OrderMappingService orderMS = new OrderMappingService();
    ATR atr = new ATRImpl();

    AccountData accountData = new AccountData();
    double prevClose = 0.0; // needed for atr
    String prevBassoIdea = ""; // needed for trend change signaling

    long[] openOrdersIDArray = new long[0];
    Risk risk = new RiskImpl();
    int riskQty = 0;
    int volRiskQty = 0;
    double totalCurrPositionQty = 0;
    int recCount = 0;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {
        accountData.nav = 10000;
        this.output = output;
    }

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        insightData.previousClose = prevClose;
        insightData.prevBassoOrderIdea = prevBassoIdea;

        insightData.atr = atr.calculateATR(insightData, 10);

        if(!insightData.bassoOrderIdea.equals("Neutral")) {

            //openOrdersIDArray = orderMS.getFromNOSIDArray(insightData.symbol);
            //if(openOrdersIDArray != null) {
                //buildNOSOngoingInsight(insightData);
            //} else {
                buildNOSInitInsight(insightData);
            //}
        } else {
            insightData.openOrderSide = "Hold";
        }

        prevClose = insightData.close;
        prevBassoIdea = insightData.bassoOrderIdea;

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;

        if((recCount >= 49) && (recCount < 404)) {
            System.out.println("INSIGHT: " + insightData.prevBassoOrderIdea);
            System.out.println("INSIGHT: " + insightData.bassoOrderIdea);
            System.out.println("INSIGHT: " + insightData.openOrderSide);
            System.out.println("INSIGHT: " + insightData.openOrderQty);
            System.out.println("INSIGHT: " + insightData.openOrderExpiry);
            System.out.println("INSIGHT: " + insightData.openOrderState);
            System.out.println("INSIGHT: " + risk.getInitRiskPercentThreshold());
            System.out.println("INSIGHT: " + insightData.currRiskPercent);
            System.out.println("INSIGHT: " + risk.getInitVolPercentThreshold());
            System.out.println("INSIGHT: " + insightData.currVolRiskPercent);
            System.out.println("\n");
        }
        recCount++;

        output.simpleCall(insightData);
    }

    private void buildNOSInitInsight(InsightData insightData) {

        riskQty = (int) (Math.round (risk.getInitRiskPercentThreshold() * accountData.nav) / insightData.close);
        volRiskQty = (int) (Math.round (risk.getInitVolPercentThreshold() * accountData.nav) / insightData.close);

        insightData.openOrderQty = Math.min(riskQty, volRiskQty);

        insightData.openOrderState = "Init Insight";
        insightData.orderType = "Limit";
        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "GTC";

        System.out.println("INIT !!" + recCount);
    }

    /*
    private void buildNOSOngoingInsight(InsightData insightData) {
        for(int i=0; i < openOrdersIDArray.length; i++) {
            OEMSData oemsData = orderMS.getNOS(openOrdersIDArray[i]);
            totalPositionQty += oemsData.openOrderQty;
        }

        insightData.currRiskPercent = risk.getCurrentTotalPercentRisk(
                (totalPositionQty * insightData.close), accountData.nav);

        insightData.currVolRiskPercent = risk.getCurrentTotalVolPercentRisk(
                (insightData.atr * insightData.close), accountData.nav);

        double riskPercentAvail = risk.getOngoingRiskPercentThreshold() - insightData.currRiskPercent;
        if(riskPercentAvail > 0) {
            insightData.orderQtyPerRisk = (int) (Math.round (riskPercentAvail * accountData.nav) / insightData.close);
        }

        double volRiskPercentAvail =  risk.getOngoingVolPercentThreshold() - insightData.currVolRiskPercent;
        if(volRiskPercentAvail > 0) {
            insightData.orderQtyPerVol = (int) (Math.round (volRiskPercentAvail * accountData.nav) / insightData.close);
        }

        insightData.openOrderId = 0;
        insightData.openOrderTimestamp = 0;
        insightData.openOrderState = "Ongoing Insight";
        insightData.orderType = "Limit";

        if(Math.min(insightData.orderQtyPerRisk, insightData.orderQtyPerVol) > 0) {
            insightData.openOrderQty = Math.min(insightData.orderQtyPerRisk, insightData.orderQtyPerVol);
        } else {
            insightData.openOrderQty = 0;
        }

        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "GTC";

        // reset
        totalPositionQty = 0;

        System.out.println("ONGOING !!" + recCount);
    }
     */

    private String getSide(InsightData insightData) {
        if(insightData.bassoOrderIdea.equals("Bullish")) {
            insightData.orderSide = "Buy";
        }
        if(insightData.bassoOrderIdea.equals("Bearish")) {
            insightData.orderSide = "Sell";
        }
        if(insightData.bassoOrderIdea.equals("Neutral")) {
            insightData.orderSide = "Hold";
        }
        return insightData.orderSide;
    }
}
