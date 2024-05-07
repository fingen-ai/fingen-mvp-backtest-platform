package core.service.oems;

import oems.map.MapManager;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    MapManager mm = new MapManager(CharSequence.class, OEMSData.class, 1000);
    double[] nosArray = null;
    double[] coaArray = null;

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        oemsData.openOrderId = String.valueOf(System.nanoTime());

        if(oemsData.nosOrderInsight.equals("NOS")) {
            mm.add(oemsData.openOrderId, oemsData);
        }

        if(oemsData.updOrderInsight.equals("UPD")) {
            mm.update(oemsData.openOrderId, oemsData);
            mm.add(oemsData.openOrderId, oemsData);
        }

        if(oemsData.coaOrderInsight.equals("COA")) {
            mm.delete(oemsData.openOrderId);
            mm.add(oemsData.openOrderId, oemsData);
        }

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}