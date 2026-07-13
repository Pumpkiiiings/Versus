package me.robomonkey.versus.command;

import me.robomonkey.versus.duel.request.Request;
import me.robomonkey.versus.duel.request.RequestManager;
import me.robomonkey.versus.party.Party;
import me.robomonkey.versus.party.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupDeclineDuel extends AbstractCommand {
    public DuelGroupDeclineDuel() {
        super("declineduel", "versus.duel");
        this.playersOnly = true;
        this.setMinArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PartyManager pm = PartyManager.getInstance();
        Party party = pm.getParty(player);
        
        if (party == null || !party.isLeader(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NOT_LEADER_DECLINE_DUEL)));
            return;
        }
        
        Player requesting = Bukkit.getPlayer(args[0]);
        if (requesting == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ERROR_PLAYER_OFFLINE).replace("%player%", args[0])));
            return;
        }
        
        RequestManager rm = RequestManager.getInstance();
        Request req = rm.getRequest(player, requesting);
        
        if (req == null || !req.isGroup()) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NO_DUEL_REQUEST)));
            return;
        }
        
        rm.removeRequest(player, requesting);
        party.broadcast(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_DUEL_DECLINED).replace("%player%", requesting.getName()));
        
        Party targetParty = pm.getParty(requesting);
        if (targetParty != null) {
            targetParty.broadcast(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_DUEL_DECLINED_OTHER).replace("%player%", player.getName()));
        }
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
