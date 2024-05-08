package core.service.insight;

import account.AccountData;
import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import performance.Performance;
import performance.PerformanceImpl;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    OrderMappingService oms = new OrderMappingService();
    Performance performance = new PerformanceImpl();
    AccountData accountData = new AccountData();
    ATR atr50 = new ATRImpl(50);
    Risk risk = new RiskImpl();
    int[] nosIDArray = null;
    OEMSData oemsData = new OEMSData();

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {this.output = output;}

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        if(!insightData.bassoOrderIdea.equals("Neutral")) {

            nosIDArray = getPositions(insightData);
            if (nosIDArray != null) {

                // Have current position
                for (int i = 0; i < nosIDArray.length; i++) {
                    System.out.println("nosIDArray[" + i + "]=" + nosIDArray[i]);
                    // update current NOS SL/TP instructions
                    // build NOS instructions
                }

            } else {

                // Do not have current position
                System.out.println("nosIDArray is null");
                // build NOS instructions
            }
        }

        insightData.svcStopTs = System.nanoTime();
        insightData.svcLatency = insightData.svcStopTs - insightData.svcStartTs;
        System.out.println("INSIGHT: " + insightData);
        output.simpleCall(insightData);
    }

    private void getSide(InsightData insightData) {
        if(insightData.bassoOrderIdea.equals("Bullish")) {
            insightData.orderSide = "Buy";
        }
        if(insightData.bassoOrderIdea.equals("Bearish")) {
            insightData.orderSide = "Sell";
        }
        if(insightData.bassoOrderIdea.equals("Neutral")) {
            insightData.orderSide = "Hold";
        }
    }

    private int[] getPositions(InsightData insightData) {
        return oms.getPositions(insightData.symbol);
    }

    private void closePositions(InsightData insightData) {
        oms.closePosition(insightData.symbol);
    }

    private void addOrder(OEMSData oemsData) {
        int[] newOrderIds = {oemsData.openOrderId}; // Assuming insightData contains an orderId
        oms.addOrder(oemsData.symbol, newOrderIds);
    }

    private void updateOrder(InsightData insightData) {
        OEMSData updateNOS = new OEMSData();
        updateNOS.orderQty = insightData.orderQty;
        updateNOS.close = insightData.close;
        updateNOS.orderType = insightData.orderType;
        oms.updateOrder(updateNOS.openOrderId, updateNOS);
    }
}
