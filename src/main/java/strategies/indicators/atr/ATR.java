package strategies.indicators.atr;

import core.service.insight.InsightData;

public interface ATR {

    double calculateATR(InsightData data, int period);
}
