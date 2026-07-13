package me.robomonkey.versus.party;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import me.robomonkey.versus.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Party {
    private final UUID leader;
    private final List<UUID> members;
    private int streak = 0;

    public Party(UUID leader) {
        this.leader = leader;
        this.members = new ArrayList<>();
        this.members.add(leader);
    }

    public UUID getLeader() {
        return leader;
    }

    public List<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID member) {
        if (!members.contains(member)) {
            members.add(member);
        }
    }

    public void removeMember(UUID member) {
        members.remove(member);
    }

    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    public void incrementStreak() {
        this.streak++;
    }

    public List<Player> getOnlinePlayers() {
        return members.stream()
                .map(Bukkit::getPlayer)
                .filter(p -> p != null && p.isOnline())
                .collect(Collectors.toList());
    }

    public void broadcast(String message) {
        String coloredMessage = MessageUtil.color(message);
        for (Player player : getOnlinePlayers()) {
            player.sendMessage(coloredMessage);
        }
    }
}
