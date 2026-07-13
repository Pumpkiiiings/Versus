package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SpectateLeaveCommand extends AbstractCommand {

    public SpectateLeaveCommand() {
        super("leave", "versus.spectate");
        setPlayersOnly(true);
        setArgumentRequired(false);
        setDescription("Leaves the current spectating session.");
        setUsage("/spectate leave");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        DuelManager duelManager = DuelManager.getInstance();

        if (!duelManager.isSpectating(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_NOT_SPECTATING));
            return;
        }

        duelManager.removeSpectator(player);
        player.sendMessage(Settings.getMessage(Setting.LEFT_SPECTATING_MESSAGE));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
