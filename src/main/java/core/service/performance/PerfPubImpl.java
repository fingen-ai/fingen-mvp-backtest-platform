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
            perfData.initialInvestment = perf.getInitialInvestment();
            perfData.cagrPercentage = perf.getCAGRPercentage(perfData);
            System.out.println(perfData.cagrPercentage);
            /*
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
             */

        } else {

            if(perfData.coaCloseOrderId > 0) {

                long[] coaArray = orderMS.getFromCOAIDArray(perfData.symbol);

                for(int i=0; i < coaArray.length; i++) {

                    OEMSData coaOEMS = orderMS.getCOA(coaArray[i]);

                    perfData.symbol = coaOEMS.symbol;
                    perfData.coaOpenOrderId = coaOEMS.coaOpenOrderId;
                    perfData.coaCloseOrderId = coaOEMS.coaCloseOrderId;

                    perfData.coaOpenOrderSide = coaOEMS.coaOpenOrderSide;
                    perfData.coaCloseOrderSide = coaOEMS.coaCloseOrderSide;

                    perfData.coaOpenOrderTimestamp = coaOEMS.coaOpenOrderTimestamp;
                    perfData.coaCloseOrderTimestamp = coaOEMS.coaCloseOrderTimestamp;

                    perfData.coaOpenOrderPrice = coaOEMS.coaOpenOrderPrice;
                    perfData.coaCloseOrderPrice = coaOEMS.coaCloseOrderPrice;

                    perfData.coaOpenOrderQty = coaOEMS.coaOpenOrderQty;
                    perfData.coaCloseOrderQty = coaOEMS.coaCloseOrderQty;

                    perfData.coaOpenOrderExpiry = coaOEMS.coaOpenOrderExpiry;
                    perfData.coaCloseOrderExpiry = coaOEMS.coaCloseOrderExpiry;

                    perfData.coaOpenOrderType = coaOEMS.coaOpenOrderType;
                    perfData.coaClosedOrderType = coaOEMS.coaClosedOrderType;

                    perfData.coaOpenOrderState = coaOEMS.coaOpenOrderState;
                    perfData.coaCloseOrderState = coaOEMS.coaCloseOrderState;

                    perfData.roi = (perfData.coaOpenOrderPrice - perfData.coaCloseOrderPrice) / perfData.coaOpenOrderPrice;
                    perfData.roi = roundingWithPrecision(perfData.roi, 4);
                    perfData.netROI += perfData.roi;

                    returns = ArrayUtils.add(returns, perfData.roi);

                    perfData.nav += perfData.roi;
                    perfReady = "Ready";

                    //System.out.println("PERF: " + perfData);
                    //System.out.println("Returns Length: " + returns.length);
                }

                System.out.println("\n");
            }
        }

        returns = null;

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        output.simpleCall(perfData);
    }

    public static double roundingWithPrecision(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
}
