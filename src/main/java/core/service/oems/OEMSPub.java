package core.service.oems;

import java.io.IOException;

public interface OEMSPub {
    void simpleCall(OEMSData data) throws IOException;
}
