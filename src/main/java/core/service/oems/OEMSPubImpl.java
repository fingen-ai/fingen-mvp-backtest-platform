package core.service.oems;

import oems.BRM;
import oems.OMSImpl;
import oems.api.OMSIn;
import oems.api.OMSOut;
import oems.dto.*;

import java.io.IOException;
import java.util.List;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private NewOrderSingle nos = new NewOrderSingle();
    OMSIn omsIn;
    OMSOut omsOut;
    ExecutionReport er = new ExecutionReport();

    private OEMSPub output;

    public OEMSPubImpl() {
    }
    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) {
        oemsData.svcStartTs = System.nanoTime();

        // New signal response
        if(oemsData.bassoOrderIdea != null) {

            // Curr pos in the symbol
                // Get orders map. Keyed by openOrderId. Returns OrdersMap
                // Risk % check
                // Vol % check
                // Add to  position or not

            // No curr pos in the symbol
            if(oemsData.bassoOrderIdea.equals("Bullish")) {
                omsIn.newOrderSingle(nos);
            } else if(oemsData.bassoOrderIdea.equals("Bearish")) {
                omsIn.newOrderSingle(nos);
            }
        }

        // Trade and Order history
        //omsOut.executionReport(er);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}