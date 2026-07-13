package me.robomonkey.versus.duel.playerdata;

import me.robomonkey.versus.Versus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StatsManager {
    private static StatsManager instance;
    private final Map<UUID, PlayerStats> statsCache = new HashMap<>();

    private StatsManager() {
    }

    public static StatsManager getInstance() {
        if (instance == null) {
            instance = new StatsManager();
        }
        return instance;
    }

    public void loadPlayer(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            try {
                Connection conn = DatabaseManager.getInstance().getConnection();
                try (PreparedStatement stmt = conn.prepareStatement("SELECT * FROM player_stats WHERE uuid = ?")) {
                    stmt.setString(1, player.getUniqueId().toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        PlayerStats stats = new PlayerStats(
                                player.getUniqueId(),
                                rs.getString("name"),
                                rs.getInt("wins"),
                                rs.getInt("losses"),
                                rs.getInt("current_streak"),
                                rs.getInt("best_streak")
                        );
                        statsCache.put(player.getUniqueId(), stats);
                    } else {
                        PlayerStats stats = new PlayerStats(player.getUniqueId(), player.getName(), 0, 0, 0, 0);
                        statsCache.put(player.getUniqueId(), stats);
                        savePlayer(stats); // Insert initial row
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void unloadPlayer(Player player) {
        PlayerStats stats = statsCache.remove(player.getUniqueId());
        if (stats != null) {
            savePlayer(stats);
        }
    }

    public void savePlayer(PlayerStats stats) {
        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            try {
                Connection conn = DatabaseManager.getInstance().getConnection();
                try (PreparedStatement stmt = conn.prepareStatement(
                        "MERGE INTO player_stats (uuid, name, wins, losses, current_streak, best_streak) KEY (uuid) VALUES (?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, stats.getUuid().toString());
                    stmt.setString(2, stats.getName());
                    stmt.setInt(3, stats.getWins());
                    stmt.setInt(4, stats.getLosses());
                    stmt.setInt(5, stats.getCurrentStreak());
                    stmt.setInt(6, stats.getBestStreak());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public PlayerStats getStats(Player player) {
        if (!statsCache.containsKey(player.getUniqueId())) {
            // Should be loaded on join, but just in case
            PlayerStats temp = new PlayerStats(player.getUniqueId(), player.getName(), 0, 0, 0, 0);
            statsCache.put(player.getUniqueId(), temp);
            return temp;
        }
        return statsCache.get(player.getUniqueId());
    }
}
