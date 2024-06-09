package core.util;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.pool.ClassAliasPool;
import net.openhft.chronicle.wire.JSONWire;
import net.openhft.chronicle.wire.Marshallable;
import net.openhft.chronicle.wire.Wire;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WireJSON {

    private static final String OUTPUT_PATH = "src/test/resources/JSON/car.json";

    public static class Car implements Marshallable {
        private int number;
        private String driver;

        public Car() {
            // Default constructor needed for Marshallable
        }

        public Car(String driver, int number) {
            this.driver = driver;
            this.number = number;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }
    }

    public static void writeCarToJSON(Car car) {
        // Create directories if they don't exist
        try {
            Files.createDirectories(Paths.get("src/test/resources/JSON"));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Register alias
        ClassAliasPool.CLASS_ALIASES.addAlias(Car.class);

        // Create Wire object for serialization
        Wire wire = new JSONWire(Bytes.allocateElasticOnHeap());
        wire.getValueOut().object(car);

        // Write JSON to file
        try (FileWriter fileWriter = new FileWriter(OUTPUT_PATH)) {
            fileWriter.write(wire.bytes().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("JSON written to: " + OUTPUT_PATH);
    }
}
