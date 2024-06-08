package core.service.performance;

import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {


    CharSequence perfReady = null;
    double[] returns = new double[0];
    double[] drawdowns = new double[0];
    double roi = 0;

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

            roi = (perfData.openOrderPrice - perfData.closeOrderPrice) / perfData.openOrderPrice;
            returns = ArrayUtils.add(returns, roi);

            System.out.println("COA: " + perfData.closeOrderId + " @ " + perfData.closeOrderPrice);
            System.out.println("ROI: " + roi);
            System.out.println("Returns Length: " + returns.length);
            System.out.println("\n");
        }

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        output.simpleCall(perfData);
    }
}
