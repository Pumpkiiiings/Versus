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
    private final java.util.Map<String, Integer> eloMap = new java.util.HashMap<>();

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak) {
        this(uuid, name, wins, losses, currentStreak, bestStreak, "K_NONE", "V_NONE");
    }

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak, String activeKillEffect, String activeVictoryEffect, String unlockedCosmeticsStr) {
        this(uuid, name, wins, losses, currentStreak, bestStreak, activeKillEffect, activeVictoryEffect, unlockedCosmeticsStr, "");
    }

    public PlayerStats(UUID uuid, String name, int wins, int losses, int currentStreak, int bestStreak, String activeKillEffect, String activeVictoryEffect, String unlockedCosmeticsStr, String eloDataStr) {
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
        if (eloDataStr != null && !eloDataStr.isEmpty()) {
            this.eloMap.putAll(parseEloData(eloDataStr));
        }
    }

    public static java.util.Map<String, Integer> parseEloData(String eloDataStr) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        if (eloDataStr == null || eloDataStr.isEmpty()) return map;
        for (String entry : eloDataStr.split(",")) {
            String[] split = entry.split(":");
            if (split.length == 2) {
                try {
                    map.put(split[0], Integer.parseInt(split[1]));
                } catch (NumberFormatException ignored) {}
            }
        }
        return map;
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

    public int getElo(String kitName) {
        int baseElo = me.robomonkey.versus.config.model.Settings.getNumber(me.robomonkey.versus.config.model.Setting.RANKED_BASE_ELO);
        return eloMap.getOrDefault(kitName, baseElo);
    }

    public void setElo(String kitName, int elo) {
        eloMap.put(kitName, elo);
    }

    public String getEloDataString() {
        java.util.List<String> list = new java.util.ArrayList<>();
        for (java.util.Map.Entry<String, Integer> entry : eloMap.entrySet()) {
            list.add(entry.getKey() + ":" + entry.getValue());
        }
        return String.join(",", list);
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
