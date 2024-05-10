package core.service.insight;

import account.AccountData;
import core.service.oems.OEMSData;
import oems.map.InsightMappingService;
import oems.map.OrderMappingService;
import performance.Performance;
import performance.PerformanceImpl;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    InsightMappingService insightMS = new InsightMappingService();
    OrderMappingService orderMS = new OrderMappingService();
    Performance perf = new PerformanceImpl();

    AccountData accountData = new AccountData();
    InsightData currNOSInsight = new InsightData();
    long[] arrayOpenOrderID = new long[0];


    Risk risk = new RiskImpl();
    int riskQty = 0;
    int volRiskQty = 0;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {this.output = output;}

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        if(!insightData.bassoOrderIdea.equals("Neutral")) {
            buildNOSInitInsight(insightData);
        }

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        //System.out.println("INSIGHT: " + currNOSInsight);
        output.simpleCall(insightData);
    }

    private void buildNOSInitInsight(InsightData insightData) {

        riskQty = (int) (Math.round (risk.getInitRiskPercentThreshold() * accountData.nav) / insightData.close);
        volRiskQty = (int) (Math.round (risk.getInitVolPercentThreshold() * accountData.nav) / insightData.close);

        insightData.openOrderId = 0;
        insightData.openOrderTimestamp = 0;
        insightData.openOrderState = "Init Insight";

        insightData.openOrderQty = Math.max(riskQty, volRiskQty);
        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "NA";

        System.out.println("INSIGHT: " + insightData);
    }

    private void buildNOSOgoingInsight(InsightData insightData) {
        currNOSInsight.symbol = insightData.symbol;

        // GET OPEN ORDERS FOR SYMBOL
        for(int i=0; i < arrayOpenOrderID.length; i++) {
        }

        //riskQty = (risk.getOngoingRiskPercentThreshold() * accountData.nav) / insightData.close;
        //volRiskQty = (risk.getOngoingVolPercentThreshold() * accountData.nav) / insightData.close;;

        currNOSInsight.closeOrderQty = Math.max(riskQty, volRiskQty);

        currNOSInsight.openOrderId = 0;
        currNOSInsight.openOrderTimestamp = 0;
        currNOSInsight.openOrderState = "Ongoing Insight";
        currNOSInsight.openOrderQty = 0;
        currNOSInsight.openOrderSide = getSide(insightData);;
        currNOSInsight.openOrderPrice = insightData.close;
        currNOSInsight.openOrderExpiry = "NA";

        currNOSInsight.closeOrderId = 0;
        currNOSInsight.closeOrderTimestamp = 0;
        currNOSInsight.closeOrderState = "";
        currNOSInsight.closeOrderQty = 0;
        currNOSInsight.closeOrderSide = "";;
        currNOSInsight.closeOrderPrice = 0;
        currNOSInsight.closeOrderExpiry = "NA";
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
