package me.robomonkey.versus.queue;

import me.robomonkey.versus.kit.model.Kit;
import org.bukkit.entity.Player;

public class QueueEntry {
    private final Player player;
    private final Kit kit;
    private final long startTime;
    private final int elo;

    public QueueEntry(Player player, Kit kit, int elo) {
        this.player = player;
        this.kit = kit;
        this.elo = elo;
        this.startTime = System.currentTimeMillis();
    }

    public Player getPlayer() {
        return player;
    }

    public Kit getKit() {
        return kit;
    }

    public int getElo() {
        return elo;
    }

    public long getStartTime() {
        return startTime;
    }

    public int getSecondsInQueue() {
        return (int) ((System.currentTimeMillis() - startTime) / 1000L);
    }
}
