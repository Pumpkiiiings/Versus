package me.robomonkey.versus.duel.eventlisteners;

import me.robomonkey.versus.duel.DuelManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEventListener implements Listener {

    /**
     * Always intercepts lethal damage for duelists to prevent a real death event,
     * which fixes the inventory-loss bug caused by other plugins or Minecraft
     * processing the death before Versus could restore the inventory.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player duelist = (Player) event.getEntity();
        if (!DuelManager.getInstance().isDueling(duelist)) return;

        // If this hit would kill the player, cancel it and handle it ourselves
        if (event.getFinalDamage() >= duelist.getHealth()) {
            event.setCancelled(true);
            DuelManager.getInstance().registerDuelistDeath(duelist, true);
        }
    }
}
