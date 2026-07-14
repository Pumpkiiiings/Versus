package me.robomonkey.versus.command;

import me.robomonkey.versus.command.RootCommand;
import me.robomonkey.versus.config.command.ConfigCommand;
import me.robomonkey.versus.config.command.SupportCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

public class RootVersusCommand extends RootCommand {

    public RootVersusCommand() {
        super("versus", "versus.admin");
        addBranches(new ConfigCommand(), new SupportCommand());
        setPermissionRequired(true);
        enforcePermissionForChildren();
        enforcePermissionRulesForChildren();
        setPlayersOnly(true);
        setMaxArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {

    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
