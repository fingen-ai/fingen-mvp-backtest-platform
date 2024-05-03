package strategies.indicators.atr;

public interface ATR {

    double calculateTR(double high, double low, double previousClose);
    double update(double high, double low, double close, double previousClose);
}
