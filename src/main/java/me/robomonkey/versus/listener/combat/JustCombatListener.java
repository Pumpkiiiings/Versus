package me.robomonkey.versus.listener.combat;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.duel.manager.DuelManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;

public class JustCombatListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        handleJustCombatBypass(event.getPlayer(), event.getItem());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            handleJustCombatBypass(event.getPlayer(), event.getItem());
        }
    }

    private void handleJustCombatBypass(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) return;
        if (!DuelManager.getInstance().isDueling(player)) return;

        // We run a task 1 tick later to remove the vanilla cooldown that JustCombat might have added
        // and also clear the JustCombat internal cooldowns, allowing normal use during a duel.
        Bukkit.getScheduler().runTaskLater(Versus.getInstance(), () -> {
            if (player.isOnline()) {
                // Clear vanilla cooldown just in case JustCombat added it
                player.setCooldown(item.getType(), 0);

                // Clear JustCombat internal cooldowns via Reflection to avoid hard dependency
                Plugin justCombatPlugin = Bukkit.getPluginManager().getPlugin("JustCombat");
                if (justCombatPlugin != null) {
                    try {
                        Method getCooldownManager = justCombatPlugin.getClass().getMethod("getCooldownManager");
                        Object cooldownManager = getCooldownManager.invoke(justCombatPlugin);
                        Method clearCooldowns = cooldownManager.getClass().getMethod("clearCooldowns", UUID.class);
                        clearCooldowns.invoke(cooldownManager, player.getUniqueId());
                    } catch (Exception ignored) {
                        // Ignore if method not found or plugin changed
                    }
                }
            }
        }, 1L);
    }

    public static void untagPlayer(Player player) {
        Plugin justCombatPlugin = Bukkit.getPluginManager().getPlugin("JustCombat");
        if (justCombatPlugin != null) {
            try {
                Method getCombatManager = justCombatPlugin.getClass().getMethod("getCombatManager");
                Object combatManager = getCombatManager.invoke(justCombatPlugin);
                Method untagPlayer = combatManager.getClass().getMethod("untagPlayer", Player.class);
                untagPlayer.invoke(combatManager, player);
            } catch (Exception ignored) {
                // Ignore
            }
        }
    }
}
