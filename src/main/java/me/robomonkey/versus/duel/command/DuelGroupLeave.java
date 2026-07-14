package me.robomonkey.versus.duel.command;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.command.RootCommand;

import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Setting;

import me.robomonkey.versus.party.model.Party;
import me.robomonkey.versus.party.manager.PartyManager;
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
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_NOT_IN)));
            return;
        }
        
        if (party.isLeader(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_LEADER_MUST_DISBAND)));
            return;
        }
        
        party.broadcast(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_PLAYER_LEFT).replace("%player%", player.getName()));
        pm.removePlayerFromParty(player);
        player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_LEFT)));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
