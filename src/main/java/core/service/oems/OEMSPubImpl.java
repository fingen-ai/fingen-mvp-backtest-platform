package core.service.oems;

import oems.api.OMSIn;
import oems.api.OMSOut;
import oems.dto.ExecutionReport;
import oems.dto.NewOrderSingle;
import oems.dto.OrderType;

import java.io.IOException;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private NewOrderSingle nos = new NewOrderSingle();
    OMSIn omsIn;
    OMSOut omsOut;

    private OEMSPub output;

    public OEMSPubImpl() throws IOException {
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
                // Build open long order + details
                nos.symbol(1); // use long instead of String for symbol. lowers latency.
                nos.ordType(OrderType.limit);
                nos.price(oemsData.bid);
                omsIn.newOrderSingle(nos);

            } else if(oemsData.bassoOrderIdea.equals("Bearish")) {
                // Build open short order + details
                nos.symbol(1); // just have a long to symbol identification map
                nos.ordType(OrderType.limit);
                nos.price(oemsData.ask);
                omsIn.newOrderSingle(nos);
            }
        }

        // Get all open trades

            // Recalc stop
            // Yes hit stop
                // Place close trade
                // Confirmed closed trade
            // Update stop


        // OMS-OUT REPORTING
        ExecutionReport er = null;
        er.orderID(Long.parseLong(nos.clOrdID()));
        er.clOrdID(nos.clOrdID());
        er.ordType(OrderType.valueOf(nos.clOrdID()));
        omsOut.executionReport(er);

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}