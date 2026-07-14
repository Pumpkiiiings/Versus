package me.robomonkey.versus.kit.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.kit.manager.KitManager;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class SaveKitCommand extends AbstractCommand {
    public SaveKitCommand() {
        super("savekit", "versus.kit.save");
        setPlayersOnly(true);
        setStaticTabComplete(true);
        this.setMaxArguments(1);
        this.setArgumentRequired(true);
        this.addTabCompletion("<kit-name>");
        setUsage("/arena savekit <name>");
        setDescription("Saves a kit with a given name.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            error(sender, Settings.getMessage(Setting.ERROR_PROVIDE_KIT));
            return;
        }

        String kitName = args[0];
        if (KitManager.getInstance().isKit(kitName)) {
            error(sender, Settings.getMessage(Setting.ERROR_KIT_ALREADY_EXISTS, Placeholder.of("%kit%", kitName)));
            return;
        }
        KitManager.getInstance().add(kitName, player.getInventory().getContents());
        sender.sendMessage(Settings.getMessage(Setting.KIT_SAVED, Placeholder.of("%kit%", kitName)));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
