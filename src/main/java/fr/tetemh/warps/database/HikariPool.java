package fr.tetemh.warps.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class HikariPool {
    private HikariDataSource hikari;

    public HikariDataSource init(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();

        // set pool name so the logging output can be linked back to us
        hikariConfig.setPoolName("infra-hikari");

        // allow the implementation to configure the HikariConfig appropriately with these values
        configureDatabase(hikariConfig,
            config.getString("storage.sql.hostname"),
            config.getString("storage.sql.port"),
            config.getString("storage.sql.database"),
            config.getString("storage.sql.username"),
            config.getString("storage.sql.password"));

        // configure the connection pool
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setMinimumIdle(10);
        hikariConfig.setMaxLifetime(1800000);
        hikariConfig.setKeepaliveTime(0);
        hikariConfig.setConnectionTimeout(5000);

        hikariConfig.setConnectionTestQuery("SELECT 1");

        this.hikari = new HikariDataSource(hikariConfig);
        return hikari;
    }

    public void close() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    private void configureDatabase(HikariConfig config, String hostname, String port, String databaseName, String username, String password) {
        try {
            MariaDbDataSource mariaDbDataSource = new MariaDbDataSource();
            mariaDbDataSource.setUrl("jdbc:mariadb://" + hostname + ":" + port + "/" + databaseName);
            mariaDbDataSource.setUser(username);
            mariaDbDataSource.setPassword(password);
            config.setDataSource(mariaDbDataSource);
        } catch (SQLException exception) {
            System.out.println("MariaDBPool error");
            exception.printStackTrace();
        }
    }

    public HikariDataSource getHikari() {
        return hikari;
    }

}
