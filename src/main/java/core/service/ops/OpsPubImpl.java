package core.service.ops;

public class OpsPubImpl implements OpsPub, OpsHandler<OpsPub> {

    private OpsData opsDataALL = new OpsData();

    int counter = 0;

    private OpsPub output;

    public OpsPubImpl() {
    }
    public void init(OpsPub output) {
        this.output = output;
    }

    public void simpleCall(OpsData opsData) {
        opsData.svcStartTs = System.nanoTime();
        opsData.svcStopTs = System.nanoTime();
        opsData.svcLatency = opsData.svcStopTs - opsData.svcStartTs;
        System.out.println("OPS: " + opsData);
        output.simpleCall(opsData);
    }
}

