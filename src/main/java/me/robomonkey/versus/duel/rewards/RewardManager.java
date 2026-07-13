package me.robomonkey.versus.duel.rewards;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.duel.playerdata.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Manages persistent duel rewards in the H2 database.
 * Anti-dupe: claim is done with an atomic UPDATE WHERE claimed=FALSE so
 * double-clicks or race conditions can never grant a reward twice.
 */
public class RewardManager {

    private static RewardManager instance;

    private RewardManager() {}

    public static RewardManager getInstance() {
        if (instance == null) {
            instance = new RewardManager();
        }
        return instance;
    }

    // ── Persistence ──────────────────────────────────────────────────────────

    /**
     * Deletes duel rewards older than 7 days, checking every 12 hours.
     */
    public void startCleanupTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Versus.getInstance(), () -> {
            Connection conn = DatabaseManager.getInstance().getConnection();
            if (conn == null) return;

            long expirationTime = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            String sql = "DELETE FROM duel_rewards WHERE timestamp < ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, expirationTime);
                int deleted = ps.executeUpdate();
                if (deleted > 0) {
                    Versus.log("Deleted " + deleted + " expired duel rewards.");
                }
            } catch (SQLException e) {
                Versus.error("Failed to delete expired rewards: " + e.getMessage());
                e.printStackTrace();
            }
        }, 0L, 12L * 60L * 60L * 20L); // 12 hours in ticks
    }
    /**
     * Saves a new reward to the database and sets its auto-generated id.
     */
    public void saveReward(DuelReward reward) {
        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            Connection conn = DatabaseManager.getInstance().getConnection();
            if (conn == null) return;

            String sql = "INSERT INTO duel_rewards (winner_uuid, loser_uuid, timestamp, items_json, money, xp, claimed) " +
                         "VALUES (?, ?, ?, ?, ?, ?, FALSE)";
            try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, reward.getWinnerUuid().toString());
                ps.setString(2, reward.getLoserUuid().toString());
                ps.setLong(3, reward.getTimestamp());
                ps.setString(4, serializeItems(reward.getItems()));
                ps.setDouble(5, reward.getMoney());
                ps.setInt(6, reward.getXp());
                ps.executeUpdate();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) reward.setId(rs.getInt(1));
                }
            } catch (SQLException e) {
                Versus.error("Failed to save duel reward: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns all unclaimed rewards for the given player UUID.
     */
    public List<DuelReward> getUnclaimedRewards(UUID winnerUuid) {
        List<DuelReward> rewards = new ArrayList<>();
        Connection conn = DatabaseManager.getInstance().getConnection();
        if (conn == null) return rewards;

        String sql = "SELECT * FROM duel_rewards WHERE winner_uuid = ? AND claimed = FALSE ORDER BY timestamp DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, winnerUuid.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rewards.add(fromResultSet(rs));
            }
        } catch (SQLException e) {
            Versus.error("Failed to fetch rewards: " + e.getMessage());
            e.printStackTrace();
        }
        return rewards;
    }

    /**
     * Atomically claims a reward.
     * Returns true ONLY if the reward was successfully claimed (not already claimed).
     * Anti-dupe: UPDATE only executes if claimed=FALSE, so concurrent calls are safe.
     */
    public boolean claimReward(int rewardId, UUID winnerUuid) {
        Connection conn = DatabaseManager.getInstance().getConnection();
        if (conn == null) return false;

        String sql = "UPDATE duel_rewards SET claimed = TRUE " +
                     "WHERE id = ? AND winner_uuid = ? AND claimed = FALSE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rewardId);
            ps.setString(2, winnerUuid.toString());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected == 1; // Only true if the row was actually updated (not already claimed)
        } catch (SQLException e) {
            Versus.error("Failed to claim reward id=" + rewardId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── Serialization ─────────────────────────────────────────────────────────

    private String serializeItems(List<ItemStack> items) {
        if (items == null || items.isEmpty()) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BukkitObjectOutputStream oos = new BukkitObjectOutputStream(baos);
            oos.writeInt(items.size());
            for (ItemStack item : items) {
                oos.writeObject(item);
            }
            oos.close();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            Versus.error("Failed to serialize reward items: " + e.getMessage());
            return null;
        }
    }

    public List<ItemStack> deserializeItems(String base64) {
        List<ItemStack> items = new ArrayList<>();
        if (base64 == null || base64.isEmpty()) return items;
        try {
            byte[] data = Base64.getDecoder().decode(base64);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            BukkitObjectInputStream ois = new BukkitObjectInputStream(bais);
            int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                items.add((ItemStack) ois.readObject());
            }
            ois.close();
        } catch (Exception e) {
            Versus.error("Failed to deserialize reward items: " + e.getMessage());
        }
        return items;
    }

    private DuelReward fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        UUID winner = UUID.fromString(rs.getString("winner_uuid"));
        UUID loser = UUID.fromString(rs.getString("loser_uuid"));
        long timestamp = rs.getLong("timestamp");
        List<ItemStack> items = deserializeItems(rs.getString("items_json"));
        double money = rs.getDouble("money");
        int xp = rs.getInt("xp");
        boolean claimed = rs.getBoolean("claimed");
        return new DuelReward(id, winner, loser, timestamp, items, money, xp, claimed);
    }
}
