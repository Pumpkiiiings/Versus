package me.robomonkey.versus.duel.command;
import me.robomonkey.versus.command.AbstractCommand;
import me.robomonkey.versus.command.RootCommand;

import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.config.model.Setting;

import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.duel.model.Request;
import me.robomonkey.versus.duel.manager.RequestManager;
import me.robomonkey.versus.party.model.Party;
import me.robomonkey.versus.party.manager.PartyManager;
import me.robomonkey.versus.util.MessageUtil;
import me.robomonkey.versus.arena.model.Arena;
import me.robomonkey.versus.arena.manager.ArenaManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class DuelGroupAcceptDuel extends AbstractCommand {
    public DuelGroupAcceptDuel() {
        super("acceptduel", "versus.duel");
        this.playersOnly = true;
        this.setMinArguments(1);
    }

    @Override
    public void callCommand(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        PartyManager pm = PartyManager.getInstance();
        Party party = pm.getParty(player);
        
        if (party == null || !party.isLeader(player.getUniqueId())) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_NOT_LEADER_ACCEPT_DUEL)));
            return;
        }
        
        Player requesting = Bukkit.getPlayer(args[0]);
        if (requesting == null) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_PLAYER_OFFLINE).replace("%player%", args[0])));
            return;
        }
        
        RequestManager rm = RequestManager.getInstance();
        Request req = rm.getRequest(player, requesting);
        
        if (req == null || !req.isGroup()) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_NO_DUEL_REQUEST)));
            return;
        }
        
        Party targetParty = pm.getParty(requesting);
        if (targetParty == null) {
            player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_DOES_NOT_EXIST));
            return;
        }
        
        Arena arena = null;
        if (req.getRequestedArena() != null) {
            arena = ArenaManager.getInstance().getArena(req.getRequestedArena());
        }
        if (arena == null) {
            arena = ArenaManager.getInstance().getAvailableArena();
        }
        
        if (arena == null || !ArenaManager.getInstance().getAvailableArenas().contains(arena)) {
            player.sendMessage(MessageUtil.color(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.NO_ARENAS_AVAILABLE)));
            rm.placeInQueue(req);
            return;
        }
        
        if (req.getBetAmount() > 0) {
            for (Player p : party.getOnlinePlayers()) {
                if (!me.robomonkey.versus.dependency.EconomyManager.has(p, req.getBetAmount())) {
                    party.broadcast(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_NOT_ENOUGH_MONEY).replace("%player%", p.getName()));
                    targetParty.broadcast(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.PARTY_OTHER_NOT_ENOUGH_MONEY));
                    return;
                }
            }
            for (Player p : targetParty.getOnlinePlayers()) {
                if (!me.robomonkey.versus.dependency.EconomyManager.has(p, req.getBetAmount())) {
                    targetParty.broadcast("&c" + p.getName() + " does not have enough money for the bet.");
                    party.broadcast("&cThe duel was cancelled because a player in the other party didn't have enough money.");
                    return;
                }
            }
            
            for (Player p : party.getOnlinePlayers()) {
                me.robomonkey.versus.dependency.EconomyManager.withdraw(p, req.getBetAmount());
            }
            for (Player p : targetParty.getOnlinePlayers()) {
                me.robomonkey.versus.dependency.EconomyManager.withdraw(p, req.getBetAmount());
            }
        }
        
        rm.cancelQueue(player);
        DuelManager.getInstance().setupGroupDuel(party.getOnlinePlayers(), targetParty.getOnlinePlayers(), arena, req.getBetAmount(), req.getRequestedKit());
    }

    @Override
    public List<String> callCompletionsUpdate(CommandSender sender, String[] args) {
        return null;
    }
}
