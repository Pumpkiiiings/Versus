package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.config.model.Placeholder;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.manager.RequestManager;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class DenyCommand extends AbstractCommand {

    public DenyCommand() {
        super("deny", "versus.duel");
        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(true);
        setUsage("/duel deny <player>");
        setDescription("Denies a player's request to duel.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        RequestManager requestManager = RequestManager.getInstance();
        if (!requestManager.hasIncomingRequest(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_NO_REQUEST));
            return;
        }
        if (args.length < 1) {
            error(sender, Settings.getMessage(Setting.ERROR_SPECIFY_PLAYER_DENY));
            return;
        }
        String playername = args[0];
        Player requester = Bukkit.getPlayer(playername);
        if (requester == null) {
            error(sender, Settings.getMessage(Setting.ERROR_PLAYER_OFFLINE, new me.robomonkey.versus.config.model.Placeholder("%player%", playername)));
            return;
        }
        if (requester.equals(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_DUEL_SELF));
            return;
        }
        if (requestManager.getRequest(player, requester) == null) {
            error(sender, Settings.getMessage(Setting.ERROR_NO_REQUEST));
            return;
        }
        requestManager.denyRequest(player, requester);
    }


    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers()
                .stream()
                .map(player -> player.getName())
                .collect(Collectors.toList());
    }
}
