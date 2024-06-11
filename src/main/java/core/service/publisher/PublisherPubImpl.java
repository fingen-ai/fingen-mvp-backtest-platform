package core.service.publisher;

import core.util.wirejson.*;
import publish.LandingPageData;

import java.io.IOException;

public class PublisherPubImpl implements PublisherPub, PublisherHandler<PublisherPub> {

    WireJSON wireJSON = new WireJSONImpl();

    LandingPageData landingPageData = new LandingPageData();

    private PublisherPub output;

    public PublisherPubImpl() {
    }
    public void init(PublisherPub output) {
        this.output = output;
    }

    public void simpleCall(PublisherData pubData) throws IOException {
        pubData.svcStartTs = System.nanoTime();

        landingPageData.avgLossAmt = pubData.avgLossAmt;
        landingPageData.avgWinAmt = pubData.avgWinAmt;
        landingPageData.avgLossPercent = pubData.avgLossPercent;
        landingPageData.avgWinPercent = pubData.avgWinPercent;
        landingPageData.edge = pubData.edge;

        wireJSON.wireLandingPageJSON(landingPageData);

        pubData.svcStopTs = System.nanoTime();
        pubData.svcLatency = pubData.svcStopTs - pubData.svcStartTs;

        //System.out.println("PUBLISHER: " + pubData);
        System.out.println("\n");

        output.simpleCall(pubData);
    }
}
