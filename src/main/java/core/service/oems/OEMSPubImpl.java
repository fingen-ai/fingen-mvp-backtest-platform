package core.service.oems;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private OEMSData oemsDataALL = new OEMSData();

    int counter = 0;

    private OEMSPub output;

    public OEMSPubImpl() {
    }
    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) {
        oemsData.svcStartTs = System.nanoTime();
        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        //System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}