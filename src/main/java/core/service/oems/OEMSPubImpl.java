package core.service.oems;

import account.AccountData;
import oems.OrderBuilderImpl;
import oems.OMSImpl;
import oems.api.OMSIn;
import oems.api.OrderBuilder;
import oems.dto.*;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private NewOrderSingle nos = new NewOrderSingle();
    private OrderBuilder ob = new OrderBuilderImpl();
    private OMSIn om = new OMSImpl();
    private ExecutionReport er = new ExecutionReport();
    private Risk risk = new RiskImpl();
    private AccountData acct = new AccountData();

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
    }
    public void init(OEMSPub output) {
        this.output = output;
    }

    public void simpleCall(OEMSData oemsData) throws IOException {
        oemsData.svcStartTs = System.nanoTime();

        if(oemsData.bassoOrderIdea != null) {

            //Get curr pos direction
            //If(currPosDir != bassoOrderIdea) {
            //Close all curr pos in symbol
            //} else {
            //Update stop loss and take profit exits
            //}

            //Get curr nav

            //If(ExistingPositions == Yes)

                //Get curr pos amt
                //If(currPos <= tradeAmtInstruction) {
                    oemsData.tradeAmtPerRiskInstruction = risk.getOngoingRiskPercentThreshold() * acct.nav;
                    oemsData.tradeAmtPerVolInstruction = risk.getOngoingVolPercentThreshold() * acct.nav;
                //} else {
                    // No trade due to pos lmt
                //}
            //} else {

                oemsData.tradeAmtPerRiskInstruction = risk.getInitRiskPercentThreshold() * acct.nav;
                oemsData.tradeAmtPerVolInstruction = risk.getInitVolPercentThreshold() * acct.nav;
            //}
        }

        nos = ob.buildNOS(oemsData);
        om.newOrderSingle(nos);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}