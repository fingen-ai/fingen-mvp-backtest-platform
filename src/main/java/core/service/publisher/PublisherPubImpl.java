package core.service.publisher;


public class PublisherPubImpl implements PublisherPub, PublisherHandler<PublisherPub> {

    private PublisherData publisherDataALL = new PublisherData();

    int counter = 0;

    private PublisherPub output;

    public PublisherPubImpl() {
    }
    public void init(PublisherPub output) {
        this.output = output;
    }

    public void simpleCall(PublisherData publisherData) {
        publisherData.svcStartTs = System.nanoTime();
        publisherData.svcStopTs = System.nanoTime();
        publisherData.svcLatency = publisherData.svcStopTs - publisherData.svcStartTs;

        System.out.println("PUBLISHER: " + publisherData);
        System.out.println("\n");

        output.simpleCall(publisherData);
    }
}
