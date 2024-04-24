package service.publisher;

public interface PublisherHandler<O> {
    void init(O output);
}
