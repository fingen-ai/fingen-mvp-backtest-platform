package core.service.performance;

import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    OrderMappingService orderMS = new OrderMappingService();

    OEMSData closedOEMS = new OEMSData();
    OEMSData openOEMS = new OEMSData();

    long[] coaIDArray = new long[0];
    long[] nosIDArray = new long[0];

    Performance perf = new PerformanceImpl();

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();

        getPerformance(perfData);

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        output.simpleCall(perfData);
    }

    private void getPerformance(PerfData perfData) {

        coaIDArray = orderMS.getFromCOAIDArray(perfData.symbol);
        if(coaIDArray != null && coaIDArray.length > 0) {

            for (int i = 0; i < coaIDArray.length; i++) {
                closedOEMS = orderMS.getCOA(coaIDArray[i]);
                System.out.println("CLOSED OEMS: " + closedOEMS.closeOrderId);
            }
        }

        nosIDArray = orderMS.getFromNOSIDArray(perfData.symbol);
        if(nosIDArray != null && nosIDArray.length > 0) {

            //System.out.println("OPEN ARRAY: " + nosIDArray.length);
            for(int i = 0; i < nosIDArray.length; i++) {
                openOEMS = orderMS.getNOS(nosIDArray[i]);
                if(openOEMS != null) {
                    System.out.println("OPEN OEMS: " + openOEMS.openOrderId);
                }
            }
        }

        System.out.println("\n");
    }
}
