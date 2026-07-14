package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.duel.manager.RequestManager;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.model.Request;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ConfirmCommand extends AbstractCommand {

    public ConfirmCommand() {
        super("confirm", "versus.duel");
        setArgumentRequired(false);
        setUsage("/duel confirm");
        setPermissionRequired(false);
        setPlayersOnly(true);
        setDescription("Confirms a pending duel request.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        RequestManager requestManager = RequestManager.getInstance();
        
        Request pending = requestManager.getPendingConfirmation(player);
        if (pending == null) {
            error(sender, Settings.getMessage(Setting.ERROR_NO_PENDING_BET));
            return;
        }
        
        // Remove from pending and send the actual request
        requestManager.removePendingConfirmation(player);
        requestManager.sendRequest(pending.getRequestingPlayer(), pending.getRequestedPlayer(), pending.getBetAmount(), pending.getRequestedArena(), pending.getRequestedKit());
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
