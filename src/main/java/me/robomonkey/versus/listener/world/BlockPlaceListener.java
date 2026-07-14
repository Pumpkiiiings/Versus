package me.robomonkey.versus.listener.world;

import me.robomonkey.versus.arena.manager.ArenaRollbackManager;
import me.robomonkey.versus.duel.model.Duel;
import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
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

        Duel duel = DuelManager.getInstance().getDuel(player);
        if (duel == null || duel.getArena() == null) return;

        if (!duel.getArena().canPlaceBlocks()) {
            event.setCancelled(true);
            return;
        }

        // Track the placed block so it can be removed when the duel ends
        if (duel != null && duel.getArena() != null) {
            ArenaRollbackManager.getInstance().recordPlaced(
                    duel.getArena().getName(), event.getBlock());
        }
    }
}
