package me.robomonkey.versus.command;

import me.robomonkey.versus.party.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupCreate extends AbstractCommand {
    public DuelGroupCreate() {
        super("create", "versus.duel");
        this.playersOnly = true;
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if (PartyManager.getInstance().hasParty(player)) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_ALREADY_IN)));
            return;
        }
        PartyManager.getInstance().createParty(player);
        player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_CREATED)));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
