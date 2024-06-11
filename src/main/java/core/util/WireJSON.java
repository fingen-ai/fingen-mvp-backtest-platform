package core.util;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.Wire;
import net.openhft.chronicle.wire.SelfDescribingMarshallable;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WireJSON {

    //private static final String OUTPUT_PATH = "src/test/resources/JSON/data.json";
    private static final String OUTPUT_PATH = "/Users/bart/IdeaProjects/json-server-api/resources/JSON/data.json";

    public static void writeToJSON(SelfDescribingMarshallable dto) {
        // Create directories if they don't exist
        try {
            //Files.createDirectories(Paths.get("src/test/resources/JSON"));
            Files.createDirectories(Paths.get("/Users/bart/IdeaProjects/json-server-api/resources/JSON"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Register alias for the DTO class
        ClassAliasPool.CLASS_ALIASES.addAlias(dto.getClass());

        // Create Wire object for serialization
        Wire wire = new JSONWire(Bytes.allocateElasticOnHeap());
        wire.getValueOut().object(dto);

        // Write JSON to file
        try (FileWriter fileWriter = new FileWriter(OUTPUT_PATH)) {
            fileWriter.write(wire.bytes().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("JSON written to: " + OUTPUT_PATH);
    }
}
