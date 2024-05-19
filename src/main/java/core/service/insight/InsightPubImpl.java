package core.service.insight;

import account.AccountData;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    ATR atr = new ATRImpl();
    Risk risk = new RiskImpl();

    AccountData accountData = new AccountData();

    double prevClose = 0.0; // needed for atr
    String prevBassoIdea = ""; // needed for trend change signaling
    int riskQty = 0;
    int volRiskQty = 0;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {
        accountData.nav = 25000;
        this.output = output;
    }

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        insightData.previousClose = prevClose;
        insightData.prevBassoOrderIdea = prevBassoIdea;

        insightData.atr = atr.calculateATR(insightData, 10);

        if(!insightData.bassoOrderIdea.equals("Neutral")) {
            buildNOSInsight(insightData);
        } else {
            insightData.openOrderSide = "Hold";
        }

        prevClose = insightData.close;
        prevBassoIdea = insightData.bassoOrderIdea;

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;

        /*
        if((recCount >= 49) && (recCount < 404)) {
            System.out.println("INSIGHT:" + recCount);
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
         */

        output.simpleCall(insightData);
    }

    private void buildNOSInsight(InsightData insightData) {
        riskQty = (int) (Math.round (risk.getInitRiskPercentThreshold() * accountData.nav) / insightData.close);
        volRiskQty = (int) (Math.round (risk.getInitVolPercentThreshold() * accountData.nav) / insightData.close);
        insightData.openOrderQty = Math.min(riskQty, volRiskQty);
        insightData.openOrderState = "Init Insight";
        insightData.orderType = "Limit";
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
