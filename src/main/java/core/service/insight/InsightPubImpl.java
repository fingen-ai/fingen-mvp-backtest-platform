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
    OEMSData currNOSInsight = new OEMSData();
    OEMSData openNOSOrder = new OEMSData();
    long[] arrayOpenOrderID = new long[0];


    Risk risk = new RiskImpl();
    double riskQty = 0;
    double volRiskQty = 0;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {this.output = output;}

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        if(!insightData.bassoOrderIdea.equals("Neutral")) {

            if(insightData.bassoOrderIdea.equals("Bullish")) {
                System.out.println("INSIGHT - BULLS: " + insightData.bassoOrderIdea);
            } else {
                System.out.println("INSIGHT - BEARS: " + insightData.bassoOrderIdea);
            }

        }

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }

    private void buildNOSInitInsight(InsightData insightData) {
        currNOSInsight.symbol = insightData.symbol;

        riskQty = (risk.getInitRiskPercentThreshold() * accountData.nav) / insightData.close;
        volRiskQty = (risk.getInitVolPercentThreshold() * accountData.nav) / insightData.close;;
        currNOSInsight.closeOrderQty = Math.max(riskQty, volRiskQty);

        currNOSInsight.openOrderId = 0;
        currNOSInsight.openOrderTimestamp = 0;
        currNOSInsight.openOrderState = "";
        currNOSInsight.openOrderQty = 0;
        currNOSInsight.openOrderSide = getSide(insightData);;
        currNOSInsight.openOrderPrice = insightData.close;
        currNOSInsight.openOrderExpiry = "";

        currNOSInsight.closeOrderId = 0;
        currNOSInsight.closeOrderTimestamp = 0;
        currNOSInsight.closeOrderState = "";
        currNOSInsight.closeOrderQty = 0;
        currNOSInsight.closeOrderSide = "";;
        currNOSInsight.closeOrderPrice = 0;
        currNOSInsight.closeOrderExpiry = "";

        System.out.println("INSIGHT - Init Curr NOS Insight: " + currNOSInsight);

        insightMS.addNOSInsight(currNOSInsight.recId, currNOSInsight);
    }

    private void buildNOSOgoingInsight(InsightData insightData) {
        currNOSInsight.symbol = insightData.symbol;

        for(int i=0; i < arrayOpenOrderID.length; i++) {
            System.out.println("Ongoing Curr NOS Insight ... See Our Open Order ID: " + arrayOpenOrderID[i]);
        }

        //riskQty = (risk.getOngoingRiskPercentThreshold() * accountData.nav) / insightData.close;
        //volRiskQty = (risk.getOngoingVolPercentThreshold() * accountData.nav) / insightData.close;;

        currNOSInsight.closeOrderQty = Math.max(riskQty, volRiskQty);

        currNOSInsight.openOrderId = 0;
        currNOSInsight.openOrderTimestamp = 0;
        currNOSInsight.openOrderState = "";
        currNOSInsight.openOrderQty = 0;
        currNOSInsight.openOrderSide = getSide(insightData);;
        currNOSInsight.openOrderPrice = insightData.close;
        currNOSInsight.openOrderExpiry = "";

        currNOSInsight.closeOrderId = 0;
        currNOSInsight.closeOrderTimestamp = 0;
        currNOSInsight.closeOrderState = "";
        currNOSInsight.closeOrderQty = 0;
        currNOSInsight.closeOrderSide = "";;
        currNOSInsight.closeOrderPrice = 0;
        currNOSInsight.closeOrderExpiry = "";

        insightMS.addNOSInsight(currNOSInsight.recId, currNOSInsight);
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
