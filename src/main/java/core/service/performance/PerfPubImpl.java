package core.service.performance;

import core.service.oems.pubData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    OrderMappingService orderMS = new OrderMappingService();

    double[] returns = new double[0];
    double[] drawdowns = new double[0];

    CharSequence perfReady = null;
    double initialInvestment = 0;
    double nav = 0;

    Performance perf = new PerformanceImpl(drawdowns);

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();

        if(initialInvestment == 0) {
            initialInvestment = perf.getInitialInvestment();
            nav = initialInvestment;
        }

        if(perfData.coaCloseOrderId > 0) {

            long[] coaArray = orderMS.getFromCOAIDArray(perfData.symbol);

            perfData.initialInvestment = initialInvestment;
            perfData.nav = nav;

            for(int i=0; i < coaArray.length; i++) {

                pubData coaOEMS = orderMS.getCOA(coaArray[i]);

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
                perfData.netROI = roundingWithPrecision(perfData.netROI, 4);

                returns = ArrayUtils.add(returns, perfData.roi);

                perfData.nav += perfData.roi;
                perfData.nav = roundingWithPrecision(perfData.nav, 4);

                perfReady = "Ready";
            }

            if(returns.length > 1) {
                perfData.cagrPercentage = perf.getCAGRPercentage(perfData);
                perfData.cagrPercentage = roundingWithPrecision(perfData.cagrPercentage, 4);

                perfData.sharpeRatio = perf.getSharpeRatio(returns);
                perfData.sharpeRatio = roundingWithPrecision(perfData.sharpeRatio, 4);

                perfData.sortinoRatio = perf.getSortinoRatio(returns);
                perfData.sortinoRatio = roundingWithPrecision(perfData.sortinoRatio, 4);

                perfData.marRatio = perf.getMARRatio(perfData, returns);
                perfData.marRatio = roundingWithPrecision(perfData.marRatio, 4);

                perfData.drawdown = perf.getDrawdown(returns);
                perfData.drawdown = roundingWithPrecision(perfData.drawdown, 4);

                double[] drawdowns = new double[returns.length];
                drawdowns = ArrayUtils.add(drawdowns, perfData.drawdown);
                if(drawdowns != null) {
                    //System.out.println("DD Array: " + drawdowns.length);
                }

                perfData.drawdownPercentage = perf.getDrawdownPercentage(returns);
                perfData.drawdownPercentage = roundingWithPrecision(perfData.drawdownPercentage, 4);

                perfData.returnToAvgDrawdown = perf.getReturnToAvgDrawdown();
                perfData.returnToAvgDrawdown = roundingWithPrecision(perfData.returnToAvgDrawdown, 4);

                perfData.winCount = perf.getWinCount(returns);
                perfData.lossCount = perf.getLossCount(returns);

                perfData.winPercent = perf.getWinPercent();
                perfData.winPercent = roundingWithPrecision(perfData.winPercent, 4);

                perfData.lossPercent = perf.getLossPercent();
                perfData.lossPercent = roundingWithPrecision(perfData.lossPercent, 4);

                perfData.avgWinAmt = perf.getAvgWinAmt(perfData);
                perfData.avgLossAmt = perf.getAvgLossAmt(perfData);

                perfData.avgWinPercent = perf.getAvgWinPercent();
                perfData.avgWinPercent = roundingWithPrecision(perfData.avgWinPercent, 4);

                perfData.avgLossPercent = perf.getAvgLossPercent();
                perfData.avgLossPercent = roundingWithPrecision(perfData.avgLossPercent, 4);

                perfData.edge = perf.getEdge();
                perfData.edge = roundingWithPrecision(perfData.edge, 4);

                //System.out.println(perfData);

                perfData.totalProfit = perf.getTotalProfit();

                perfData.profitFactor = perf.getProfitFactor();
                perfData.profitFactor = roundingWithPrecision(perfData.profitFactor, 4);
            }

            //System.out.println("\n");

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
