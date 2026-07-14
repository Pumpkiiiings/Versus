package me.robomonkey.versus.storage.model;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private final String name;
    private int wins;
    private int losses;
    private int currentStreak;
    private int bestStreak;
    private String activeKillEffect = "NONE";
    private String activeVictoryEffect = "NONE";
    private final java.util.Set<String> unlockedCosmetics = new java.util.HashSet<>();

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak) {
        this(uuid, name, wins, losses, currentStreak, bestStreak, "K_NONE", "V_NONE");
    }

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak, String activeKillEffect, String activeVictoryEffect, String unlockedCosmeticsStr) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.activeKillEffect = activeKillEffect;
        this.activeVictoryEffect = activeVictoryEffect;
        if (unlockedCosmeticsStr != null && !unlockedCosmeticsStr.isEmpty()) {
            this.unlockedCosmetics.addAll(java.util.Arrays.asList(unlockedCosmeticsStr.split(",")));
        }
    }

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak, String activeKillEffect, String activeVictoryEffect) {
        this(uuid, name, wins, losses, currentStreak, bestStreak, activeKillEffect, activeVictoryEffect, "");
    }

    public boolean hasCosmetic(String id) {
        return unlockedCosmetics.contains(id);
    }

    public void unlockCosmetic(String id) {
        unlockedCosmetics.add(id);
    }

    public String getUnlockedCosmeticsString() {
        return String.join(",", unlockedCosmetics);
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    
    public int getWins() { return wins; }
    public void addWin() { this.wins++; }
    public void setWins(int wins) { this.wins = wins; }
    
    public int getLosses() { return losses; }
    public void addLoss() { this.losses++; }
    public void setLosses(int losses) { this.losses = losses; }
    
    public int getCurrentStreak() { return currentStreak; }
    public void addStreak() { 
        this.currentStreak++; 
        if (this.currentStreak > this.bestStreak) {
            this.bestStreak = this.currentStreak;
        }
    }
    public void resetStreak() { this.currentStreak = 0; }
    
    public int getBestStreak() { return bestStreak; }

    public String getActiveKillEffect() { return activeKillEffect; }
    public void setActiveKillEffect(String activeKillEffect) { this.activeKillEffect = activeKillEffect; }

    public String getActiveVictoryEffect() { return activeVictoryEffect; }
    public void setActiveVictoryEffect(String activeVictoryEffect) { this.activeVictoryEffect = activeVictoryEffect; }
}
