package core.service.insight;

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

    private InsightPub output;

    public InsightPubImpl() {
    }

    public void init(InsightPub output) {
        this.output = output;
    }

    public void simpleCall(InsightData insightData) {
        insightData.svcStartTs = System.nanoTime();

        // Account DTO Needed - for account info
        insightData.equity = performance.getInitialInvestment();
        insightData.tradeCount = performance.getTradeCount();

        insightData.atr = atr50.update(insightData.high, insightData.low, insightData.close, insightData.priorClose);

        if(insightData.tradeCount == 0) {
            insightData.riskInitPercentThreshold = risk.getInitRiskPercentThreshold();
            insightData.volInitPercentThreshold = risk.getInitVolPercentThreshold();
            insightData.tradeInstructionStatus = "Trade";

        } else {
            insightData.positionRisk = 0; // get from broker client 'Open Trades' info
            insightData.riskOngoingPercentThreshold = risk.getOngoingRiskPercentThreshold();
            insightData.volOngoingPercentThreshold = risk.getOngoingVolPercentThreshold();

            if(insightData.riskOngoingPercentThreshold < risk.getOngoingRiskPercentThreshold()) {
                insightData.tradeInstructionStatus = "Trade";
            } else {
                insightData.tradeInstructionStatus = "Do Not Trade";
            }

            if(insightData.volOngoingPercentThreshold < risk.getOngoingVolPercentThreshold()) {
                insightData.tradeInstructionStatus = "Trade";
            } else {
                insightData.tradeInstructionStatus = "Do Not Trade";
            }
        }

        // Calc current total risk
        insightData.currentTotalPercentRiskPercent = risk.getCurrentTotalPercentRisk(insightData.positionRisk, insightData.equity);
        insightData.currentTotalPercentVolRiskPercent = risk.getCurrentTotalVolPercentRisk(insightData.atr, insightData.equity);

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }
}
