package me.robomonkey.versus.duel.playerdata;

import java.util.UUID;

public class PlayerStats {
    private final UUID uuid;
    private final String name;
    private int wins;
    private int losses;
    private int currentStreak;
    private int bestStreak;

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak) {
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
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
}
