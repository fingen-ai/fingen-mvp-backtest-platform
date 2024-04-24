package indicators.emax;

public class EMACrossover {

    private final TenDayEMA tenDayEMA;
    private final FiftyDayEMA fiftyDayEMA;

    public EMACrossover(TenDayEMA tenDayEMA, FiftyDayEMA fiftyDayEMA) {
        this.tenDayEMA = tenDayEMA;
        this.fiftyDayEMA = fiftyDayEMA;
    }

    public String checkForCrossover(double[] tenDayPrices, double[] fiftyDayPrices) {
        double lastTenDayEMA = calculateLastEMA(tenDayPrices, tenDayEMA);
        double lastFiftyDayEMA = calculateLastEMA(fiftyDayPrices, fiftyDayEMA);

        if (lastTenDayEMA > lastFiftyDayEMA) {
            return "open-buy";
        } else if (lastTenDayEMA < lastFiftyDayEMA) {
            return "open-sell";
        } else {
            return "no-action";
        }
    }

    private double calculateLastEMA(double[] prices, TenDayEMA ema) {
        double emaValue = 0;
        for (double price : prices) {
            emaValue = ema.calculateEMA(emaValue, price);
        }
        return emaValue;
    }

    private double calculateLastEMA(double[] prices, FiftyDayEMA ema) {
        double emaValue = 0;
        for (double price : prices) {
            emaValue = ema.calculateEMA(emaValue, price);
        }
        return emaValue;
    }
}
