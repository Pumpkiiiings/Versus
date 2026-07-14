package me.robomonkey.versus.duel.command;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.command.RootCommand;

import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Setting;

import me.robomonkey.versus.party.model.Party;
import me.robomonkey.versus.party.manager.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import java.util.List;

public class DuelGroupAccept extends AbstractCommand {
    public DuelGroupAccept() {
        super("accept", "versus.duel");
        this.playersOnly = true;
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PartyManager pm = PartyManager.getInstance();
        
        if (pm.hasParty(player)) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_ALREADY_IN)));
            return;
        }
        
        // Find who invited them
        // If there are multiple, they need to specify. Let's just find the first one for simplicity
        Player inviter = null;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (pm.hasInviteFrom(player, online)) {
                inviter = online;
                break;
            }
        }
        
        if (inviter == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_NO_INVITES)));
            return;
        }
        
        Party party = pm.getParty(inviter);
        if (party == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_DOES_NOT_EXIST)));
            pm.removeInvite(player, inviter);
            return;
        }
        
        pm.removeInvite(player, inviter);
        pm.addPlayerToParty(player, party);
        
        party.broadcast(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_PLAYER_JOINED).replace("%player%", player.getName()));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
