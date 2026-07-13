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
            
            // Update viewer (p) seeing target (other)
            if (canSee(p, other)) {
                p.showPlayer(Versus.getInstance(), other);
            } else {
                p.hidePlayer(Versus.getInstance(), other);
            }
            
            // Update target (other) seeing viewer (p)
            if (canSee(other, p)) {
                other.showPlayer(Versus.getInstance(), p);
            } else {
                other.hidePlayer(Versus.getInstance(), p);
            }
        }
    }

    public static void updateAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            updateVisibility(p);
        }
    }

    public static boolean canSee(Player viewer, Player target) {
        Duel viewerDuel = DuelManager.getInstance().getDuel(viewer);
        Duel targetDuel = DuelManager.getInstance().getDuel(target);
        Duel viewerSpec = spectatorMap.get(viewer.getUniqueId());
        Duel targetSpec = spectatorMap.get(target.getUniqueId());

        // Target is spectating
        if (targetSpec != null) {
            // Viewer can only see the spectator if the viewer is ALSO spectating the SAME duel
            if (viewerSpec != null && viewerSpec.equals(targetSpec)) {
                return true;
            }
            return false;
        }

        // Viewer is spectating
        if (viewerSpec != null) {
            if (targetDuel != null && viewerSpec.equals(targetDuel)) {
                return true;
            }
            return false;
        }

        // Both are duelers
        if (viewerDuel != null && targetDuel != null) {
            return viewerDuel.equals(targetDuel);
        }

        // One is dueling, one is in lobby
        if (viewerDuel != null || targetDuel != null) {
            return false;
        }

        // Both in lobby
        return true;
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
