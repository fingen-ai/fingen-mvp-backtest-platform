package core.service.insight;

import account.AccountData;
import org.glassfish.grizzly.http.io.BinaryNIOInputSource;
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

        getSide(insightData);
        getPositions(insightData);

        if(insightData.orderSide.equals("Buy")) {
            addOrder(insightData);
        }

        if(insightData.orderSide.equals("Sell")) {
            updateOrder(insightData);
        }

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }

    private void getSide(InsightData insightData) {
        if(insightData.bassoOrderIdea.equals("Bullish")) {
            insightData.orderSide = "Buy";
        }
        if(insightData.bassoOrderIdea.equals("Bearish")) {
            insightData.orderSide = "Sell";
        }
        if(insightData.bassoOrderIdea.equals("Neutral")) {
            insightData.orderSide = "Hold";
        }
    }

    private void getPositions(InsightData insightData) {
        // Call Chronicle Map to get current position in given asset
    }

    private void closePositions(InsightData insightData) {
        // Call Chronicle Map to close all positives in given asset
    }

    private void addOrder(InsightData insightData) {
        // Call Chronicle Map to add new position in given asset
    }

    private void updateOrder(InsightData insightData) {
        // Call Chronicle Map to update current positions in given asset; update stop-loss, take-profit
    }

    // Populate all InsightData elements
}
