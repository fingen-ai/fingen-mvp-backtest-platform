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
    private CloseOrderAll coa = new CloseOrderAll();
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

        System.out.println("Basso: " + oemsData.bassoOrderIdea);

        if(oemsData.bassoOrderIdea != null) {

            //Get curr nav
            //Get curr pos direction

            //If(ExistingPos == Yes) {

                //If(currPosDir != bassoOrderIdea) {
                    // add add'l oemsdata for order builder
                    //ob.buildCOA(oemsData);
                    //om.closeOrderAll(coa);
                    // Given nos order closed, delete tradeID from nosArray
                    // Given nos order closed, add new tradeID to coaArray

                //} else {
                    // Get new SL and TP prices
                    //ob.buildNOS(oemsData);
                    //om.updateSLTP(nos);

                //Get curr pos amt
                //If(currPos <= tradeAmtInstruction) {
                    //oemsData.tradeQtyPerRiskInstruction = risk.getOngoingRiskPercentThreshold() * acct.nav;
                    //oemsData.tradeQtyPerVolInstruction = risk.getOngoingVolPercentThreshold() * acct.nav;
                    // add add'l oemsdata for order builder
                //}

            //} else {

                //oemsData.tradeQtyPerRiskInstruction = risk.getInitRiskPercentThreshold() * acct.nav;
                //oemsData.tradeQtyPerVolInstruction = risk.getInitVolPercentThreshold() * acct.nav;
                // add add'l oemsdata for order builder
            //}
        }

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}