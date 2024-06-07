package core.service.performance;

import org.apache.commons.lang3.ArrayUtils;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    CharSequence perfReady = null;
    double[] returns = new double[0];
    double[] drawdowns = new double[0];

    Performance perf = new PerformanceImpl(drawdowns);

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();

        if(perfReady != null) {
            perfData.cagrPercentage = perf.getCAGRPercentage();
            perfData.sharpeRatio = perf.getSharpeRatio();
            perfData.sortinoRatio = perf.getSortinoRatio();
            perfData.returnToAvgDrawdown = perf.getReturnToAvgDrawdown();
            perfData.marRatio = perf.getMARRatio();
            perfData.maxDrawdownPercentage = perf.getMaxDrawdownPercentage();
            perfData.winCount = perf.getWinCount();
            perfData.lossCount = perf.getLossCount();
            perfData.reliabilityPercentage = perf.getWinPercent();
            perfData.totalProfit = perf.getTotalProfit();
            perfData.profitFactor = perf.getProfitFactor();
        } else {
            returns = ArrayUtils.add(returns, (perfData.openOrderPrice-perfData.close));
        }

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        System.out.println("PERF: " + perfData);
        System.out.println("\n");

        output.simpleCall(perfData);
    }
}
