package io.github.daichangya.xlsql.jdbc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Driver;
import java.sql.SQLException;

/**
 * Unit tests for xlDriver class
 */
public class xlDriverTest {
    
    private xlDriver driver;
    
    @BeforeEach
    public void setUp() {
        driver = new xlDriver();
    }
    
    @Test
    public void testAcceptsURL_ValidURL() throws SQLException {
        String validURL = Constants.URL_PFX_XLS + "/path/to/excel";
        assertTrue(driver.acceptsURL(validURL));
    }
    
    @Test
    public void testAcceptsURL_InvalidURL() throws SQLException {
        String invalidURL = "jdbc:mysql://localhost:3306/test";
        assertFalse(driver.acceptsURL(invalidURL));
    }
    
    @Test
    public void testAcceptsURL_NullURL() {
        assertThrows(SQLException.class, () -> {
            driver.acceptsURL(null);
        });
    }
    
    @Test
    public void testGetMajorVersion() {
        assertEquals(4, driver.getMajorVersion());
    }
    
    @Test
    public void testGetMinorVersion() {
        assertEquals(0, driver.getMinorVersion());
    }
    
    @Test
    public void testJdbcCompliant() {
        assertFalse(driver.jdbcCompliant());
    }
    
    @Test
    public void testGetPropertyInfo() {
        java.sql.DriverPropertyInfo[] propertyInfo = driver.getPropertyInfo(Constants.URL_PFX_XLS, null);
        assertNotNull(propertyInfo);
        assertTrue(propertyInfo.length > 0);
    }
}

