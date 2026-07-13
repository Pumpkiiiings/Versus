package me.robomonkey.versus.arena;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaVisibilityManager {
    
    // Tracks which duel an entity (like arrow or dropped item) belongs to
    private static final Map<Integer, Duel> entityDuelMap = new ConcurrentHashMap<>();
    
    // Tracks which duel a spectator is currently watching
    private static final Map<UUID, Duel> spectatorMap = new ConcurrentHashMap<>();

    public static void updateVisibility(Player p) {
        if (p == null || !p.isOnline()) return;
        
        for (Player other : Bukkit.getOnlinePlayers()) {
            if (p.equals(other)) continue;
            
            // Re-show all players to undo any previous hiding
            p.showPlayer(Versus.getInstance(), other);
            other.showPlayer(Versus.getInstance(), p);
        }
    }

    public static void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updateVisibility(p);
        }
    }

    public static boolean shouldSee(Player p1, Player p2) {
        Duel p1Duel = DuelManager.getInstance().getDuel(p1);
        Duel p2Duel = DuelManager.getInstance().getDuel(p2);
        Duel p1Spec = spectatorMap.get(p1.getUniqueId());
        Duel p2Spec = spectatorMap.get(p2.getUniqueId());

        // Both in lobby (not dueling and not spectating)
        if (p1Duel == null && p1Spec == null && p2Duel == null && p2Spec == null) return true;

        // Both in the same active duel
        if (p1Duel != null && p1Duel.equals(p2Duel)) return true;

        // P1 is spectating P2's duel
        if (p1Spec != null && p1Spec.equals(p2Duel)) return true;
        
        // P2 is spectating P1's duel
        if (p2Spec != null && p2Spec.equals(p1Duel)) return true;

        // Both are spectating the same duel
        if (p1Spec != null && p1Spec.equals(p2Spec)) return true;

        // Otherwise, they should not see each other
        return false;
    }

    public static void addSpectator(Player player, Duel duel) {
        spectatorMap.put(player.getUniqueId(), duel);
        updateVisibility(player);
    }

    public static void removeSpectator(Player player) {
        spectatorMap.remove(player.getUniqueId());
        updateVisibility(player);
    }
    
    public static Duel getSpectatingDuel(Player player) {
        return spectatorMap.get(player.getUniqueId());
    }

    public static void trackEntity(int entityId, Duel duel) {
        entityDuelMap.put(entityId, duel);
    }

    public static void untrackEntity(int entityId) {
        entityDuelMap.remove(entityId);
    }

    public static Duel getEntityDuel(int entityId) {
        return entityDuelMap.get(entityId);
    }
}
