package core.service.strategy;

import strategies.trend.basso.BassoTrendStrategy;
import strategies.trend.basso.BassoTrendStrategyImpl;

import java.io.IOException;

public class StrategyPubImpl implements StrategyPub, StrategyHandler<StrategyPub> {

    private StrategyPub output;

    private double[] prices = new double[50];
    private double[] high = new double[50];
    private double[] low = new double[50];
    private double[] close = new double[50];
    private int i = 0;
    String bassoOrderIdea = null;
    BassoTrendStrategy bassoTrendStrategy = new BassoTrendStrategyImpl();
    String bassoTradeDecision = null;

    public StrategyPubImpl() {
    }

    public void init(StrategyPub output) {
        this.output = output;
    }

    public void simpleCall(StrategyData strategyData) throws IOException {
        strategyData.svcStartTs = System.nanoTime();

        bassTrendStrategy(strategyData);

        strategyData.svcStopTs = System.nanoTime();
        strategyData.svcLatency = strategyData.svcStopTs - strategyData.svcStartTs;
        System.out.println("STRATEGY: " + strategyData);
        output.simpleCall(strategyData);
    }

    private void bassTrendStrategy(StrategyData strategyData) {
        low[i] = strategyData.low;
        high[i] = strategyData.high;
        close[i] = strategyData.close;
        prices[i] = (low[i] + high[i] + close[i]) / 3;

        if(i == 49) {
            strategyData.bassoOrderIdea = bassoTrendStrategy.getStrategyDecision(prices, high, low, close);
            strategyData.lhcAvgPrice = prices[i];
            removeTheElement(low, 0);
            removeTheElement(high, 0);
            removeTheElement(close, 0);
            removeTheElement(prices, 0);
            i = 49;
        } else {
            strategyData.bassoOrderIdea = "Neutral";
            i++;
        }
    }

    private static double[] removeTheElement(double[] arr, int index)
    {
        if (arr == null || index < 0
                || index >= arr.length) {
            return arr;
        }

        // Create another array of size one less
        double[] updateArray = new double[arr.length - 1];

        // Copy the elements except the index from original array to the other array
        for (int i = 0, k = 0; i < arr.length; i++) {

            // if the index is the removal element index
            if (i == index) {
                continue;
            }

            // if the index is not the removal element index
            updateArray[k++] = arr[i];
        }

        // return the resultant array
        return updateArray;
    }
}