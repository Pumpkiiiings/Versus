package me.robomonkey.versus.ranked.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.queue.gui.RankedMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class QueueCommand extends AbstractCommand {

    public QueueCommand() {
        super("queue", "versus.ranked.queue");
        setPlayersOnly(true);
        setUsage("/ranked queue");
        setDescription("Opens the Ranked matchmaking menu.");
        setMaxArguments(0);
        setArgumentRequired(false);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        new RankedMenu((Player) sender).open();
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
