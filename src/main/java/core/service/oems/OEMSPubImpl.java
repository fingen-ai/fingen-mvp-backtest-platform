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

        // OMS-IN OPEN ORDER
        // place new order (single, market)

        // place new order (single, limit)

        // place new order (single, stop)

        // place new order (single, trailing stop)

        // OMS-IN CLOSE ORDER
        // place new order (single, market)

        // place new order (single, limit)

        // place new order (single, stop)

        // place new order (single, trailing stop)

        // OMS-OUT REPORTING
        // get all orders

        // get all trades

        // positions
        // get reports of all states: order, trade, position

        // get reports of all activity: order, trade, position

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}