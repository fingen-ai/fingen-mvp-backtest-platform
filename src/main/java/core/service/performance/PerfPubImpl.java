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
    long oemsRecCount = 0;

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
        getRisk(perfData);
        getPerformance(perfData);

        oemsRecCount++;

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        System.out.println("\n");

        nosIDArray = null;
        coaIDArray = null;
        openOEMS = null;
        closeOEMS = null;

        output.simpleCall(perfData);
    }

    private void getNosPosition(PerfData perfData) {

    }

    private void getCoaPosition(PerfData perfData) {

    }

    private void getPerformance(PerfData perfData) {

    }

    private void getRisk(PerfData perfData) {

    }
}
