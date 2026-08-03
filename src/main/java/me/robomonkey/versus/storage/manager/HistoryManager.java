package me.robomonkey.versus.storage.manager;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.storage.model.DuelRecord;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Records completed duels and answers history / head-to-head queries.
 * All database work runs off the main thread; callbacks are handed back on the main thread
 * so they are safe to use for sending messages and opening menus.
 */
public class HistoryManager {

    private static volatile HistoryManager instance;

    private HistoryManager() {}

    public static HistoryManager getInstance() {
        if (instance == null) {
            synchronized (HistoryManager.class) {
                if (instance == null) {
                    instance = new HistoryManager();
                }
            }
        }
        return instance;
    }

    /**
     * Stores a finished duel. Fire and forget.
     */
    public void record(DuelRecord record) {
        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            Connection conn = DatabaseManager.getInstance().getConnection();
            if (conn == null) return;

            String sql = "INSERT INTO duel_history (winner_uuid, loser_uuid, winner_name, loser_name, kit_name, ranked, timestamp) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, record.getWinnerUuid().toString());
                ps.setString(2, record.getLoserUuid().toString());
                ps.setString(3, record.getWinnerName());
                ps.setString(4, record.getLoserName());
                ps.setString(5, record.getKitName());
                ps.setBoolean(6, record.isRanked());
                ps.setLong(7, record.getTimestamp());
                ps.executeUpdate();
            } catch (Exception e) {
                Versus.error("Failed to record duel history: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Fetches a player's most recent duels, newest first.
     */
    public void getRecent(UUID uuid, int limit, Consumer<List<DuelRecord>> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            List<DuelRecord> records = new ArrayList<>();
            Connection conn = DatabaseManager.getInstance().getConnection();

            if (conn != null) {
                String sql = "SELECT * FROM duel_history WHERE winner_uuid = ? OR loser_uuid = ? " +
                             "ORDER BY timestamp DESC LIMIT ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, uuid.toString());
                    ps.setString(2, uuid.toString());
                    ps.setInt(3, limit);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            records.add(fromResultSet(rs));
                        }
                    }
                } catch (Exception e) {
                    Versus.error("Failed to fetch duel history: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            respond(callback, records);
        });
    }

    /**
     * Counts wins between two players. The result is [winsForPlayerOne, winsForPlayerTwo].
     */
    public void getHeadToHead(UUID playerOne, UUID playerTwo, Consumer<int[]> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            int[] score = new int[]{0, 0};
            Connection conn = DatabaseManager.getInstance().getConnection();

            if (conn != null) {
                String sql = "SELECT winner_uuid, COUNT(*) AS total FROM duel_history " +
                             "WHERE (winner_uuid = ? AND loser_uuid = ?) OR (winner_uuid = ? AND loser_uuid = ?) " +
                             "GROUP BY winner_uuid";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, playerOne.toString());
                    ps.setString(2, playerTwo.toString());
                    ps.setString(3, playerTwo.toString());
                    ps.setString(4, playerOne.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int total = rs.getInt("total");
                            if (playerOne.toString().equals(rs.getString("winner_uuid"))) score[0] = total;
                            else score[1] = total;
                        }
                    }
                } catch (Exception e) {
                    Versus.error("Failed to fetch head-to-head record: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            respond(callback, score);
        });
    }

    private <T> void respond(Consumer<T> callback, T result) {
        if (callback == null) return;
        Bukkit.getScheduler().runTask(Versus.getInstance(), () -> callback.accept(result));
    }

    private DuelRecord fromResultSet(ResultSet rs) throws Exception {
        return new DuelRecord(
                UUID.fromString(rs.getString("winner_uuid")),
                UUID.fromString(rs.getString("loser_uuid")),
                rs.getString("winner_name"),
                rs.getString("loser_name"),
                rs.getString("kit_name"),
                rs.getBoolean("ranked"),
                rs.getLong("timestamp")
        );
    }
}
