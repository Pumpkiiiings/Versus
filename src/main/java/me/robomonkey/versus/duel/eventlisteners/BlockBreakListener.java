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
