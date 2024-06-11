package core.util;

import core.service.publisher.PublisherData;
import core.util.wirejson.WireJSONImpl;

import java.io.IOException;

public class WireJSONTest {
    public static void main(String[] args) throws IOException {
        // Create an instance of PublisherData and populate it
        PublisherData publisherData = new PublisherData();
        publisherData.recId = 12345;
        publisherData.marketCap = 5000000000.0;
        publisherData.svcStartTs = System.currentTimeMillis();
        publisherData.symbol = "AAPL";
        publisherData.open = 150.0;
        // ... populate other fields ...

        // Write the DTO to JSON
        WireJSONImpl.writeToJSON(publisherData);
    }
}
