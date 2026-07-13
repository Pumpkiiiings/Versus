package me.robomonkey.versus.duel.playerdata;

import me.robomonkey.versus.Versus;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        connect();
        setupTable();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void connect() {
        try {
            File dataFolder = new File(Versus.getInstance().getDataFolder(), "data");
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            String path = dataFolder.getAbsolutePath().replace('\\', '/');
            String url = "jdbc:h2:" + path + "/database";
            
            java.util.Properties props = new java.util.Properties();
            props.put("user", "sa");
            props.put("password", "");
            connection = new org.h2.Driver().connect(url, props);
        } catch (SQLException e) {
            Versus.error("Failed to connect to H2 Database!");
            e.printStackTrace();
        }
    }

    private void setupTable() {
        if (connection == null) return;
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS player_stats (" +
                    "uuid VARCHAR(36) PRIMARY KEY, " +
                    "name VARCHAR(16), " +
                    "wins INT DEFAULT 0, " +
                    "losses INT DEFAULT 0, " +
                    "current_streak INT DEFAULT 0, " +
                    "best_streak INT DEFAULT 0" +
                    ")");
            statement.execute("CREATE TABLE IF NOT EXISTS duel_rewards (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "winner_uuid VARCHAR(36) NOT NULL, " +
                    "loser_uuid VARCHAR(36) NOT NULL, " +
                    "timestamp BIGINT NOT NULL, " +
                    "items_json TEXT, " +
                    "money DOUBLE DEFAULT 0, " +
                    "xp INTEGER DEFAULT 0, " +
                    "claimed BOOLEAN DEFAULT FALSE" +
                    ")");
        } catch (SQLException e) {
            Versus.error("Failed to create database tables!");
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
