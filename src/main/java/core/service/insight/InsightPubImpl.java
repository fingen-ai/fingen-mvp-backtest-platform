package core.service.insight;

import account.AccountData;
import performance.Performance;
import performance.PerformanceImpl;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    Performance performance = new PerformanceImpl();
    AccountData accountData = new AccountData();
    ATR atr50 = new ATRImpl(50);
    Risk risk = new RiskImpl();
    double[] nosArray = null;
    double[] coaArray = null;

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {
        this.output = output;
    }

    public void simpleCall(InsightData insightData) {

        insightData.svcStartTs = System.nanoTime();

        insightData.bassoOrderIdea = "Bullish";

        if(insightData.bassoOrderIdea == null) {
            // Have existing pos - update SL/TP
            updateInsight(insightData);

        } else {

            if(insightData.bassoOrderIdea.equals("Bullish")) {
                // Have existing pos, But diff direction?
                coaInsight(insightData);
                nosInsight(insightData);

                // Have existing pos, But same direction?
                updateInsight(insightData);
                // Add more to the position?
                nosInsight(insightData);

                // Have no pos?
                nosInsight(insightData);
            }

            if(insightData.bassoOrderIdea.equals("Bearish")) {
                // Have existing pos, But diff direction?
                coaInsight(insightData);
                nosInsight(insightData);

                // Have existing pos, But same direction?
                updateInsight(insightData);
                // Add more to the position?
                nosInsight(insightData);

                // Have no pos?
                nosInsight(insightData);
            }
        }

        /*
        insightData.atr = atr50.update(insightData.high, insightData.low, insightData.close, insightData.priorClose);
        insightData.tradeCount = performance.getTradeCount();

        // Trade instructions
        if(insightData.tradeCount == 0) {

            // Initial instructions
            insightData.nav = performance.getInitialInvestment();
            insightData.riskInitPercentThreshold = risk.getInitRiskPercentThreshold();
            insightData.volInitPercentThreshold = risk.getInitVolPercentThreshold();

            if(insightData.bassoOrderIdea != null) {
                insightData.tradeDecisionInstruction = "Trade";
                insightData.tradeQtyPerRiskInstruction = risk.updateOngoingRiskAmtThreshold(insightData.nav, insightData.riskInitPercentThreshold);
                insightData.tradeQtyPerVolInstruction = risk.updateOngoingVolAmtThreshold(insightData.nav, insightData.volInitPercentThreshold);

            } else {
                insightData.tradeDecisionInstruction = "Do Not Trade";
            }

        } else {

            // Ongoing instructions
            insightData.nav = accountData.nav;
            insightData.positionRisk = risk.getCurrentTotalPercentRisk(accountData.positionAmt, accountData.nav);

            insightData.riskOngoingPercentThreshold = risk.getOngoingRiskPercentThreshold();
            insightData.volOngoingPercentThreshold = risk.getOngoingVolPercentThreshold();

            if(insightData.bassoOrderIdea != null) {

                if(insightData.currentTotalPercentRiskPercent < insightData.riskOngoingPercentThreshold) {
                    insightData.tradeDecisionInstruction = "Trade";
                    insightData.tradeQtyPerRiskInstruction = risk.updateOngoingRiskAmtThreshold(insightData.nav, insightData.riskOngoingPercentThreshold);

                } else {
                    insightData.tradeDecisionInstruction = "Do Not Trade";
                    insightData.tradeQtyPerRiskInstruction = 0;
                }

                if(insightData.currentTotalPercentVolRiskPercent < insightData.volOngoingPercentThreshold) {
                    insightData.tradeDecisionInstruction = "Trade";
                    insightData.tradeQtyPerVolInstruction = risk.updateOngoingVolAmtThreshold(insightData.nav, insightData.volOngoingPercentThreshold);
                } else {
                    insightData.tradeDecisionInstruction = "Do Not Trade";
                    insightData.tradeQtyPerVolInstruction = 0;
                }

            } else {

                insightData.tradeDecisionInstruction = "Do Not Trade";
            }

            // Decide trade amt instruction
            insightData.tradeQtyInstruction = Math.min(insightData.tradeQtyPerRiskInstruction, insightData.tradeQtyPerVolInstruction);
        }

        // Calc current total risk
        insightData.currentTotalPercentRiskPercent = risk.getCurrentTotalPercentRisk(insightData.positionRisk, insightData.nav);
        insightData.currentTotalPercentVolRiskPercent = risk.getCurrentTotalVolPercentRisk(insightData.atr, insightData.nav);
        */

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }

    private void nosInsight(InsightData insightData) {
        insightData.nosOrderInsight = "NOS";
    }

    private void updateInsight(InsightData insightData) {
        insightData.updOrderInsight = "UPD";
    }

    private void coaInsight(InsightData insightData) {
        insightData.coaOrderInsight = "COA";
    }
}
