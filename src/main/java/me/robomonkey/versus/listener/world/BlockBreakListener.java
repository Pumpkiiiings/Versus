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
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (!DuelManager.getInstance().isDueling(player)) return;

        Duel duel = DuelManager.getInstance().getDuel(player);
        if (duel == null || duel.getArena() == null) return;

        if (!duel.getArena().canDestroyBlocks()) {
            event.setCancelled(true);
            return;
        }

        // Snapshot the block BEFORE it is removed so we can restore it after the duel
        if (duel != null && duel.getArena() != null) {
            ArenaRollbackManager.getInstance().recordBroken(
                    duel.getArena().getName(), event.getBlock());
        }
    }
}
