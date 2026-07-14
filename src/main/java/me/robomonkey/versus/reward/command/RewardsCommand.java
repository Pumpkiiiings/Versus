package me.robomonkey.versus.reward.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.reward.gui.RewardsGUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * /duel rewards — Opens the rewards claim GUI for the executing player.
 */
public class RewardsCommand extends AbstractCommand {

    public RewardsCommand() {
        super("rewards", "versus.duel");
        setPlayersOnly(true);
        setArgumentRequired(false);
        setUsage("/duel rewards");
        setDescription("Abre el menú para reclamar tus recompensas de duelo.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        RewardsGUI.open(player);
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
