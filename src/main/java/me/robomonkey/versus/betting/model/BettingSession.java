package me.robomonkey.versus.betting.model;

import me.robomonkey.versus.kit.model.Kit;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.duel.manager.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BettingSession {
    private final UUID player1;
    private final UUID player2;
    private final String arenaName;
    private final me.robomonkey.versus.kit.model.Kit kit;

    private double player1Money = 0;
    private double player2Money = 0;
    
    private int player1Xp = 0;
    private int player2Xp = 0;
    
    private List<ItemStack> player1Items = new ArrayList<>();
    private List<ItemStack> player2Items = new ArrayList<>();

    private boolean player1Ready = false;
    private boolean player2Ready = false;

    public BettingSession(Player player1, Player player2, String arenaName, me.robomonkey.versus.kit.model.Kit kit) {
        this.player1 = player1.getUniqueId();
        this.player2 = player2.getUniqueId();
        this.arenaName = arenaName;
        this.kit = kit;
    }

    public UUID getPlayer1() { return player1; }
    public UUID getPlayer2() { return player2; }
    public String getArenaName() { return arenaName; }
    public me.robomonkey.versus.kit.model.Kit getKit() { return kit; }

    public Player getPlayer1Online() { return Bukkit.getPlayer(player1); }
    public Player getPlayer2Online() { return Bukkit.getPlayer(player2); }

    public boolean isReady(UUID uuid) {
        if (uuid.equals(player1)) return player1Ready;
        if (uuid.equals(player2)) return player2Ready;
        return false;
    }

    public void setReady(UUID uuid, boolean ready) {
        if (uuid.equals(player1)) player1Ready = ready;
        if (uuid.equals(player2)) player2Ready = ready;
    }

    public boolean areBothReady() {
        return player1Ready && player2Ready;
    }

    public void setMoney(UUID uuid, double money) {
        if (uuid.equals(player1)) player1Money = money;
        if (uuid.equals(player2)) player2Money = money;
    }

    public double getMoney(UUID uuid) {
        return uuid.equals(player1) ? player1Money : player2Money;
    }

    public void setXp(UUID uuid, int xp) {
        if (uuid.equals(player1)) player1Xp = xp;
        if (uuid.equals(player2)) player2Xp = xp;
    }

    public int getXp(UUID uuid) {
        return uuid.equals(player1) ? player1Xp : player2Xp;
    }

    public void setItems(UUID uuid, List<ItemStack> items) {
        if (uuid.equals(player1)) player1Items = items;
        if (uuid.equals(player2)) player2Items = items;
    }

    public List<ItemStack> getItems(UUID uuid) {
        return uuid.equals(player1) ? player1Items : player2Items;
    }
    
    public void unreadyBoth() {
        player1Ready = false;
        player2Ready = false;
    }
}
