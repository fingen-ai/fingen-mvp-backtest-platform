package core.service.performance;

public class PerfPubImpl implements PerfPub, PerfHandler<PerfPub> {

    private PerfPub output;

    public PerfPubImpl() {
    }

    public void init(PerfPub output) {
        this.output = output;
    }

    public void simpleCall(PerfData perfData) {
        perfData.svcStartTs = System.nanoTime();
        perfData.svcStopTs = System.nanoTime();
        perfData.svcLatency = perfData.svcStopTs - perfData.svcStartTs;
        System.out.println("PERF: " + perfData);
        output.simpleCall(perfData);
    }
}
