package me.robomonkey.versus.listener.combat;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class EnderPearlListener implements Listener {

    private final HashMap<UUID, Long> pearlCooldowns = new HashMap<>();

    @EventHandler
    public void onPearlThrow(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null || item.getType() != Material.ENDER_PEARL) return;
        if (!DuelManager.getInstance().isDueling(player)) return;
        
        int cooldownSeconds = 10;
        try {
            cooldownSeconds = Settings.getNumber(Setting.ENDER_PEARL_COOLDOWN);
        } catch (Exception ignored) {}
        
        if (cooldownSeconds <= 0) return;
        
        long now = System.currentTimeMillis();
        long cooldownMillis = cooldownSeconds * 1000L;
        
        if (pearlCooldowns.containsKey(player.getUniqueId())) {
            long timePassed = now - pearlCooldowns.get(player.getUniqueId());
            if (timePassed < cooldownMillis) {
                event.setCancelled(true);
                long secondsLeft = (cooldownMillis - timePassed) / 1000L;
                if (secondsLeft == 0) secondsLeft = 1; // display at least 1 second
                player.sendMessage(MessageUtil.color("&cYou must wait " + secondsLeft + " seconds before using another Ender Pearl."));
                // Update inventory to prevent visual glitch
                player.updateInventory();
                return;
            }
        }
        
        pearlCooldowns.put(player.getUniqueId(), now);
    }
}
