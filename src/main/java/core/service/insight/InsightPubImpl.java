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

        double riskPercentage = getRiskPercentage(insightData);
        double volPercentage = getVolPercentage(insightData);

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }

    private double getRiskPercentage(InsightData insightData) {
        double riskPercentage = 0;
        return riskPercentage;
    }

    private double getVolPercentage(InsightData insightData) {
        double volPercentage = 0;
        return volPercentage;
    }
}
