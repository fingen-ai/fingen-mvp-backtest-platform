package core.service.insight;

import account.AccountData;
import performance.Performance;
import performance.PerformanceImpl;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    Performance performance = new PerformanceImpl();
    Risk risk = new RiskImpl();
    ATR atr50 = new ATRImpl(50);
    AccountData accountData = new AccountData();

    private InsightPub output;

    public InsightPubImpl() {
    }

    public void init(InsightPub output) {
        this.output = output;
    }

    public void simpleCall(InsightData insightData) {
        insightData.svcStartTs = System.nanoTime();

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
                insightData.tradeAmtPerRiskInstruction = risk.updateOngoingRiskAmtThreshold(insightData.nav, insightData.riskInitPercentThreshold);
                insightData.tradeAmtPerVolInstruction = risk.updateOngoingVolAmtThreshold(insightData.nav, insightData.volInitPercentThreshold);

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
                    insightData.tradeAmtPerRiskInstruction = risk.updateOngoingRiskAmtThreshold(insightData.nav, insightData.riskOngoingPercentThreshold);

                } else {
                    insightData.tradeDecisionInstruction = "Do Not Trade";
                    insightData.tradeAmtPerRiskInstruction = 0;
                }

                if(insightData.currentTotalPercentVolRiskPercent < insightData.volOngoingPercentThreshold) {
                    insightData.tradeDecisionInstruction = "Trade";
                    insightData.tradeAmtPerVolInstruction = risk.updateOngoingVolAmtThreshold(insightData.nav, insightData.volOngoingPercentThreshold);
                } else {
                    insightData.tradeDecisionInstruction = "Do Not Trade";
                    insightData.tradeAmtPerVolInstruction = 0;
                }

            } else {

                insightData.tradeDecisionInstruction = "Do Not Trade";
            }

            // Decide trade amt instruction
            insightData.tradeAmtInstruction = Math.min(insightData.tradeAmtPerRiskInstruction, insightData.tradeAmtPerVolInstruction);
        }

        // Calc current total risk
        insightData.currentTotalPercentRiskPercent = risk.getCurrentTotalPercentRisk(insightData.positionRisk, insightData.nav);
        insightData.currentTotalPercentVolRiskPercent = risk.getCurrentTotalVolPercentRisk(insightData.atr, insightData.nav);

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }
}
