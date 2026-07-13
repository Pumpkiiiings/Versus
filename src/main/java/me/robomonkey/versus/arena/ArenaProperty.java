package me.robomonkey.versus.arena;

public enum ArenaProperty {
    CENTER_LOCATION("center of the arena"),
    SPAWN_LOCATION_ONE("first spawn location"),
    SPAWN_LOCATION_TWO("second spawn location"),
    SPECTATE_LOCATION("location for spectators"),
    KIT("kit for players"),
    SHARED("packet-based sharing mode"),
    POS_ONE("first boundary position"),
    POS_TWO("second boundary position"),
    ALLOW_BLOCK_PLACEMENTS("allow block placements"),
    ALLOW_BLOCK_DESTRUCTION("allow block destruction");


    private String friendlyString;

    ArenaProperty(String friendlyVersion) {
        friendlyString = friendlyVersion;
    }

    public ArenaProperty getNextProperty() {
        switch (this) {
            case CENTER_LOCATION:
                return SPAWN_LOCATION_ONE;
            case SPAWN_LOCATION_ONE:
                return SPAWN_LOCATION_TWO;
            case SPAWN_LOCATION_TWO:
                return SPECTATE_LOCATION;
            case SPECTATE_LOCATION:
                return KIT;
            case KIT:
                return SHARED;
            case SHARED:
                return POS_ONE;
            case POS_ONE:
                return POS_TWO;
            case POS_TWO:
                return ALLOW_BLOCK_PLACEMENTS;
            case ALLOW_BLOCK_PLACEMENTS:
                return ALLOW_BLOCK_DESTRUCTION;
            case ALLOW_BLOCK_DESTRUCTION:
                return null;
            default:
                return null;
        }
    }

    public String getExplanation() {
        switch (this) {
            case CENTER_LOCATION:
                return "Select the center of the arena, often located in the middle of both duelists.";
            case SPAWN_LOCATION_ONE:
                return "This determines whether the first player in a duel will spawn. ";
            case SPAWN_LOCATION_TWO:
                return "This determines where the second player in a duel will spawn";
            case SPECTATE_LOCATION:
                return "This determines where spectators will be teleported to watch a duel, and where the players will be sent after" +
                        " completing a duel.";
            case KIT:
                return "This determines the inventory players will have upon entering a duel in this arena.";
            case SHARED:
                return "Toggles whether this arena can hold multiple packet-based duels at the same time.";
            case POS_ONE:
                return "Select the first corner of the arena boundary.";
            case POS_TWO:
                return "Select the second corner of the arena boundary.";
            case ALLOW_BLOCK_PLACEMENTS:
                return "Toggles whether blocks can be placed during a duel in this arena.";
            case ALLOW_BLOCK_DESTRUCTION:
                return "Toggles whether blocks can be destroyed during a duel in this arena.";
            default:
                return "";
        }
    }

    public static ArenaProperty fromString(String propertyName) {
        try {
            ArenaProperty fromString = ArenaProperty.valueOf(propertyName.toUpperCase());
            return fromString;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    public String toFriendlyString() {
        return friendlyString;
    }
}
