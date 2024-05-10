package core.service.oems;

import oems.map.InsightMappingService;
import oems.map.OrderMappingService;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    OrderMappingService orderMS = new OrderMappingService();

    long[] arrayOpenOrderID = new long[0];

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }

    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        if(oemsData.bassoOrderIdea.equals("Bullish")) {
            System.out.println("OEMS - BULLS: " + oemsData.bassoOrderIdea);
        } else {
            System.out.println("OEMS - BEARS: " + oemsData.bassoOrderIdea);
        }

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}