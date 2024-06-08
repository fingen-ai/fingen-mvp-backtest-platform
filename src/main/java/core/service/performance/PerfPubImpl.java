package core.service.performance;

import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    OrderMappingService orderMS = new OrderMappingService();

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

            if(perfData.coaCloseOrderId > 0) {

                long[] coaArray = orderMS.getFromCOAIDArray(perfData.symbol);

                for(int i=0; i < coaArray.length; i++) {

                    OEMSData coaOEMS = orderMS.getCOA(coaArray[i]);

                    roi = (coaOEMS.coaOpenOrderPrice - coaOEMS.coaCloseOrderPrice) / coaOEMS.coaOpenOrderPrice;
                    roi = roundingWithPrecision(roi, 4);

                    System.out.println("SIDE: " + coaOEMS.openOrderSide);
                    System.out.println("CLOSE PRICE: " + coaOEMS.coaCloseOrderPrice);
                    System.out.println("OPEN PRICE: " + coaOEMS.coaOpenOrderPrice);

                    System.out.println("ROI: " + roi);
                    returns = ArrayUtils.add(returns, roi);
                    System.out.println("Returns Length: " + returns.length);
                    System.out.println("\n");
                }
            }
        }

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        output.simpleCall(perfData);
    }

    public static double roundingWithPrecision(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
