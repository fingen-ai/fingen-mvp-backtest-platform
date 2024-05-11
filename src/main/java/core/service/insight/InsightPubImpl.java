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
    InsightData prevInsightData = new InsightData(); // previousClose needed for ATR

    long[] openOrdersIDArray = new long[0];
    Risk risk = new RiskImpl();
    int riskQty = 0;
    int volRiskQty = 0;
    double totalPositionQty = 0;
    int recCount = 0;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {this.output = output;}

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        insightData.previousClose = prevInsightData.previousClose;

        if(!insightData.bassoOrderIdea.equals("Neutral")) {

            if(recCount == 0) {
                accountData.nav = 10000;
                recCount++;
            }

            openOrdersIDArray = orderMS.getFromNOSIDArray(insightData.symbol);
            if(openOrdersIDArray != null) {
                buildNOSOngoingInsight(insightData);
            } else {
                buildNOSInitInsight(insightData);
            }
        }

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;

        prevInsightData = insightData;

        //System.out.println("INSIGHT: " + currNOSInsight);
        output.simpleCall(insightData);
    }

    private void buildNOSInitInsight(InsightData insightData) {

        riskQty = (int) (Math.round (risk.getInitRiskPercentThreshold() * accountData.nav) / insightData.close);
        volRiskQty = (int) (Math.round (risk.getInitVolPercentThreshold() * accountData.nav) / insightData.close);

        insightData.openOrderId = 0;
        insightData.openOrderTimestamp = 0;
        insightData.openOrderState = "Init Insight";
        insightData.orderType = "Limit";

        insightData.openOrderQty = Math.min(riskQty, volRiskQty);
        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "GTC";
    }

    private void buildNOSOngoingInsight(InsightData insightData) {
        for(int i=0; i < openOrdersIDArray.length; i++) {
            OEMSData oemsData = orderMS.getNOS(openOrdersIDArray[i]);
            totalPositionQty += oemsData.openOrderQty;
        }

        insightData.currRiskPercent = risk.getCurrentTotalPercentRisk(
                (totalPositionQty * insightData.close), accountData.nav);

        insightData.atr = atr.calculateATR(insightData, 10);

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

        insightData.openOrderQty = Math.min(insightData.orderQtyPerRisk, insightData.orderQtyPerVol);
        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "GTC";

        // reset
        totalPositionQty = 0;

        System.out.println("INSIGHT ONGOING: " + insightData);
    }

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
