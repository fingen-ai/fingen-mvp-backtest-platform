package core.service.price;

public class PricePubImpl implements PricePub, PriceHandler<PricePub> {

    private PricePub output;

    public void init(PricePub output) {
        this.output = output;
    }

    public void simpleCall(PriceData priceData) {
        priceData.svcStartTs = System.nanoTime();
        priceData.svcStopTs = System.nanoTime();
        priceData.svcLatency = priceData.svcStopTs - priceData.svcStartTs;
        //System.out.println("PRICE: " + priceData);
        output.simpleCall(priceData);
    }
}
