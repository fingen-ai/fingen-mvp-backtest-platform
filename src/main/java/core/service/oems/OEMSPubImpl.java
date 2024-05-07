package core.service.oems;

import oems.dto.CloseOrderAll;
import oems.dto.NewOrderSingle;
import oems.map.MapManager;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    NewOrderSingle nos = new NewOrderSingle();
    CloseOrderAll coa = new CloseOrderAll();
    MapManager mm = new MapManager(CharSequence.class, OEMSData.class, 1000);

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        addNOS(oemsData);
        updateNOS(oemsData);
        closeNOS(oemsData);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }

    private void addNOS(OEMSData oemsData) {
        mm.add(oemsData.symbol, oemsData);
        System.out.println("OEMS ADD: " + oemsData);
    }

    private void updateNOS(OEMSData oemsData) {
        System.out.println("OEMS UPDT: " + oemsData);
    }

    private void closeNOS(OEMSData oemsData) {
        System.out.println("OEMS CLOSE: " + oemsData);
    }
}