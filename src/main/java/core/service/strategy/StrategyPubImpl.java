package core.service.strategy;

import strategies.trend.basso.BassoTrendStrategy;
import strategies.trend.basso.BassoTrendStrategyImpl;

import java.io.IOException;

public class StrategyPubImpl implements StrategyPub, StrategyHandler<StrategyPub> {

    private StrategyPub output;

    public StrategyPubImpl() {
    }

    public void init(StrategyPub output) {
        this.output = output;
    }

    public void simpleCall(StrategyData strategyData) throws IOException {
        strategyData.svcStartTs = System.nanoTime();

        BassoTrendStrategy bassoTrendStrategy = new BassoTrendStrategyImpl();
        bassoTrendStrategy.getStrategyDecision();

        strategyData.svcStopTs = System.nanoTime();
        strategyData.svcLatency = strategyData.svcStopTs - strategyData.svcStartTs;
        System.out.println("STRATEGY: " + strategyData);
        output.simpleCall(strategyData);
    }
}