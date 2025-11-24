package com.jsdiff.xlsql.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for xlInstance class
 */
public class xlInstanceTest {
    
    @BeforeEach
    public void setUp() {
        // Disconnect any existing instance before each test
        xlInstance.disconnect();
    }
    
    @AfterEach
    public void tearDown() {
        // Clean up after each test
        xlInstance.disconnect();
    }
    
    @Test
    public void testGetInstance_Default() throws xlException {
        xlInstance instance = xlInstance.getInstance();
        assertNotNull(instance);
    }
    
    @Test
    public void testGetInstance_Singleton() throws xlException {
        xlInstance instance1 = xlInstance.getInstance();
        xlInstance instance2 = xlInstance.getInstance();
        assertSame(instance1, instance2);
    }
    
    @Test
    public void testDisconnect() throws xlException {
        xlInstance instance = xlInstance.getInstance();
        assertNotNull(instance);
        
        xlInstance.disconnect();
        // After disconnect, getInstance should create a new instance
        xlInstance newInstance = xlInstance.getInstance();
        assertNotNull(newInstance);
    }
    
    @Test
    public void testGetEngine() throws xlException {
        xlInstance instance = xlInstance.getInstance();
        String engine = instance.getEngine();
        assertNotNull(engine);
        assertFalse(engine.isEmpty());
    }
    
    @Test
    public void testGetDatabase() throws xlException {
        xlInstance instance = xlInstance.getInstance();
        String database = instance.getDatabase();
        assertNotNull(database);
    }
    
    @Test
    public void testSetAndGetDatabase() throws xlException {
        xlInstance instance = xlInstance.getInstance();
        String testPath = "/test/path";
        instance.setDatabase(testPath);
        assertEquals(testPath, instance.getDatabase());
    }
}

