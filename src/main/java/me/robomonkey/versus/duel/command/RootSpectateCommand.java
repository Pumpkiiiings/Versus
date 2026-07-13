package me.robomonkey.versus.duel.command;

import me.robomonkey.versus.command.RootCommand;
import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.settings.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RootSpectateCommand extends RootCommand {

    public RootSpectateCommand() {
        super("spectate", "versus.spectate");
        setUsage("/spectate <player>");
        setDescription("Spectates a player in their current duel.");
        setAutonomous(true);
        setPermissionRequired(Settings.is(Setting.PERMISSION_REQUIRED_TO_DUEL));
        setPlayersOnly(true);
        setArgumentRequired(true);
        setMaxArguments(1);
        addBranches(new SpectateLeaveCommand());
        enforcePermissionRulesForChildren();
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            error(sender, Settings.getMessage(Setting.ERROR_SPECIFY_PLAYER_SPECTATE));
            return;
        }
        String playerName = args[0];
        Player selectedPlayer = Bukkit.getPlayer(playerName);
        if (DuelManager.getInstance().isDueling(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_SPECTATE_RIGHT_NOW));
            return;
        }
        if (selectedPlayer == null) {
            error(sender, Settings.getMessage(Setting.ERROR_PLAYER_NOT_ONLINE, Placeholder.of("%player%", playerName)));
            return;
        }
        if (selectedPlayer.equals(player)) {
            error(sender, Settings.getMessage(Setting.ERROR_CANNOT_SPECTATE_SELF));
            return;
        }
        if (!DuelManager.getInstance().isDueling(selectedPlayer)) {
            error(sender, Settings.getMessage(Setting.ERROR_PLAYER_NOT_DUELING, Placeholder.of("%player%", playerName)));
            return;
        }
        Duel duel = DuelManager.getInstance().getDuel(selectedPlayer);
        duel.spectate(player);
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}
