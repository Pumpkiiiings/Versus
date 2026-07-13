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

public class DuelGroupDuel extends AbstractCommand {
    public DuelGroupDuel() {
        super("duel", "versus.duel");
        this.playersOnly = true;
        this.setMinArguments(1);
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
        
        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_NOT_LEADER_DUEL)));
            return;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ERROR_PLAYER_OFFLINE).replace("%player%", args[0])));
            return;
        }
        
        Party targetParty = pm.getParty(target);
        if (targetParty == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_TARGET_NOT_IN)));
            return;
        }
        
        if (!targetParty.isLeader(target.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_MUST_DUEL_LEADER)));
            return;
        }
        
        if (party.getLeader().equals(targetParty.getLeader())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_CANNOT_DUEL_SELF)));
            return;
        }
        
        // Setup request
        double betAmount = 0.0;
        if (args.length > 1) {
            try {
                betAmount = Double.parseDouble(args[1]);
                if (betAmount < 0) {
                    player.sendMessage(MessageUtil.color(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ERROR_NEGATIVE_BET)));
                    return;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(MessageUtil.color("&cInvalid bet amount."));
                return;
            }
        }
        
        Request req = new Request(target, player, betAmount, null, null);
        req.setGroup(true);
        RequestManager.getInstance().placeInQueue(req);
        
        party.broadcast(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_DUEL_CHALLENGE_SENT).replace("%player%", target.getName()));
        targetParty.broadcast(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.PARTY_DUEL_CHALLENGE_RECEIVED).replace("%player%", player.getName()));
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
