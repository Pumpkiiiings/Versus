package me.robomonkey.versus.duel.eventlisteners;

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
        e.setDeathMessage("");
        e.getDrops().clear();
    }
}
