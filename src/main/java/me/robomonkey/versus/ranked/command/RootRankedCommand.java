package me.robomonkey.versus.ranked.command;

import me.robomonkey.versus.command.RootCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RootRankedCommand extends RootCommand {

    public RootRankedCommand() {
        super("ranked", "versus.ranked");
        addBranches(
                new QueueCommand(),
                new LeaveCommand(),
                new TopCommand()
        );
        setArgumentRequired(true);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        sender.sendMessage(getUsage());
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
