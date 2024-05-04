package core.service.oems;

import account.AccountData;
import oems.BRM;
import oems.OrderBuilderImpl;
import oems.OrderManagerImpl;
import oems.api.OrderBuilder;
import oems.api.OrderManager;
import oems.dto.*;
import risk.Risk;
import risk.RiskImpl;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private NewOrderSingle nos = new NewOrderSingle();
    private OrderBuilder ob = new OrderBuilderImpl();
    private OrderManager om = new OrderManagerImpl();
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

        if(oemsData.bassoOrderIdea != null) {
            // openOrders = brm.getOpenOrders(order.orderId);
            // if(openOrders) {
            oemsData.tradeAmtPerRiskInstruction = risk.getOngoingRiskPercentThreshold() * acct.nav;
            oemsData.tradeAmtPerVolInstruction = risk.getOngoingVolPercentThreshold() * acct.nav;
            // } else {
            oemsData.tradeAmtPerRiskInstruction = risk.getInitRiskPercentThreshold() * acct.nav;
            oemsData.tradeAmtPerVolInstruction = risk.getInitVolPercentThreshold() * acct.nav;
        }

        nos = ob.buildNOS(oemsData);
        om.newOrderSingle(nos);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}