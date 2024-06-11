package core.service.oems;

import java.io.IOException;

public interface OEMSPub {
    void simpleCall(pubData data) throws IOException;
}
