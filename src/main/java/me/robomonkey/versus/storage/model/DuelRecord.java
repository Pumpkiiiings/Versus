package me.robomonkey.versus.storage.model;

import java.util.UUID;

/**
 * A single completed duel, as stored in the duel_history table.
 */
public class DuelRecord {

    private final UUID winnerUuid;
    private final UUID loserUuid;
    private final String winnerName;
    private final String loserName;
    private final String kitName;
    private final boolean ranked;
    private final long timestamp;

    public DuelRecord(UUID winnerUuid, UUID loserUuid, String winnerName, String loserName,
                      String kitName, boolean ranked, long timestamp) {
        this.winnerUuid = winnerUuid;
        this.loserUuid = loserUuid;
        this.winnerName = winnerName;
        this.loserName = loserName;
        this.kitName = kitName;
        this.ranked = ranked;
        this.timestamp = timestamp;
    }

    public UUID getWinnerUuid() { return winnerUuid; }
    public UUID getLoserUuid() { return loserUuid; }
    public String getWinnerName() { return winnerName; }
    public String getLoserName() { return loserName; }
    public String getKitName() { return kitName; }
    public boolean isRanked() { return ranked; }
    public long getTimestamp() { return timestamp; }

    /**
     * Returns true when the given player won this duel.
     */
    public boolean isWinner(UUID uuid) {
        return winnerUuid.equals(uuid);
    }

    /**
     * Returns the name of the player on the other side of this duel.
     */
    public String getOpponentName(UUID uuid) {
        return isWinner(uuid) ? loserName : winnerName;
    }
}
