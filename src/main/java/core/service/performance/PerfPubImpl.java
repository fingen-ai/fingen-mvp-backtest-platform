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

    Performance perfKPI = new PerformanceImpl();

    private PerfPub output;

    public PerfPubImpl() throws IOException {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();


        getOpenPosition(perfData);
        getOpenPositionConfirmation(perfData);
        getClosedPosition(perfData);
        getClosePositionConfirmation(perfData);
        getRisk(perfData);
        getPerformance(perfData);

        System.out.println("\n");

        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;

        nosIDArray = null;
        coaIDArray = null;
        openOEMS = null;
        closeOEMS = null;

        output.simpleCall(perfData);
    }

    private void getOpenPosition(PerfData perfData) {
        perfData.tradeCount = 0;
        nosIDArray = orderMS.getFromNOSIDArray(perfData.symbol);
        if(nosIDArray != null && nosIDArray.length > 0) {
            for(int i = 0; i < nosIDArray.length; i++) {
                openOEMS = orderMS.getNOS(nosIDArray[i]);
                if(openOEMS != null) {
                    perfData.tradeCount++;
                    //System.out.println("OPEN OEMS: " + openOEMS.openOrderId + " - " + perfData.tradeCount);
                }
            }
        }
    }

    private void getOpenPositionConfirmation(PerfData perfData) {
        perfData.tradeCount = 0;
        long prevOpenId = 0;
        if(nosIDArray != null && nosIDArray.length > 0) {
            for(int i = 0; i < nosIDArray.length; i++) {

                //System.out.println("ARRAy LENGTh: " + nosIDArray.length + " =---- " + nosIDArray[i] + " === " + prevOpenId);
                // are any open id in coa id array map?
                closeOEMS = orderMS.getCOA(nosIDArray[i]);
                if(closeOEMS != null) {
                    perfData.tradeCount++;
                    //System.out.println("NOS In COA Map Check - Failure Confirmed" + closeOEMS.openOrderId + " - " + perfData.tradeCount);
                }

                // does nos id array map contain redundant open id?
                openOEMS = orderMS.getNOS(nosIDArray[i]);
                if(openOEMS != null) {
                    if(openOEMS.openOrderId == prevOpenId && prevOpenId > 0) {
                        //System.out.println("NOS Redundancy Check - Failure Confirmed" + openOEMS.openOrderId + " - " + perfData.tradeCount);
                    }
                    prevOpenId = openOEMS.openOrderId;
                }
            }
        }
    }

    private void getClosedPosition(PerfData perfData) {
        perfData.tradeCount = 0;
        coaIDArray = orderMS.getFromCOAIDArray(perfData.symbol);
        if (coaIDArray != null && coaIDArray.length > 0) {
            for (int i = 0; i < coaIDArray.length; i++) {
                closeOEMS = orderMS.getCOA(coaIDArray[i]);
                if(closeOEMS != null) {
                    perfData.tradeCount++;
                    //System.out.println("CLOSED OEMS: " + closeOEMS.closeOrderId + " - " + perfData.tradeCount);
                }
            }
        }
    }

    private void getClosePositionConfirmation(PerfData perfData) {
        perfData.tradeCount = 0;
        long prevCloseId = 0;
        if(coaIDArray != null && coaIDArray.length > 0) {
            for(int i = 0; i < coaIDArray.length; i++) {

                // do any close id remain in nos id array map?
                openOEMS = orderMS.getNOS(coaIDArray[i]);
                if(openOEMS != null) {
                    perfData.tradeCount++;
                    //System.out.println("COA In NOS Map Check - Failure Confirmed" + openOEMS.closeOrderId + " - " + perfData.tradeCount);
                }

                // does coa id array map contain redundant close id?
                closeOEMS = orderMS.getCOA(coaIDArray[i]);
                if(closeOEMS != null) {
                    if(closeOEMS.closeOrderId == prevCloseId && prevCloseId > 0) {
                        //System.out.println("COA Redundancy Check - Failure Confirmed" + closeOEMS.closeOrderId + " - " + perfData.tradeCount);
                    }
                    prevCloseId = closeOEMS.closeOrderId;
                }
            }
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
