package me.robomonkey.versus.settings.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import org.bukkit.command.CommandSender;

import java.util.List;

public class SaveCommand extends AbstractCommand {
    public SaveCommand() {
        super("save", null);
        setUsage("/versus config save");
        setDescription("Saves all changes made to config.");
        setArgumentRequired(false);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        sender.sendMessage(Settings.getMessage(Setting.ADMIN_SAVING_CONFIG));
        Settings.getInstance().saveSettingsChanges(allChanged -> {
            if (allChanged == null) {
                error(sender, Settings.getMessage(Setting.ERROR_SAVE_CONFIG));
                return;
            }
            sender.sendMessage(Settings.getMessage(Setting.ADMIN_SAVED_CONFIG));
            allChanged.forEach(setting -> sender
                    .sendMessage(Settings.getMessage(Setting.ADMIN_SAVED_CONFIG_LIST_ITEM) + setting.toString().toLowerCase()));
        });
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
