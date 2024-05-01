package core.service.insight;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    private InsightData insightDataALL = new InsightData();

    int counter = 0;

    private InsightPub output;

    public InsightPubImpl() {
    }
    public void init(InsightPub output) {
        this.output = output;
    }

    public void simpleCall(InsightData insightData) {
        insightData.svcStartTs = System.nanoTime();
        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        //System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }
}
