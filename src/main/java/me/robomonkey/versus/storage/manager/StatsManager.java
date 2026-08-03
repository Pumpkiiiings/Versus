package me.robomonkey.versus.storage.manager;

import me.robomonkey.versus.storage.model.PlayerStats;

import me.robomonkey.versus.Versus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class StatsManager {
    private static volatile StatsManager instance;
    // Written from async DB tasks, read from the main thread.
    private final Map<UUID, PlayerStats> statsCache = new ConcurrentHashMap<>();

    private StatsManager() {
    }

    public static StatsManager getInstance() {
        if (instance == null) {
            synchronized (StatsManager.class) {
                if (instance == null) {
                    instance = new StatsManager();
                }
            }
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
                                rs.getInt("best_streak"),
                                normalizeEffectId(rs.getString("kill_effect"), "K_NONE"),
                                normalizeEffectId(rs.getString("victory_effect"), "V_NONE"),
                                rs.getString("unlocked_cosmetics"),
                                rs.getString("elo_data")
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
                        "MERGE INTO player_stats (uuid, name, wins, losses, current_streak, best_streak, kill_effect, victory_effect, unlocked_cosmetics, elo_data) KEY (uuid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    stmt.setString(1, stats.getUuid().toString());
                    stmt.setString(2, stats.getName());
                    stmt.setInt(3, stats.getWins());
                    stmt.setInt(4, stats.getLosses());
                    stmt.setInt(5, stats.getCurrentStreak());
                    stmt.setInt(6, stats.getBestStreak());
                    stmt.setString(7, stats.getActiveKillEffect());
                    stmt.setString(8, stats.getActiveVictoryEffect());
                    stmt.setString(9, stats.getUnlockedCosmeticsString());
                    stmt.setString(10, stats.getEloDataString());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public PlayerStats getStats(Player player) {
        // Should be loaded on join, but fall back to a blank record just in case.
        return statsCache.computeIfAbsent(player.getUniqueId(),
                uuid -> new PlayerStats(uuid, player.getName(), 0, 0, 0, 0));
    }

    /**
     * Normalizes a stored effect ID. If the stored value is null, empty, or the
     * old legacy "NONE" string (without category prefix), it returns the given default.
     */
    private String normalizeEffectId(String stored, String defaultId) {
        if (stored == null || stored.isEmpty() || stored.equalsIgnoreCase("NONE")) {
            return defaultId;
        }
        return stored;
    }
}
