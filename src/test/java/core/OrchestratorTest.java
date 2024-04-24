package core;

import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.io.IOTools;
import org.junit.Test;
import core.service.Orchestrator;

import java.io.IOException;

public class OrchestratorTest {

    @Test
    public void testOrchestratorPipeline() throws IOException {

        String priceQ = OS.TMP + "/HiveMain/Queues/priceQ";
        String hiveQ = OS.TMP + "/HiveMain/Queues/hiveQ";
        String tradeQ = OS.TMP + "/HiveMain/Queues/tradeQ";
        String performanceQ = OS.TMP + "/HiveMain/Queues/performanceQ";

        String[] instruments = new String[]{"BTC_USD"};
        Orchestrator.init(instruments);
        Orchestrator.run();

        // Cleanup queues
        IOTools.deleteDirWithFiles(priceQ, 2);
        IOTools.deleteDirWithFiles(hiveQ, 2);
        IOTools.deleteDirWithFiles(tradeQ, 2);
        IOTools.deleteDirWithFiles(performanceQ, 2);

        // instantiate chronicle software queue reader class

        // read price queue and print result to console
        // read hive queue and print result to console
        // read trade queue and print result to console
        // read performance queue and print result to console
    }
}
