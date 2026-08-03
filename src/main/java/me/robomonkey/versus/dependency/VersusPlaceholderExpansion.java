package me.robomonkey.versus.dependency;

import me.robomonkey.versus.config.model.Placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.robomonkey.versus.duel.manager.DuelManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VersusPlaceholderExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "versus";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Antigravity";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; 
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        if (params.equalsIgnoreCase("in_duel")) {
            DuelManager duelManager = DuelManager.getInstance();
            if (duelManager.isDueling(player) || duelManager.isSpectating(player)) {
                return "En Duelo";
            } else {
                return "";
            }
        }

        // Checked before "rank_" so the longer prefixes win.
        if (params.startsWith("rank_progress_")) {
            int elo = eloFor(player, params.substring(14));
            return String.valueOf(me.robomonkey.versus.ranked.manager.RankManager.getInstance().getEloToNextRank(elo));
        }

        if (params.startsWith("rank_next_")) {
            int elo = eloFor(player, params.substring(10));
            me.robomonkey.versus.ranked.model.Rank next =
                    me.robomonkey.versus.ranked.manager.RankManager.getInstance().getNextRank(elo);
            return next == null ? "" : me.robomonkey.versus.util.MessageUtil.color(next.getDisplayName());
        }

        if (params.startsWith("rank_")) {
            int elo = eloFor(player, params.substring(5));
            return me.robomonkey.versus.util.MessageUtil.color(
                    me.robomonkey.versus.ranked.manager.RankManager.getInstance().getDisplayName(elo));
        }

        if (params.startsWith("elo_")) {
            String kitName = params.substring(4);
            me.robomonkey.versus.storage.model.PlayerStats stats = me.robomonkey.versus.storage.manager.StatsManager.getInstance().getStats(player);
            if (stats != null) {
                return String.valueOf(stats.getElo(kitName));
            }
            return "1000";
        }

        if (params.startsWith("top_name_")) {
            String[] parts = params.substring(9).split("_");
            if (parts.length == 2) {
                String kitName = parts[0];
                try {
                    int pos = Integer.parseInt(parts[1]) - 1; // 1-indexed to 0-indexed
                    java.util.List<me.robomonkey.versus.storage.manager.LeaderboardManager.LeaderboardEntry> top = me.robomonkey.versus.storage.manager.LeaderboardManager.getInstance().getTop(kitName);
                    if (pos >= 0 && pos < top.size()) {
                        return top.get(pos).getPlayerName();
                    }
                } catch (NumberFormatException ignored) {}
            }
            return "N/A";
        }

        if (params.startsWith("top_elo_")) {
            String[] parts = params.substring(8).split("_");
            if (parts.length == 2) {
                String kitName = parts[0];
                try {
                    int pos = Integer.parseInt(parts[1]) - 1; // 1-indexed to 0-indexed
                    java.util.List<me.robomonkey.versus.storage.manager.LeaderboardManager.LeaderboardEntry> top = me.robomonkey.versus.storage.manager.LeaderboardManager.getInstance().getTop(kitName);
                    if (pos >= 0 && pos < top.size()) {
                        return String.valueOf(top.get(pos).getElo());
                    }
                } catch (NumberFormatException ignored) {}
            }
            return "0";
        }

        return null; // Placeholder is unknown
    }

    /**
     * Resolves a player's ELO for the given kit name.
     */
    private int eloFor(Player player, String kitName) {
        me.robomonkey.versus.storage.model.PlayerStats stats =
                me.robomonkey.versus.storage.manager.StatsManager.getInstance().getStats(player);
        if (stats == null) {
            return me.robomonkey.versus.config.model.Settings.getNumber(
                    me.robomonkey.versus.config.model.Setting.RANKED_BASE_ELO);
        }
        return stats.getElo(kitName);
    }
}
