package me.robomonkey.versus.party;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PartyManager {
    private static PartyManager instance;
    private final HashMap<UUID, Party> partyMap = new HashMap<>();
    private final HashMap<UUID, List<UUID>> partyInvites = new HashMap<>();

    public static PartyManager getInstance() {
        if (instance == null) {
            instance = new PartyManager();
        }
        return instance;
    }

    public boolean hasParty(Player player) {
        return partyMap.containsKey(player.getUniqueId());
    }

    public Party getParty(Player player) {
        return partyMap.get(player.getUniqueId());
    }

    public Party getParty(UUID uuid) {
        return partyMap.get(uuid);
    }

    public Party createParty(Player leader) {
        if (hasParty(leader)) return null;
        Party party = new Party(leader.getUniqueId());
        partyMap.put(leader.getUniqueId(), party);
        return party;
    }

    public void addPlayerToParty(Player player, Party party) {
        if (hasParty(player)) return;
        party.addMember(player.getUniqueId());
        partyMap.put(player.getUniqueId(), party);
    }

    public void removePlayerFromParty(Player player) {
        Party party = getParty(player);
        if (party != null) {
            party.removeMember(player.getUniqueId());
            partyMap.remove(player.getUniqueId());
            
            // If the party is empty or only has the leader left (or if the leader left), disband?
            // Usually if the leader leaves, the party disbands.
            if (party.isLeader(player.getUniqueId())) {
                disbandParty(party);
            }
        }
    }

    public void disbandParty(Party party) {
        party.broadcast("&cThe party has been disbanded.");
        for (UUID memberId : party.getMembers()) {
            partyMap.remove(memberId);
        }
    }

    public void invitePlayer(Player sender, Player target) {
        partyInvites.putIfAbsent(target.getUniqueId(), new ArrayList<>());
        partyInvites.get(target.getUniqueId()).add(sender.getUniqueId());
    }

    public boolean hasInviteFrom(Player target, Player sender) {
        return partyInvites.containsKey(target.getUniqueId()) && partyInvites.get(target.getUniqueId()).contains(sender.getUniqueId());
    }

    public void removeInvite(Player target, Player sender) {
        if (partyInvites.containsKey(target.getUniqueId())) {
            partyInvites.get(target.getUniqueId()).remove(sender.getUniqueId());
        }
    }
}
