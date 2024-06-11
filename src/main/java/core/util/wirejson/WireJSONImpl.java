package core.util.wirejson;

import core.service.publisher.PublisherData;
import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.Wire;
import net.openhft.chronicle.wire.SelfDescribingMarshallable;
import publish.LandingPageData;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;

public class WireJSONImpl implements WireJSON {

    private static final String OUTPUT_DIR = "/Users/bart/IdeaProjects/json-server-api/resources/JSON/";

    @Override
    public void wireLandingPageJSON(LandingPageData landingPageData) throws IOException {
        writeToJSON(landingPageData);
    }

    public static void writeToJSON(SelfDescribingMarshallable dto) throws IOException {
        // Create directories if they don't exist
        try {
            Files.createDirectories(Paths.get(OUTPUT_DIR));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Register alias for the DTO class
        ClassAliasPool.CLASS_ALIASES.addAlias(dto.getClass());

        // Create Wire object for serialization
        Wire wire = new JSONWire(Bytes.allocateElasticOnHeap());
        wire.getValueOut().object(dto);

        // Generate a unique filename
        String fileName = OUTPUT_DIR + "landingPage_" + System.nanoTime() + ".json";

        // Write JSON to file
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(wire.bytes().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("JSON written to: " + fileName);
    }
}
