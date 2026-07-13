package me.robomonkey.versus.duel.rewards;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Represents a pending reward for a duel winner (items, money, XP).
 * Persisted in H2 table duel_rewards with anti-dupe claimed flag.
 */
public class DuelReward {

    private int id;
    private final UUID winnerUuid;
    private final UUID loserUuid;
    private final long timestamp;
    private final List<ItemStack> items;
    private final double money;
    private final int xp;
    private boolean claimed;

    public DuelReward(int id, UUID winnerUuid, UUID loserUuid, long timestamp,
                      List<ItemStack> items, double money, int xp, boolean claimed) {
        this.id = id;
        this.winnerUuid = winnerUuid;
        this.loserUuid = loserUuid;
        this.timestamp = timestamp;
        this.items = items;
        this.money = money;
        this.xp = xp;
        this.claimed = claimed;
    }

    public DuelReward(UUID winnerUuid, UUID loserUuid, long timestamp,
                      List<ItemStack> items, double money, int xp) {
        this(-1, winnerUuid, loserUuid, timestamp, items, money, xp, false);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public UUID getWinnerUuid() { return winnerUuid; }
    public UUID getLoserUuid() { return loserUuid; }
    public long getTimestamp() { return timestamp; }

    public List<ItemStack> getItems() { return items; }
    public double getMoney() { return money; }
    public int getXp() { return xp; }

    public boolean isClaimed() { return claimed; }
    public void setClaimed(boolean claimed) { this.claimed = claimed; }

    public boolean hasItems() {
        return items != null && !items.isEmpty();
    }

    public boolean hasRewards() {
        return money > 0 || xp > 0 || hasItems();
    }
}
