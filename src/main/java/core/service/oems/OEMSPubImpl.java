package core.service.oems;

import oems.OMSImpl;
import oems.api.OMSIn;
import oems.api.OMSOut;
import oems.dto.NewOrderSingle;
import oems.dto.OrderType;

public class OEMSPubImpl implements OEMSPub, OEMSHandler<OEMSPub> {

    private OEMSData oemsDataALL = new OEMSData();
    private NewOrderSingle nos = new NewOrderSingle();
    OMSIn omsIn;
    OMSOut omsOut;

    int counter = 0;

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
            // Recalc stops
            // Yes hit stop
                // Place close order
            // Update stop


        // OMS-OUT REPORTING
        // get all orders

        // get all trades

        // positions
        // get reports of all states: order, trade, position

        // get reports of all activity: order, trade, position

        oemsData.svcStopTs = System.nanoTime();
        oemsData.svcLatency = oemsData.svcStopTs - oemsData.svcStartTs;
        System.out.println("OEMS: " + oemsData);
        output.simpleCall(oemsData);
    }
}