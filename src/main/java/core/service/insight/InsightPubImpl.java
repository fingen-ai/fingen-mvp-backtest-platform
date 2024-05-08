package core.service.insight;

import account.AccountData;
import core.service.oems.OEMSData;
import oems.map.OrderMappingService;
import org.apache.commons.lang3.ArrayUtils;
import performance.Performance;
import performance.PerformanceImpl;
import risk.Risk;
import risk.RiskImpl;
import strategies.indicators.atr.ATR;
import strategies.indicators.atr.ATRImpl;
import util.AppendArray;

import java.io.IOException;

public class InsightPubImpl implements InsightPub, InsightHandler<InsightPub> {

    OrderMappingService oms = new OrderMappingService();
    Performance performance = new PerformanceImpl();
    AccountData accountData = new AccountData();
    ATR atr50 = new ATRImpl(50);
    Risk risk = new RiskImpl();
    long[] nosIDArray = new long[0];
    OEMSData oemsData = new OEMSData();

    private InsightPub output;

    public InsightPubImpl() throws IOException {
    }

    public void init(InsightPub output) {this.output = output;}

    public void simpleCall(InsightData insightData) throws IOException {
        insightData.svcStartTs = System.nanoTime();

        if(!insightData.bassoOrderIdea.equals("Neutral")) {
            nosIDArray = oms.getFromNOSIDArray(insightData.symbol);
            if (nosIDArray != null) {

                // Have current position
                for (int i = 0; i < nosIDArray.length; i++) {
                    System.out.println("nosIDArray[" + i + "]=" + nosIDArray[i]);
                    // build UPDT-NOS SL/TP instructions
                    // send UPDT-NOS instructions
                }

            } else {

                // Do not have current position
                System.out.println("nosIDArray is null");
                // build CURR-NOS instructions
            }
        }
        
        oemsData.openOrderId = System.nanoTime();
        oemsData.symbol = insightData.symbol;
        nosIDArray = ArrayUtils.add(nosIDArray, oemsData.openOrderId);

        System.out.println("NOS ID: " + oemsData.openOrderId);
        System.out.println("NOS Symbol: " + oemsData.symbol);
        System.out.println("NOS ID ARRAY: " + nosIDArray.length);

        oms.addToNOSIDArray(oemsData.symbol, nosIDArray);

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
}
