package me.robomonkey.versus.duel.eventlisteners;

import me.robomonkey.versus.arena.ArenaRollbackManager;
import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (!DuelManager.getInstance().isDueling(player)) return;

        if (!Settings.is(Setting.ALLOW_BLOCK_PLACEMENTS)) {
            event.setCancelled(true);
            return;
        }

        // Track the placed block so it can be removed when the duel ends
        Duel duel = DuelManager.getInstance().getDuel(player);
        if (duel != null && duel.getArena() != null) {
            ArenaRollbackManager.getInstance().recordPlaced(
                    duel.getArena().getName(), event.getBlock());
        }
    }
}
