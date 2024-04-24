package core.service.base;

public class ServiceImpl implements Service, ServiceHandler<Service> {

    private Service output;

    public ServiceImpl() {

    }

    @Override
    public void init(Service output) {
        this.output = output;
    }

    @Override
    public void simpleCall(SimpleData data) {
        data.number *= 10;
        long time = System.nanoTime();
        data.ts = time; // the start time for the next stage.
        output.simpleCall(data); // pass the data to the next stage.
    }
}
