package core.service.performance;

import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import performance.Performance;
import performance.PerformanceImpl;

import java.io.IOException;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    OrderMappingService orderMS = new OrderMappingService();
    Performance perf = new PerformanceImpl();

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();

        //getPerformance(perfData);

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        output.simpleCall(perfData);
    }

    private void getPerformance(PerfData perfData) {
        //OEMSData closedOEMS = orderMS.getCOS(perfData.openOrderId);
        //System.out.println("CLOSED OEMS: " + closedOEMS);
        //System.out.println("\n");
    }
}
