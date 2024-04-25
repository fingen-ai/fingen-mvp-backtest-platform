package core.service.strategy;

import strategies.trend.basso.BassoTrendStrategy;
import strategies.trend.basso.BassoTrendStrategyImpl;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;

public class StrategyPubImpl implements StrategyPub, StrategyHandler<StrategyPub> {

    private StrategyPub output;

    private double[] prices = new double[50];
    private double[] high = new double[50];
    private double[] low = new double[50];
    private double[] close = new double[50];
    private int i = 0;
    String bassoTrendStrategyDecision = null;

    BassoTrendStrategy bassoTrendStrategy = new BassoTrendStrategyImpl();

    public StrategyPubImpl() {
    }

    public void init(StrategyPub output) {
        this.output = output;
    }

    public void simpleCall(StrategyData strategyData) throws IOException {
        strategyData.svcStartTs = System.nanoTime();

        low[i] = strategyData.low;
        high[i] = strategyData.high;
        close[i] = strategyData.close;
        prices[i] = (low[i] + high[i] + close[i]) / 3;

        i++;

        if(prices.length >= 50) {
            bassoTrendStrategyDecision = bassoTrendStrategy.getStrategyDecision(prices, high, low, close);
            System.out.println("Decision: " + bassoTrendStrategyDecision);
        }

        strategyData.svcStopTs = System.nanoTime();
        strategyData.svcLatency = strategyData.svcStopTs - strategyData.svcStartTs;
        System.out.println("STRATEGY: " + strategyData);
        output.simpleCall(strategyData);
    }
}