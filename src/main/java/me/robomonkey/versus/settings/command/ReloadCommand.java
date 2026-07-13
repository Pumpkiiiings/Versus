package me.robomonkey.versus.settings.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand extends AbstractCommand {

    public ReloadCommand() {
        super("reload", null);
        setUsage("/versus config reload");
        setDescription("Reloads all changes from the config file.");
        setArgumentRequired(false);
        setAutonomous(true);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        sender.sendMessage(Settings.getMessage(Setting.ADMIN_RELOADING_CONFIG));
        Settings.getInstance().reloadConfig(() -> {
            me.robomonkey.versus.util.MenuManager.getInstance().reloadMenus();
            me.robomonkey.versus.kit.KitManager.getInstance().reload();
            me.robomonkey.versus.arena.ArenaManager.getInstance().loadArenas();
            sender.sendMessage(Settings.getMessage(Setting.ADMIN_RELOADED_CONFIG));
        });
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
