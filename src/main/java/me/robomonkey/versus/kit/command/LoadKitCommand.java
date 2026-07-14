package me.robomonkey.versus.kit.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.kit.model.Kit;
import me.robomonkey.versus.kit.manager.KitManager;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class LoadKitCommand extends AbstractCommand {
    KitManager kitManager = KitManager.getInstance();

    public LoadKitCommand() {
        super("loadkit", "versus.kit.load");
        setUsage("/arena loadkit <name>");
        setDescription("Loads a kit into player inventory.");
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (args.length < 1) {
            error(sender, Settings.getMessage(Setting.ERROR_SPECIFY_KIT_TO_LOAD));
            return;
        }
        String kitName = args[0];
        if (kitManager.contains(kitName)) {
            ItemStack[] contents = kitManager.getKit(kitName).getItems();
            player.getInventory().setContents(contents);
            sender.sendMessage(Settings.getMessage(Setting.KIT_LOADED, Placeholder.of("%kit%", kitName)));
        } else {
            error(sender, Settings.getMessage(Setting.ERROR_KIT_NOT_EXIST, Placeholder.of("%kit%", kitName)));
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return kitManager.getAllKits().stream().map(Kit::getName).collect(Collectors.toList());
    }
}
