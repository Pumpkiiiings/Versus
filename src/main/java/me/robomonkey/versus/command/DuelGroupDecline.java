package me.robomonkey.versus.command;

import me.robomonkey.versus.party.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupDecline extends AbstractCommand {
    public DuelGroupDecline() {
        super("decline", "versus.duel");
        this.playersOnly = true;
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PartyManager pm = PartyManager.getInstance();
        
        Player inviter = null;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (pm.hasInviteFrom(player, online)) {
                inviter = online;
                break;
            }
        }
        
        if (inviter == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NO_INVITES)));
            return;
        }
        
        pm.removeInvite(player, inviter);
        player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_INVITE_DECLINED)));
        inviter.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_INVITE_DECLINED_OTHER).replace("%player%", player.getName())));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
