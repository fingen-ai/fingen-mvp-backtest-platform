package strategies.indicators.emax;

public interface EMA {

    double getEma();
    double update(double price);
}
