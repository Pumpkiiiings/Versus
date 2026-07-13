package me.robomonkey.versus.command;

import me.robomonkey.versus.party.Party;
import me.robomonkey.versus.party.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupInvite extends AbstractCommand {
    public DuelGroupInvite() {
        super("invite", "versus.duel");
        this.playersOnly = true;
        this.setMinArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PartyManager pm = PartyManager.getInstance();
        Party party = pm.getParty(player);
        
        if (party == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NOT_IN_CREATE)));
            return;
        }
        
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NOT_LEADER_INVITE)));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ERROR_PLAYER_OFFLINE).replace("%player%", args[0])));
            return;
        }
        
        if (pm.hasParty(target)) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_TARGET_ALREADY_IN)));
            return;
        }
        
        if (pm.hasInviteFrom(target, player)) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_ALREADY_INVITED)));
            return;
        }
        
        pm.invitePlayer(player, target);
        player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_INVITE_SENT).replace("%player%", target.getName())));
        target.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_INVITE_RECEIVED).replace("%player%", player.getName())));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
