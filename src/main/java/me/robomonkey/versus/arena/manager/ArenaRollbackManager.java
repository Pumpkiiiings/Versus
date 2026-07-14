package me.robomonkey.versus.arena.manager;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import java.util.*;

/**
 * Tracks blocks placed or broken during a duel so that the arena
 * can be restored to its original state when the duel ends.
 *
 * <p>Usage:
 * <ol>
 *   <li>Call {@link #recordPlaced(String, Block)} when a block is placed.</li>
 *   <li>Call {@link #recordBroken(String, Block)} BEFORE the break event removes the block.</li>
 *   <li>Call {@link #rollback(String)} when the duel finishes.</li>
 * </ol>
 */
public class ArenaRollbackManager {

    private static ArenaRollbackManager instance;

    /** arena name → list of restoration actions */
    private final Map<String, List<RollbackEntry>> pending = new HashMap<>();

    public static ArenaRollbackManager getInstance() {
        if (instance == null) {
            instance = new ArenaRollbackManager();
        }
        return instance;
    }

    /**
     * Called AFTER a block has been placed during a duel.
     * We need to restore it to AIR later.
     */
    public void recordPlaced(String arenaName, Block block) {
        pending.computeIfAbsent(arenaName, k -> new ArrayList<>())
               .add(new RollbackEntry(block.getLocation().clone(), Material.AIR, null));
    }

    /**
     * Called BEFORE a block is broken during a duel so we can snapshot its state.
     * We need to restore the original block later.
     */
    public void recordBroken(String arenaName, Block block) {
        pending.computeIfAbsent(arenaName, k -> new ArrayList<>())
               .add(new RollbackEntry(block.getLocation().clone(), block.getType(), block.getBlockData().clone()));
    }

    /**
     * Restores all blocks changed during the duel and clears the record.
     */
    public void rollback(String arenaName) {
        List<RollbackEntry> entries = pending.remove(arenaName);
        if (entries == null) return;

        // Iterate in reverse so stacked placed blocks are removed top-down
        ListIterator<RollbackEntry> it = entries.listIterator(entries.size());
        while (it.hasPrevious()) {
            RollbackEntry entry = it.previous();
            Block block = entry.location.getWorld().getBlockAt(entry.location);
            if (entry.originalType == Material.AIR) {
                // This was a placed block — remove it
                block.setType(Material.AIR, false);
            } else {
                // This was a broken block — restore it
                block.setType(entry.originalType, false);
                if (entry.blockData != null) {
                    block.setBlockData(entry.blockData, false);
                }
            }
        }
    }

    /** Simple immutable snapshot of a block state. */
    private static class RollbackEntry {
        final org.bukkit.Location location;
        final Material originalType;
        final BlockData blockData;

        RollbackEntry(org.bukkit.Location location, Material originalType, BlockData blockData) {
            this.location  = location;
            this.originalType = originalType;
            this.blockData = blockData;
        }
    }
}
