package me.robomonkey.versus.dependency;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.robomonkey.versus.duel.DuelManager;
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

        return null; // Placeholder is unknown
    }
}
