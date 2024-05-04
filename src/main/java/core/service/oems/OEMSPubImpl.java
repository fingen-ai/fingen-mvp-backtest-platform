package core.service.oems;

import account.AccountData;
import oems.BRM;
import oems.OrderBuilder;
import oems.api.OMSIn;
import oems.api.OMSOut;
import oems.dto.*;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private NewOrderSingle nos = new NewOrderSingle();
    private OrderBuilder ob = new OrderBuilder();
    private OMSIn omsIn = null;
    private OMSOut omsOut = null;
    private ExecutionReport er = new ExecutionReport();
    private BRM brm = new BRM();
    private Risk risk = new RiskImpl();
    private AccountData acct = new AccountData();

    private OEMSPub output;

    public OEMSPubImpl() {
    }
    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        // New signal response
        if(oemsData.bassoOrderIdea != null) {
            // openOrders = brm.getOpenOrders(order.orderId);
            // if(openOrders) {

                oemsData.tradeAmtPerRiskInstruction = risk.getOngoingRiskPercentThreshold() * acct.nav;
                oemsData.tradeAmtPerVolInstruction = risk.getOngoingVolPercentThreshold() * acct.nav;

                oemsData.tradeAmtPerRiskInstruction = risk.getInitRiskPercentThreshold() * acct.nav;
                oemsData.tradeAmtPerVolInstruction = risk.getInitVolPercentThreshold() * acct.nav;
                nos = ob.buildNOS(oemsData);
                omsIn.newOrderSingle(nos);

            // } else {
            //
            // Get init risk amount by Risk %
            // Get init risk amount by Vol %
            // omsIn.newOrderSingle(nos);
            // }
        }

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}