package core.service.publisher;

public interface PublisherHandler<O> {
    void init(O output);
}
