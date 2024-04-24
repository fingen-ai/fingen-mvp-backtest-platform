package service.strategy;

import net.openhft.affinity.AffinityLock;
import net.openhft.chronicle.bytes.MethodReader;
import net.openhft.chronicle.core.io.Closeable;
import net.openhft.chronicle.queue.ChronicleQueue;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import net.openhft.chronicle.threads.LongPauser;
import net.openhft.chronicle.threads.Pauser;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class StrategyWrapper<I extends StrategyHandler> implements Runnable, Closeable {
    private final ChronicleQueue inputQueue, outputQueue;
    private final MethodReader serviceIn;
    private final Object serviceOut;
    private final Thread thread;
    private final Pauser pauser = new LongPauser(1, 100, 500, 10_000, TimeUnit.MICROSECONDS);

    private volatile boolean closed = false;

    public StrategyWrapper(String inputPath, String outputPath, I serviceImpl) {
        outputQueue = SingleChronicleQueueBuilder.binary(outputPath).build();
        serviceOut = outputQueue.acquireAppender().methodWriter(StrategyPub.class);
        serviceImpl.init(serviceOut);

        inputQueue = SingleChronicleQueueBuilder.binary(inputPath).build();
        serviceIn = inputQueue.createTailer().methodReader(serviceImpl);

        thread = new Thread(this, new File(inputPath).getName() + " to " + new File(outputPath).getName());
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void run() {
        AffinityLock lock = AffinityLock.acquireLock();
        try {
            while (!closed) {
                if (serviceIn.readOne()) {
                    pauser.reset();
                } else {
                    pauser.pause();
                }
            }
        } finally {
            lock.release();
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
