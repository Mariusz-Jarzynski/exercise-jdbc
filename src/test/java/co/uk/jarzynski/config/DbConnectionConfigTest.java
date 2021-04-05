package co.uk.jarzynski.config;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

// test class must be public
public class DbConnectionConfigTest {

    @Test
    public void checkConnection() {
        try (Connection connection = DbConnectionConfig.getInstance().getConnection()) {
            Assert.assertNotNull("Couldn't get connection to dv", connection);
        } catch (SQLException throwables) {
            Assert.fail("Something wrong happened during obtaining db connection");
            throwables.printStackTrace();
        }
    }

}