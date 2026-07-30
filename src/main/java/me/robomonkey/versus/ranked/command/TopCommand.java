package me.robomonkey.versus.ranked.command;

import me.robomonkey.versus.arena.gui.TopMenu;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.kit.manager.KitManager;
import me.robomonkey.versus.kit.model.Kit;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class TopCommand extends AbstractCommand {

    public TopCommand() {
        super("top", "versus.ranked.top");
        setPlayersOnly(true);
        setUsage("/ranked top <kit>");
        setDescription("Opens the Top 10 ELO leaderboard for a kit.");
        setMaxArguments(1);
        setMinArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        String kitName = args[0];

        Kit kit = KitManager.getInstance().getKit(kitName);
        if (kit == null) {
            player.sendMessage(MessageUtil.color("&cKit not found."));
            return;
        }

        new TopMenu(player, kit).open();
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return KitManager.getInstance().getAllKits().stream()
                    .map(Kit::getName)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
