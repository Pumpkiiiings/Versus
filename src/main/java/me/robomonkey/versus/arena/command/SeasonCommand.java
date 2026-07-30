package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.season.manager.SeasonManager;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SeasonCommand extends AbstractCommand {

    public SeasonCommand() {
        super("season", "versus.admin");
        setPlayersOnly(false);
        setUsage("/arena season end");
        setDescription("Manages Competitive Seasons.");
        setMinArguments(1);
        setMaxArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        if (args[0].equalsIgnoreCase("end")) {
            SeasonManager.getInstance().endSeason(sender);
        } else {
            sender.sendMessage(me.robomonkey.versus.util.MessageUtil.color("&cUsage: /arena season end"));
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return List.of("end");
        }
        return new ArrayList<>();
    }
}
