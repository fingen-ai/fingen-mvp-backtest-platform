package core.util;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.Wire;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WireJSONTest {

    @Test
    public void testCarSerialization() {
        String filePath = "src/test/resources/JSON/car.json";

        // Register alias
        ClassAliasPool.CLASS_ALIASES.addAlias(WireJSON.Car.class);

        // Create a Car object
        WireJSON.Car car = new WireJSON.Car("Lewis Hamilton", 44);

        // Write Car object to JSON file
        WireJSON.writeCarToJSON(car);

        // Verify that the file was written
        assertTrue(Files.exists(Paths.get(filePath)));

        // Read JSON from file and deserialize
        String json = "";
        try {
            json = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Wire readWire = new JSONWire(Bytes.wrapForRead(json.getBytes()));
        WireJSON.Car deserializedCar = readWire.getValueIn().object(WireJSON.Car.class);

        // Verify the deserialized object
        assertEquals(car.getDriver(), deserializedCar.getDriver());
        assertEquals(car.getNumber(), deserializedCar.getNumber());
    }
}
