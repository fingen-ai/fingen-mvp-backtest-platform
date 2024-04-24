package core.service.base;
import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;

import java.util.UUID;

public class Warmup {

    private Service serviceIn;
    private ServiceWrapper<ServiceImpl> service2, service3, serviceOut;
    private SimpleData data = new SimpleData();
    private String queueIn, queue2, queue3, queueOut;

    public Warmup() {
    }

    public void start() {
        init();
        run();
        complete();
    }

    public void init() {
        UUID uuid = UUID.randomUUID();
        queueIn = OS.TMP + "/MainOrchestrator/" + uuid + "/pathIn";
        queue2 = OS.TMP + "/MainOrchestrator/" + uuid + "/stage2";
        queue3 = OS.TMP + "/MainOrchestrator/" + uuid + "/stage3";
        queueOut = OS.TMP + "/MainOrchestrator/" + uuid + "/pathOut";

        // Initialize the service pipeline
        serviceIn = SingleChronicleQueueBuilder.binary(queueIn).build().acquireAppender().methodWriter(Service.class);
        service2 = new ServiceWrapper<>(queueIn, queue2, new ServiceImpl());
        service3 = new ServiceWrapper<>(queue2, queue3, new ServiceImpl());
        serviceOut = new ServiceWrapper<>(queue3, queueOut, new ServiceImpl());
    }

    public void run() {
        // Prepare and send a data item through the pipeline

        int i = 0;
        while(i<1000) {
            data.text = "Hello";
            System.out.println("Warmup " + i + ": " + data);
            serviceIn.simpleCall(data);
            i++;
        }
    }

    public void complete() {
        // Cleanup queues
        IOTools.deleteDirWithFiles(queueIn, 2);
        IOTools.deleteDirWithFiles(queue2, 2);
        IOTools.deleteDirWithFiles(queue3, 2);
        IOTools.deleteDirWithFiles(queueOut, 2);
    }

    public static void main(String[] args) {
        Warmup warmup = new Warmup();
        warmup.start();
    }
}
