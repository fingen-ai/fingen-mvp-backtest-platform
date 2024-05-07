package core.service.insight;

import java.io.IOException;

public interface InsightPub {
    void simpleCall(InsightData data) throws IOException;
}
