package me.robomonkey.versus.listener.player;

import me.robomonkey.versus.listener.combat.DamageEventListener;
import me.robomonkey.versus.duel.manager.DuelManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Death handling is now managed entirely in {@link DamageEventListener},
 * which intercepts lethal hits before a real death can occur.
 * This listener suppresses any residual death messages as a safety net.
 */
public class DeathEventListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Safety net: if somehow a duelist dies for real, at least hide the message.
        if (me.robomonkey.versus.duel.manager.DuelManager.getInstance().isDueling(e.getEntity())) {
            e.setDeathMessage("");
            e.getDrops().clear();
            e.setDroppedExp(0);
            e.setKeepInventory(true);
        }
    }
}
