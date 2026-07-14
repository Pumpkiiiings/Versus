package me.robomonkey.versus.arena.command;

import me.robomonkey.versus.arena.editor.ArenaBuilderCoordinator;

import me.robomonkey.versus.arena.model.ArenaProperty;
import me.robomonkey.versus.arena.editor.ArenaBuilder;
import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.arena.editor.ArenaEditor;
import me.robomonkey.versus.arena.model.Arena;


import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetCommand extends AbstractCommand {

    public SetCommand() {
        super("set", "versus.arena.set");
        setUsage("/arena set <arenaname> <setting_name>");
        setDescription("Changes a specific setting in existing arena.");
        setPlayersOnly(true);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(getUsage());
            return;
        }
        Player player = (Player) sender;
        String property = args[1];
        ArenaProperty propertyFromString = ArenaProperty.fromString(property);
        if (propertyFromString == null) {
            error(sender, Settings.getMessage(Setting.ERROR_NO_ARENA_PROPERTY));
            return;
        }
        if (ArenaBuilderCoordinator.getInstance().hasArenaBuilder(player)) {
            ArenaBuilder builder = ArenaBuilderCoordinator.getInstance().getArenaBuilder(player);
            builder.handleArenaEdit(propertyFromString);
        } else {
            editWithoutBuilder(propertyFromString, args, player);
        }
    }

    private void editWithoutBuilder(ArenaProperty property, String[] args, Player player) {
        String arenaName = args[0];
        ArenaManager arenaManager = ArenaManager.getInstance();
        Arena arenaFromString = arenaManager.getArena(arenaName);

        if (arenaFromString == null) {
            error(player, Settings.getMessage(Setting.ERROR_ARENA_NOT_EXIST, Placeholder.of("%arena%", arenaName)));
            return;
        }

        ArenaEditor.changeArenaProperty(arenaFromString, property, player);
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> arenaNames = ArenaManager.getInstance().getAllArenas()
                    .stream().map(arena -> arena.getName())
                    .collect(Collectors.toList());
            return arenaNames;
        }
        if (args.length == 2) {
            List<String> propertyNames = Arrays.stream(ArenaProperty.values()).map(property -> property.toString()).collect(Collectors.toList());
            return propertyNames;
        }
        return null;
    }
}
