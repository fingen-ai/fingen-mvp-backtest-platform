package core.service.insight;

import account.AccountData;
import core.service.oems.OEMSData;
import oems.map.InsightMappingService;
import oems.map.OrderMappingService;
import performance.Performance;
import performance.PerformanceImpl;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    InsightMappingService insightMS = new InsightMappingService();
    OrderMappingService orderMS = new OrderMappingService();
    Performance perf = new PerformanceImpl();
    ATR atr = new ATRImpl(50);

    AccountData accountData = new AccountData();
    InsightData currNOSInsight = new InsightData();
    long[] openOrdersIDArray = new long[0];


    Risk risk = new RiskImpl();
    int riskQty = 0;
    int volRiskQty = 0;
    double totalPositionQty = 0;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {this.output = output;}

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        if(!insightData.bassoOrderIdea.equals("Neutral")) {

            openOrdersIDArray = orderMS.getFromNOSIDArray(insightData.symbol);
            if(openOrdersIDArray != null) {
                buildNOSOngoingInsight(insightData);
            } else {
                buildNOSInitInsight(insightData);
            }
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

        insightData.openOrderQty = Math.min(riskQty, volRiskQty);
        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "GTC";

        System.out.println("INSIGHT: " + insightData);
    }

    private void buildNOSOngoingInsight(InsightData insightData) {

        for(int i=0; i < openOrdersIDArray.length; i++) {
            OEMSData oemsData = orderMS.getNOS(openOrdersIDArray[i]);
            totalPositionQty += oemsData.openOrderQty;
        }

        // compute current risk %
        insightData.currRiskPercent = risk.getCurrentTotalPercentRisk(totalPositionQty, accountData.nav);

        // compute current vol %
        insightData.atr = atr.calculateTR(insightData.high, insightData.low, insightData.close);
        insightData.currRiskPercent = risk.getCurrentTotalVolPercentRisk(insightData.atr, accountData.nav);

        // validate current risk % < risk % threshold
        double riskPercentAvail = risk.getOngoingRiskPercentThreshold() - insightData.currRiskPercent;
        if(riskPercentAvail > 0) {
            riskQty = (int) (Math.round (riskPercentAvail * accountData.nav) / insightData.close);
            System.out.println("INSIGHT ONGOING RISK NOS: " + insightData);
        }

        // validate current vol % < vol % threshold
        double volRiskPercentAvail =  risk.getOngoingVolPercentThreshold() - insightData.currVolRiskPercent;
        if(riskPercentAvail > 0) {
            volRiskQty = (int) (Math.round (volRiskPercentAvail * accountData.nav) / insightData.close);
            System.out.println("INSIGHT ONGOING VOL NOS: " + insightData);
        }

        insightData.openOrderId = 0;
        insightData.openOrderTimestamp = 0;
        insightData.openOrderState = "Ongoing Insight";

        insightData.openOrderQty = Math.min(riskQty, volRiskQty);
        insightData.openOrderSide = getSide(insightData);;
        insightData.openOrderPrice = insightData.close;
        insightData.openOrderExpiry = "GTC";
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
