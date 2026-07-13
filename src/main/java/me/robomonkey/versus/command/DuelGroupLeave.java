package me.robomonkey.versus.command;

import me.robomonkey.versus.party.Party;
import me.robomonkey.versus.party.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupLeave extends AbstractCommand {
    public DuelGroupLeave() {
        super("leave", "versus.duel");
        this.playersOnly = true;
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PartyManager pm = PartyManager.getInstance();
        Party party = pm.getParty(player);
        
        if (party == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NOT_IN)));
            return;
        }
        
        if (party.isLeader(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_LEADER_MUST_DISBAND)));
            return;
        }
        
        party.broadcast(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_PLAYER_LEFT).replace("%player%", player.getName()));
        pm.removePlayerFromParty(player);
        player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_LEFT)));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
