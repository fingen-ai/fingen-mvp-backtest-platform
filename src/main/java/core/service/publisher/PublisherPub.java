package core.service.publisher;

import java.io.IOException;

public interface PublisherPub {
    void simpleCall(PublisherData data) throws IOException;
}
