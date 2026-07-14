package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.arena.model.Arena;
import me.robomonkey.versus.arena.editor.ArenaEditor;
import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class EditCommand extends AbstractCommand {

    public EditCommand() {
        super("edit", "versus.arena.edit");
        setPlayersOnly(true);
        setUsage("/arena edit <arenaname>");
        setDescription("Opens a menu to edit an existing arena.");
        setMaxArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        String arenaName = args[0];
        Player player = (Player) sender;
        ArenaManager arenaManager = ArenaManager.getInstance();
        Arena fromString = arenaManager.getArena(arenaName);
        if (fromString == null) {
            error(sender, Settings.getMessage(Setting.ERROR_ARENA_NOT_EXIST, Placeholder.of("%arena%", arenaName)));
        } else {
            ArenaEditor.openEditingMenu(player, fromString);
        }
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
