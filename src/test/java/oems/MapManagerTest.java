package oems;

import oems.map.MapManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MapManagerTest {
    private MapManager<String, String> manager;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new MapManager<>(String.class, String.class, 50);
    }

    @Test
    public void testAddAndGet() {
        manager.add("key1", "value1");
        assertEquals("value1", manager.get("key1"), "Add or Get method failed.");
    }

    @Test
    public void testUpdateAndGet() {
        manager.add("key2", "value2");
        manager.update("key2", "updatedValue2");
        assertEquals("updatedValue2", manager.get("key2"), "Update or Get method failed.");
    }

    @Test
    public void testDeleteAndGet() {
        manager.add("key3", "value3");
        manager.delete("key3");
        assertNull(manager.get("key3"), "Delete or Get method failed.");
    }

    @AfterEach
    public void tearDown() {
        manager.close();
    }
}
