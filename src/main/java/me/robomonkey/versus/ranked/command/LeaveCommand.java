package me.robomonkey.versus.ranked.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.queue.QueueManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LeaveCommand extends AbstractCommand {

    public LeaveCommand() {
        super("leave", "versus.ranked.leave");
        setPlayersOnly(true);
        setUsage("/ranked leave");
        setDescription("Leaves the Ranked matchmaking queue.");
        setMaxArguments(0);
        setArgumentRequired(false);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (!QueueManager.getInstance().isInQueue(player)) {
            player.sendMessage(me.robomonkey.versus.util.MessageUtil.color("&cYou are not in the queue."));
            return;
        }
        QueueManager.getInstance().removePlayer(player);
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
