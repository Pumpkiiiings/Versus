package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SetSpawnCommand extends AbstractCommand {

    public SetSpawnCommand() {
        super("setspawn", "versus.arena.setspawn");
        setArgumentRequired(false);
        setUsage("/arena setspawn");
        setPermissionRequired(true);
        setPlayersOnly(true);
        setDescription("Sets the custom spawn location for returning after a duel.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String locStr = player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ() + " " + player.getLocation().getWorld().getName();
        
        Settings settings = Settings.getInstance();
        settings.changeSetting(Setting.WINNER_RETURN_LOCATION, locStr);
        settings.changeSetting(Setting.LOSER_RETURN_LOCATION, locStr);
        settings.changeSetting(Setting.RETURN_WINNERS, "custom");
        settings.changeSetting(Setting.RETURN_LOSERS, "custom");
        
        settings.saveSettingsChanges(changed -> {
            sender.sendMessage(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ARENA_SPAWN_SET));
        });
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
