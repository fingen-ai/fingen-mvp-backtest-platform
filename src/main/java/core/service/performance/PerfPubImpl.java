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
    long openRecCount = 0;
    long closeRecCount = 0;
    long oemsRecCount = 0;

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();

        oemsRecCount++;

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

        openRecCount = 0;
        closeRecCount = 0;

        output.simpleCall(perfData);
    }

    private void getNosPosition(PerfData perfData) {
        perfData.tradeCount = 0;
        nosIDArray = orderMS.getFromNOSIDArray(perfData.symbol);
        if(nosIDArray != null) {

            for(int i = 0; i < nosIDArray.length; i++) {

                openOEMS = orderMS.getNOS(nosIDArray[i]);
                if(openOEMS != null) {

                    openRecCount++;
                    if(oemsRecCount <=407) {
                        System.out.println("OPEN OEMS: " + nosIDArray[i] + " - " + openRecCount);
                    }
                }
            }
        }
    }

    private void getCoaPosition(PerfData perfData) {
        perfData.tradeCount = 0;
        coaIDArray = orderMS.getFromCOAIDArray(perfData.symbol);
        if (coaIDArray != null) {

            for (int i = 0; i < coaIDArray.length; i++) {

                closeOEMS = orderMS.getCOA(coaIDArray[i]);
                if(closeOEMS != null) {

                    closeRecCount++;
                    if(oemsRecCount <= 407) {
                        System.out.println("CLOSED OEMS: " + coaIDArray[i] + " - " + closeRecCount);
                    }
                }
            }
        }
    }

    private void getPerformance(PerfData perfData) {
        //
    }

    private void getRisk(PerfData perfData) {
        //
    }
}
