package me.robomonkey.versus.duel.playerdata;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private final String name;
    private int wins;
    private int losses;
    private int currentStreak;
    private int bestStreak;
    private String activeKillEffect;
    private String activeVictoryEffect;

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak) {
        this(uuid, name, wins, losses, currentStreak, bestStreak, "NONE", "NONE");
    }

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak, String activeKillEffect, String activeVictoryEffect) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.activeKillEffect = activeKillEffect;
        this.activeVictoryEffect = activeVictoryEffect;
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    
    public int getWins() { return wins; }
    public void addWin() { this.wins++; }
    
    public int getLosses() { return losses; }
    public void addLoss() { this.losses++; }
    
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
