package me.robomonkey.versus.cosmetic.command;

import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.cosmetic.gui.CosmeticsMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CosmeticsCommand extends AbstractCommand {

    public CosmeticsCommand() {
        super("cosmetics", null);
        setArgumentRequired(false);
        setPlayersOnly(true);
        setUsage("/duel cosmetics");
        setDescription("Abre el menú de cosméticos.");
        setAutonomous(true);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        new CosmeticsMenu(player).open();
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return List.of();
    }
}
