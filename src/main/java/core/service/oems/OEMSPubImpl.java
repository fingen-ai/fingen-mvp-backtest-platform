package core.service.oems;

import oems.map.MapManager;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    MapManager mm = new MapManager(CharSequence.class, OEMSData.class, 1000);

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        // build order
        // map order
        oemsData.openOrderId = String.valueOf(System.nanoTime());
        mm.add(oemsData.openOrderId, oemsData);
        mm.update(oemsData.openOrderId, oemsData);
        mm.delete(oemsData.openOrderId);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}