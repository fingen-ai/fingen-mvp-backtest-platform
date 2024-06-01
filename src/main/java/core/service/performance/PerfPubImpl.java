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

        System.out.println("\n");

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        nosIDArray = null;
        coaIDArray = null;
        openOEMS = null;
        closeOEMS = null;

        //openRecCount = 0;
        //closeRecCount = 0;

        output.simpleCall(perfData);
    }

    private void getNosPosition(PerfData perfData) {
        nosIDArray = orderMS.getFromNOSIDArray(perfData.symbol);
        if(nosIDArray != null) {

            perfData.nosRecCount = nosIDArray.length;

            for(int i = 0; i < nosIDArray.length; i++) {
                openOEMS = orderMS.getNOS(nosIDArray[i]);
                if(openOEMS != null) {
                    if(oemsRecCount <=407) {
                        //System.out.println("OPEN OEMS: " + nosIDArray[i] + " - " + openRecCount);
                    }
                }
            }
        }
    }

    private void getCoaPosition(PerfData perfData) {
        coaIDArray = orderMS.getFromCOAIDArray(perfData.symbol);
        if (coaIDArray != null) {

            perfData.coaRecCount = coaIDArray.length;

            for (int i = 0; i < coaIDArray.length; i++) {
                closeOEMS = orderMS.getCOA(coaIDArray[i]);
                if(closeOEMS != null) {
                    if(oemsRecCount <= 407) {
                        //System.out.println("CLOSED OEMS: " + coaIDArray[i] + " - " + closeRecCount);
                    }
                }
            }
        }
    }

    private void getPerformance(PerfData perfData) {
        perfData.allRecCount = perfData.nosRecCount + perfData.coaRecCount;
        System.out.println("NOS Count: " + perfData.nosRecCount);
        System.out.println("COA Count: " + perfData.coaRecCount);
        System.out.println("ALL Count: " + perfData.allRecCount);
    }

    private void getRisk(PerfData perfData) {
        //
    }
}
