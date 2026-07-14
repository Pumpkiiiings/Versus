package me.robomonkey.versus.config.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetCommand extends AbstractCommand {
    public SetCommand() {
        super("set", null);
        setUsage("/versus config set <name> <value>");
        setDescription("Changes a config setting named 'name' to 'value'.");
        setMinArguments(2);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        String settingName = args[0].toUpperCase();
        if (!Settings.isSetting(settingName)) {
            error(sender, Settings.getMessage(Setting.ERROR_INVALID_SETTING, Placeholder.of("%setting%", settingName)));
            return;
        }
        Setting setting = Setting.valueOf(settingName);
        String option = buildArgs(args, 1, args.length);
        Object converted = Settings.tryConvertFromString(option, setting);
        if (converted == null) {
            error(sender, Settings.getMessage(Setting.ERROR_INVALID_SETTING_VALUE, 
                    Placeholder.of("%option%", option), 
                    Placeholder.of("%setting%", setting.toString()), 
                    Placeholder.of("%type%", setting.type.toString())));
            return;
        }
        Settings.getInstance().changeSetting(setting, converted);
        sender.sendMessage(Settings.getMessage(Setting.ADMIN_SET_CONFIG, 
                Placeholder.of("%setting%", setting.toString()), 
                Placeholder.of("%option%", option)));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Setting.values())
                    .filter(setting -> setting.getType() != Setting.Type.INVALID)
                    .map(setting -> setting.toString().toLowerCase()).collect(Collectors.toList());
        } else {
            String settingName = args[0].toUpperCase();
            if (!Settings.isSetting(settingName)) return List.of();
            List<String> options = Setting.valueOf(settingName).getType().getOptions();
            return options;
        }
    }
}
