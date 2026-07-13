package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.DuelState;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import java.util.List;

public class LeaveCommand extends AbstractCommand {

    public LeaveCommand() {
        super("leave", "versus.duel");
        setPlayersOnly(true);
        setDescription("Leaves the current duel or spectating session.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        DuelManager duelManager = DuelManager.getInstance();
        
        if (duelManager.isSpectating(player)) {
            duelManager.removeSpectator(player);
            player.sendMessage(Settings.getMessage(Setting.LEFT_SPECTATING_MESSAGE));
            return;
        }

        if (!duelManager.isDueling(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_NOT_DUELING));
            return;
        }

        Duel duel = duelManager.getDuel(player);
        if (duel.getState() == DuelState.ENDED) {
            // Already ended, no need to forfeit
            error(sender, Settings.getMessage(Setting.ERROR_DUEL_ALREADY_ENDED));
            return;
        }

        if (player.getGameMode() == GameMode.SPECTATOR) {
            // Player is already dead and spectating their own team
            error(sender, Settings.getMessage(Setting.ERROR_ALREADY_DEAD));
            return;
        }

        // Surrender / forfeit
        player.sendMessage(Settings.getMessage(Setting.DUEL_FORFEIT_MESSAGE));
        duelManager.registerDuelistDeath(player, true);
    }
    
    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
