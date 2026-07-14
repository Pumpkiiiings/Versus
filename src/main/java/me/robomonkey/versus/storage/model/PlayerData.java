package me.robomonkey.versus.storage.model;

import me.robomonkey.versus.arena.model.Arena;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {

    public ItemStack[] items;
    public String arenaName;
    public LocationData previousLocation;
    public int xpLevel;
    public float xpProgress;
    public GameMode gameMode;

    public PlayerData(Player player, Arena currentArena) {
        items = player.getInventory().getContents();
        previousLocation = new LocationData(player.getLocation());
        xpLevel = player.getLevel();
        xpProgress = player.getExp();
        arenaName = currentArena.getName();
        gameMode = player.getGameMode();
    }
}

