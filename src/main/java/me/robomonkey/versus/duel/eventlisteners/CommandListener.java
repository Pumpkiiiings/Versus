package me.robomonkey.versus.duel.eventlisteners;

import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandListener implements Listener {

    public static String BYPASS_BLOCKED_COMMANDS = "versus.bypass";

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (DuelManager.getInstance().isDueling(event.getPlayer())) {
            if (event.getPlayer().hasPermission(BYPASS_BLOCKED_COMMANDS)) return;

            boolean whitelistMode = Settings.getInstance().getConfig().getBoolean("dueling.mechanics.whitelist_commands_enabled", false);
            
            if (whitelistMode) {
                // Whitelist mode: cancel unless the command is explicitly allowed
                boolean isWhitelisted = Settings.getInstance().getConfig().getStringList("dueling.mechanics.whitelisted_commands")
                        .stream().anyMatch(command -> event.getMessage().toLowerCase().startsWith("/" + command.toLowerCase()));
                
                if (!isWhitelisted) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Settings.getMessage(Setting.ERROR_COMMAND_BLOCKED_DUEL));
                }
            } else {
                // Blacklist mode (legacy): cancel if command is in the blocked list
                if (Settings.getInstance().getStringList(Setting.BLOCKED_COMMANDS).stream()
                        .anyMatch(command -> event.getMessage().contains(command))) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(Settings.getMessage(Setting.ERROR_COMMAND_BLOCKED_SPECTATE));
                }
            }
        }
    }
}
