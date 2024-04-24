package strategies.statarb.options;

import org.apache.commons.math3.distribution.NormalDistribution;

public class BlackScholesModel {

    public static double blackScholesCallPrice(double S, double K, double T, double r, double sigma) {
        double d1 = (Math.log(S / K) + (r + 0.5 * Math.pow(sigma, 2)) * T) / (sigma * Math.sqrt(T));
        double d2 = d1 - sigma * Math.sqrt(T);

        NormalDistribution norm = new NormalDistribution();
        double callPrice = S * norm.cumulativeProbability(d1) - K * Math.exp(-r * T) * norm.cumulativeProbability(d2);
        return callPrice;
    }
}
