package me.robomonkey.versus.duel.betting;

import me.robomonkey.versus.duel.DuelManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BettingManager {
    private static final BettingManager instance = new BettingManager();
    private final Map<UUID, BettingSession> sessions = new HashMap<>();
    private final Map<UUID, BettingGUI> activeGuis = new HashMap<>();

    private BettingManager() {}

    public static BettingManager getInstance() {
        return instance;
    }

    public void startSession(Player p1, Player p2, String arenaName) {
        BettingSession session = new BettingSession(p1, p2, arenaName);
        sessions.put(p1.getUniqueId(), session);
        sessions.put(p2.getUniqueId(), session);
        
        // Open GUI for both
        BettingGUI gui1 = new BettingGUI(session, p1);
        BettingGUI gui2 = new BettingGUI(session, p2);
        activeGuis.put(p1.getUniqueId(), gui1);
        activeGuis.put(p2.getUniqueId(), gui2);
        gui1.open();
        gui2.open();
    }
    
    public void refreshOpponent(Player player, BettingSession session) {
        UUID opp = player.getUniqueId().equals(session.getPlayer1()) ? session.getPlayer2() : session.getPlayer1();
        BettingGUI oppGui = activeGuis.get(opp);
        if (oppGui != null) {
            oppGui.refresh();
        }
    }

    public BettingSession getSession(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public void endSession(BettingSession session, boolean commenceDuel) {
        sessions.remove(session.getPlayer1());
        sessions.remove(session.getPlayer2());
        BettingGUI gui1 = activeGuis.remove(session.getPlayer1());
        BettingGUI gui2 = activeGuis.remove(session.getPlayer2());
        
        if (gui1 != null) org.bukkit.event.HandlerList.unregisterAll(gui1);
        if (gui2 != null) org.bukkit.event.HandlerList.unregisterAll(gui2);
        
        Player p1 = session.getPlayer1Online();
        Player p2 = session.getPlayer2Online();
        
        if (p1 != null) p1.closeInventory();
        if (p2 != null) p2.closeInventory();
        
        if (commenceDuel && p1 != null && p2 != null) {
            // Deduct items/money/xp and start duel
            // Money deduction
            if (session.getMoney(p1.getUniqueId()) > 0) {
                me.robomonkey.versus.dependency.EconomyManager.withdraw(p1, session.getMoney(p1.getUniqueId()));
            }
            if (session.getMoney(p2.getUniqueId()) > 0) {
                me.robomonkey.versus.dependency.EconomyManager.withdraw(p2, session.getMoney(p2.getUniqueId()));
            }
            
            // XP deduction
            if (session.getXp(p1.getUniqueId()) > 0) {
                p1.setLevel(p1.getLevel() - session.getXp(p1.getUniqueId()));
            }
            if (session.getXp(p2.getUniqueId()) > 0) {
                p2.setLevel(p2.getLevel() - session.getXp(p2.getUniqueId()));
            }
            
            // Items are already taken out of their inventory when placed into the GUI.
            
            // Start duel
            me.robomonkey.versus.arena.Arena desiredArena = null;
            if (session.getArenaName() != null) {
                desiredArena = me.robomonkey.versus.arena.ArenaManager.getInstance().getArena(session.getArenaName());
            }
            
            boolean queueIt = false;
            if (desiredArena != null) {
                if (!me.robomonkey.versus.arena.ArenaManager.getInstance().getAvailableArenas().contains(desiredArena)) {
                    queueIt = true;
                }
            } else {
                desiredArena = me.robomonkey.versus.arena.ArenaManager.getInstance().getAvailableArena();
                if (desiredArena == null) {
                    queueIt = true;
                }
            }

            if (queueIt) {
                p1.sendMessage(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.NO_ARENAS_AVAILABLE));
                p2.sendMessage(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.NO_ARENAS_AVAILABLE));
                me.robomonkey.versus.duel.request.Request queuedReq = new me.robomonkey.versus.duel.request.Request(p2, p1, 0.0, session.getArenaName());
                queuedReq.setBettingSession(session);
                me.robomonkey.versus.duel.request.RequestManager.getInstance().placeInQueue(queuedReq);
            } else {
                DuelManager.getInstance().registerDuel(java.util.List.of(p1), java.util.List.of(p2), session);
            }
        } else {
            // Cancelled, return items
            returnItems(p1, session.getItems(session.getPlayer1()));
            returnItems(p2, session.getItems(session.getPlayer2()));
            
            if (p1 != null) p1.sendMessage(me.robomonkey.versus.Versus.color("&cEl duelo fue cancelado."));
            if (p2 != null) p2.sendMessage(me.robomonkey.versus.Versus.color("&cEl duelo fue cancelado."));
        }
    }
    
    private void returnItems(Player player, java.util.List<ItemStack> items) {
        if (player == null || items == null || items.isEmpty()) return;
        for (ItemStack item : items) {
            if (item != null && item.getType() != org.bukkit.Material.AIR) {
                java.util.HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(item);
                if (!leftover.isEmpty()) {
                    for (ItemStack drop : leftover.values()) {
                        player.getWorld().dropItem(player.getLocation(), drop);
                    }
                }
            }
        }
    }
}
