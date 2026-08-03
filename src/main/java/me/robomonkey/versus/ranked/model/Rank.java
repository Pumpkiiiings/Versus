package me.robomonkey.versus.ranked.model;

/**
 * A named ELO bracket, e.g. "Oro" starting at 1200.
 */
public class Rank {

    private final String id;
    private final String displayName;
    private final int minimumElo;

    public Rank(String id, String displayName, int minimumElo) {
        this.id = id;
        this.displayName = displayName;
        this.minimumElo = minimumElo;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinimumElo() {
        return minimumElo;
    }
}
