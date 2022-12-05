package fr.tetemh.warps.database;

import com.zaxxer.hikari.HikariDataSource;
import fr.tetemh.warps.Warps;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Loader {
    private final HikariDataSource dataSource;

    private final String SCHEMA_FILE = "storage.sql";
    private final List<String> TABLES = Arrays.asList("player_data");
    private final String DatabaseName = Warps.getInstance().getConfig().getString("storage.sql.database");

    private final String CHECK_TABLE = "SELECT TABLE_NAME " +
            "FROM information_schema.TABLES " +
            "WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?;";

    public Loader(FileConfiguration config) throws SQLException {
        dataSource = new HikariPool().init(config);
        // try {
        //     if (!checkStorages()) {
        //         applySchema();
        //     }
        // } catch (IOException exception) {
        //     exception.printStackTrace();
        // }
    }
    
    /**
     * Apply SQL Default schema with infra_schema.sql dump
     */
    /* private void applySchema() throws IOException { 
        List<String> statements;
        //  Read schema file
        try (InputStream schemaFileIS = this.getClass().getResourceAsStream(SCHEMA_FILE)) {
            if (schemaFileIS == null) {
                System.out.println("Missing schema SQL file !");
            }
            statements = getQueries(schemaFileIS).stream()
                    .collect(Collectors.toList());
        }
        //  Apply Schema
        try (Connection connection = dataSource.getConnection();
            Statement s = connection.createStatement()) {
            connection.setAutoCommit(false);
            for (String query : statements) {
                s.addBatch(query);
            }
            s.executeBatch();
        } catch (Exception exception) {
            if (!exception.getMessage().contains("already exists")) {
                exception.printStackTrace();
            }
        }
    } */

    /**
     * Check if all tables are created
     * @return true if all tables are created
    */
    public boolean checkStorages() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            for (String table : TABLES) {
                try (PreparedStatement q = connection.prepareStatement(CHECK_TABLE)) {
                    q.setString(1, DatabaseName);
                    q.setString(2, table);
                    q.execute();
                    if (!q.getResultSet().next()) {
                        System.out.println("Table: " + table + " not found in " + DatabaseName);
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Convert file input stream into List of SQL queries
     */
    public @Nonnull List<String> getQueries(InputStream is) throws IOException {
        List<String> queries = new LinkedList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                sb.append(line);

                // check for end of declaration
                if (line.endsWith(";")) {
                    sb.deleteCharAt(sb.length() - 1);

                    String result = sb.toString().trim();
                    if (!result.isEmpty()) {
                        queries.add(result);
                    }

                    // reset
                    sb = new StringBuilder();
                }
            }
        }
        return queries;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
