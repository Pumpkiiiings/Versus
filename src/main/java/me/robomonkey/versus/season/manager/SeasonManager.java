package me.robomonkey.versus.season.manager;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.kit.manager.KitManager;
import me.robomonkey.versus.kit.model.Kit;
import me.robomonkey.versus.reward.manager.RewardManager;
import me.robomonkey.versus.reward.model.DuelReward;
import me.robomonkey.versus.storage.manager.DatabaseManager;
import me.robomonkey.versus.storage.manager.LeaderboardManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeasonManager {

    private static SeasonManager instance;

    private SeasonManager() {}

    public static SeasonManager getInstance() {
        if (instance == null) {
            instance = new SeasonManager();
        }
        return instance;
    }

    /**
     * Ends the current season.
     * 1. Updates leaderboards to ensure we have the latest.
     * 2. Issues rewards to top 3 players of each kit.
     * 3. Clears all ELO data in the database.
     * 4. Reloads the cache.
     */
    public void endSeason(CommandSender executor) {
        executor.sendMessage(MessageUtil.color("&e[Versus] &aEnding the current season..."));

        Bukkit.getScheduler().runTaskAsynchronously(Versus.getInstance(), () -> {
            // Ensure leaderboards are completely up to date
            LeaderboardManager.getInstance().updateLeaderboards();

            List<Kit> kits = KitManager.getInstance().getAllKits();

            for (Kit kit : kits) {
                List<LeaderboardManager.LeaderboardEntry> top = LeaderboardManager.getInstance().getTop(kit.getName());
                
                // Top 3 get rewards
                for (int i = 0; i < Math.min(3, top.size()); i++) {
                    LeaderboardManager.LeaderboardEntry entry = top.get(i);
                    UUID targetUuid = getUuidFromName(entry.getPlayerName());
                    
                    if (targetUuid != null) {
                        // Position-based rewards (Mock examples: XP drops)
                        // Top 1: 5000 XP
                        // Top 2: 2500 XP
                        // Top 3: 1000 XP
                        int xpReward = 0;
                        if (i == 0) xpReward = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_SEASON_REWARDS_TOP_1_XP);
                        else if (i == 1) xpReward = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_SEASON_REWARDS_TOP_2_XP);
                        else if (i == 2) xpReward = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_SEASON_REWARDS_TOP_3_XP);
                        
                        DuelReward reward = new DuelReward(targetUuid, targetUuid, System.currentTimeMillis(), new ArrayList<>(), 0.0, xpReward);
                        RewardManager.getInstance().saveReward(reward);
                    }
                }
            }

            // Reset all ELO data
            try (Connection conn = DatabaseManager.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE player_stats SET elo_data = ''")) {
                int reset = stmt.executeUpdate();
                
                // Clear cached players' ELO
                Bukkit.getOnlinePlayers().forEach(p -> {
                    me.robomonkey.versus.storage.model.PlayerStats stats = me.robomonkey.versus.storage.manager.StatsManager.getInstance().getStats(p);
                    if (stats != null) {
                        int baseElo = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_BASE_ELO);
                        kits.forEach(k -> stats.setElo(k.getName(), baseElo));
                    }
                });
                
                LeaderboardManager.getInstance().updateLeaderboards(); // Clear leaderboard cache
                
                Bukkit.broadcastMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.RANKED_SEASON_ENDED_BROADCAST));
                executor.sendMessage(MessageUtil.color("&e[Versus] &aSuccessfully reset ELO for " + reset + " players."));

            } catch (Exception e) {
                executor.sendMessage(MessageUtil.color("&cFailed to reset season: " + e.getMessage()));
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("deprecation")
    private UUID getUuidFromName(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        return op != null ? op.getUniqueId() : null;
    }
}
