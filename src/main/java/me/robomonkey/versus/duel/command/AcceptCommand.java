package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AcceptCommand extends AbstractCommand {

    public AcceptCommand() {
        super("accept", "versus.duel");
        setUsage("/duel accept");
        setPlayersOnly(true);
        setPermissionRequired(false);
        setArgumentRequired(false);
        setDescription("Accepts the most recent request to duel.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        RequestManager requestManager = RequestManager.getInstance();
        if (DuelManager.getInstance().isDueling(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_DUEL_RIGHT_NOW));
            return;
        }
        if (!requestManager.hasIncomingRequest(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_NO_REQUEST));
            return;
        }
        try {
            requestManager.acceptRequest(player);
        } catch (RequestManager.PlayerOfflineException e) {
            error(player, Settings.getMessage(Setting.ERROR_REQUESTER_OFFLINE));
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}

