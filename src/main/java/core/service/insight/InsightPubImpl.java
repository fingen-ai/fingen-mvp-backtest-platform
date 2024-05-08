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

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }
}
