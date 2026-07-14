package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.arena.model.Arena;
import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class VisitCommand extends AbstractCommand {


    public VisitCommand() {
        super("visit", "versus.arena.visit");
        setUsage("/arena visit <arenaname>");
        setDescription("Visit an existing arena.");
        setPlayersOnly(true);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            error(sender, Settings.getMessage(Setting.ERROR_PROVIDE_ARENA));
            return;
        }
        String arenaName = args[0];
        Arena target = ArenaManager.getInstance().getArena(arenaName);
        Player player = (Player) sender;
        if (target == null) {
            error(sender, Settings.getMessage(Setting.ERROR_ARENA_NOT_EXIST, Placeholder.of("%arena%", arenaName)));
            return;
        }
        player.teleport(target.getCenterLocation());
        player.sendMessage(Settings.getMessage(Setting.ARENA_TELEPORTED, Placeholder.of("%arena%", arenaName)));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> arenaNames = ArenaManager.getInstance().getAllArenas()
                    .stream().map(arena -> arena.getName())
                    .collect(Collectors.toList());
            return arenaNames;
        }
        return null;
    }
}
