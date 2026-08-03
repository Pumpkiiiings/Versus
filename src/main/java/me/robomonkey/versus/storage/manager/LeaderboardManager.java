package me.robomonkey.versus.storage.manager;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.storage.model.PlayerStats;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private static LeaderboardManager instance;
    // Map of Kit Name -> List of (Player Name, ELO)
    private final Map<String, List<LeaderboardEntry>> topPlayersCache = new ConcurrentHashMap<>();

    private LeaderboardManager() {
        // Update every 5 minutes (6000 ticks)
        Bukkit.getScheduler().runTaskTimerAsynchronously(Versus.getInstance(), this::updateLeaderboards, 100L, 6000L);
    }

    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }

    public void updateLeaderboards() {
        Map<String, List<LeaderboardEntry>> tempMap = new HashMap<>();

        // The connection is the plugin-wide singleton, so it must NOT be closed here.
        Connection conn = DatabaseManager.getInstance().getConnection();
        if (conn == null) return;

        try (PreparedStatement stmt = conn.prepareStatement("SELECT name, elo_data FROM player_stats");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String eloDataStr = rs.getString("elo_data");

                Map<String, Integer> eloMap = PlayerStats.parseEloData(eloDataStr);
                for (Map.Entry<String, Integer> entry : eloMap.entrySet()) {
                    String kitName = entry.getKey();
                    int elo = entry.getValue();

                    tempMap.putIfAbsent(kitName, new ArrayList<>());
                    tempMap.get(kitName).add(new LeaderboardEntry(name, elo));
                }
            }

        } catch (Exception e) {
            // Keep the previous cache rather than wiping it because of a transient failure.
            Versus.error("Failed to update leaderboards from database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Sort and limit to top 10 per kit
        Map<String, List<LeaderboardEntry>> rebuilt = new HashMap<>();
        for (Map.Entry<String, List<LeaderboardEntry>> entry : tempMap.entrySet()) {
            List<LeaderboardEntry> sorted = entry.getValue().stream()
                    .sorted((e1, e2) -> Integer.compare(e2.getElo(), e1.getElo())) // Descending
                    .limit(10)
                    .collect(Collectors.toList());
            rebuilt.put(entry.getKey(), sorted);
        }

        // Replace wholesale rather than merging, so kits that no longer have any rated
        // players (for example right after a season reset) drop out of the cache.
        topPlayersCache.keySet().retainAll(rebuilt.keySet());
        topPlayersCache.putAll(rebuilt);
    }

    public List<LeaderboardEntry> getTop(String kitName) {
        return topPlayersCache.getOrDefault(kitName, Collections.emptyList());
    }

    public static class LeaderboardEntry {
        private final String playerName;
        private final int elo;

        public LeaderboardEntry(String playerName, int elo) {
            this.playerName = playerName;
            this.elo = elo;
        }

        public String getPlayerName() {
            return playerName;
        }

        public int getElo() {
            return elo;
        }
    }
}
