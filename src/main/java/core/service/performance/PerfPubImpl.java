package core.service.performance;

import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    Performance perf = new PerformanceImpl();
    OrderMappingService orderMS = new OrderMappingService();

    OEMSData closeOEMS = new OEMSData();
    OEMSData openOEMS = new OEMSData();

    long[] coaIDArray = new long[0];
    long[] nosIDArray = new long[0];
    long recCount = 0;

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();


        getNosPosition(perfData);
        getCoaPosition(perfData);
        //getNosCoaMatchTest(perfData);
        //getRisk(perfData);
        //getPerformance(perfData);

        System.out.println("\n");

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        nosIDArray = null;
        coaIDArray = null;
        openOEMS = null;
        closeOEMS = null;

        recCount++;

        output.simpleCall(perfData);
    }

    private void getNosPosition(PerfData perfData) {
        perfData.tradeCount = 0;
        nosIDArray = orderMS.getFromNOSIDArray(perfData.symbol);
        if(nosIDArray != null && nosIDArray.length > 0) {
            for(int i = 0; i < nosIDArray.length; i++) {
                openOEMS = orderMS.getNOS(nosIDArray[i]);
                if(openOEMS != null) {
                    perfData.tradeCount++;
                    if(recCount == 60) {
                        System.out.println("OPEN OEMS: " + openOEMS.openOrderId + " - " + perfData.tradeCount);
                    }
                }
            }
        }
    }

    private void getCoaPosition(PerfData perfData) {
        perfData.tradeCount = 0;
        coaIDArray = orderMS.getFromCOAIDArray(perfData.symbol);
        if (coaIDArray != null && coaIDArray.length > 0) {
            for (int i = 0; i < coaIDArray.length; i++) {
                closeOEMS = orderMS.getCOA(coaIDArray[i]);
                if(closeOEMS != null) {
                    perfData.tradeCount++;
                    if(recCount == 60) {
                        System.out.println("CLOSED OEMS: " + closeOEMS.closeOrderId + " - " + perfData.tradeCount);
                    }
                }
            }
        }
    }

    private void getNosCoaMatchTest(PerfData perfData) {
        nosIDArray = orderMS.getFromNOSIDArray(perfData.symbol);
        coaIDArray = orderMS.getFromCOAIDArray(perfData.symbol);

        if(nosIDArray != null && nosIDArray.length > 0) {
            //System.out.println("NOS: " + nosIDArray.length);
        }
        if(coaIDArray != null && coaIDArray.length > 0) {
            //System.out.println("COA: " + coaIDArray.length);
        }
    }

    private void getPerformance(PerfData perfData) {
        if(perfData.tradeCount > 0) {
            //System.out.println("TRADE COUNT: " + perfData.tradeCount);
        }
    }

    private void getRisk(PerfData perfData) {
        //
    }
}
