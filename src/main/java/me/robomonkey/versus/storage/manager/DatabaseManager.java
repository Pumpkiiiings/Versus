package me.robomonkey.versus.storage.manager;

import me.robomonkey.versus.Versus;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static volatile DatabaseManager instance;
    private volatile Connection connection;

    private DatabaseManager() {
        connect();
        setupTable();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    private synchronized void connect() {
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
                    "best_streak INT DEFAULT 0, " +
                    "kill_effect VARCHAR(32) DEFAULT 'NONE', " +
                    "victory_effect VARCHAR(32) DEFAULT 'NONE', " +
                    "unlocked_cosmetics VARCHAR(1000) DEFAULT ''," +
                    "elo_data VARCHAR(2000) DEFAULT ''" +
                    ")");
            try { statement.execute("ALTER TABLE player_stats ADD COLUMN kill_effect VARCHAR(32) DEFAULT 'NONE'"); } catch (SQLException ignored) {}
            try { statement.execute("ALTER TABLE player_stats ADD COLUMN victory_effect VARCHAR(32) DEFAULT 'NONE'"); } catch (SQLException ignored) {}
            try { statement.execute("ALTER TABLE player_stats ADD COLUMN unlocked_cosmetics VARCHAR(1000) DEFAULT ''"); } catch (SQLException ignored) {}
            try { statement.execute("ALTER TABLE player_stats ADD COLUMN elo_data VARCHAR(2000) DEFAULT ''"); } catch (SQLException ignored) {}
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
            statement.execute("CREATE TABLE IF NOT EXISTS duel_history (" +
                    "id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "winner_uuid VARCHAR(36) NOT NULL, " +
                    "loser_uuid VARCHAR(36) NOT NULL, " +
                    "winner_name VARCHAR(16), " +
                    "loser_name VARCHAR(16), " +
                    "kit_name VARCHAR(64), " +
                    "ranked BOOLEAN DEFAULT FALSE, " +
                    "timestamp BIGINT NOT NULL" +
                    ")");
            // Head-to-head lookups filter on both columns, so index each of them.
            try { statement.execute("CREATE INDEX IF NOT EXISTS idx_history_winner ON duel_history (winner_uuid)"); } catch (SQLException ignored) {}
            try { statement.execute("CREATE INDEX IF NOT EXISTS idx_history_loser ON duel_history (loser_uuid)"); } catch (SQLException ignored) {}
        } catch (SQLException e) {
            Versus.error("Failed to create database tables!");
            e.printStackTrace();
        }
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
