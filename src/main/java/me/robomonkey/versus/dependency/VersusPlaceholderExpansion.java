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
}
