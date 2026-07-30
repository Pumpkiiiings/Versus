package me.robomonkey.versus.config.model;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.duel.model.ReturnOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Setting {

    ERROR_PREFIX("messages-general.errors", Type.STRING, FileType.MESSAGES),
    NO_PERMISSION_MESSAGE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ONLY_PLAYERS_MESSAGE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_PLAYER_OFFLINE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_CANNOT_DUEL_SELF("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_CANNOT_DUEL_RIGHT_NOW("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_TARGET_CANNOT_DUEL("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_REQUESTER_OFFLINE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_ALREADY_QUEUEING("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_WAIT_FOR_RESPONSE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NEGATIVE_BET("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_ECONOMY_DISABLED("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NOT_ENOUGH_MONEY("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_BET_MINIMUM("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_BET_NOT_ENOUGH_XP("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_BET_BLACKLISTED_ITEM("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_BET_MAX_ITEMS("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_ARENA_NOT_EXIST("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_ARENA_ALREADY_EXISTS("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NO_ARENA_PROPERTY("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NO_REQUEST("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NOT_QUEUEING("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_TARGET_NOT_ENOUGH_MONEY("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_INVALID_SETTING("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_INVALID_SETTING_VALUE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_SAVE_CONFIG("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_PROVIDE_KIT("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_KIT_ALREADY_EXISTS("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_SPECIFY_KIT_TO_LOAD("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_KIT_NOT_EXIST("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_SPECIFY_KIT_TO_DELETE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_SPECIFY_PLAYER_STATS("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_PLAYER_NOT_FOUND("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_SPECIFY_PLAYER_SPECTATE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_CANNOT_SPECTATE_RIGHT_NOW("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_PLAYER_NOT_ONLINE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_CANNOT_SPECTATE_SELF("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_PLAYER_NOT_DUELING("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_SPECIFY_PLAYER_DENY("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NO_PENDING_BET("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_UNKNOWN_COMMAND("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_PROVIDE_ARENA("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_IS_GROUP_DUEL("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_COMMAND_BLOCKED_DUEL("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_COMMAND_BLOCKED_SPECTATE("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NOT_DUELING("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_DUEL_ALREADY_ENDED("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_ALREADY_DEAD("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_NOT_SPECTATING("messages-general.errors", Type.STRING, FileType.MESSAGES),
    ERROR_OUT_OF_BOUNDS("messages-general.errors", Type.STRING, FileType.MESSAGES),

    ADMIN_SAVING_CONFIG("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_SAVED_CONFIG("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_SAVED_CONFIG_LIST_ITEM("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_RELOADING_CONFIG("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_RELOADED_CONFIG("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_SET_CONFIG("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_HELP_HEADER("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_SUPPORT_MESSAGE("messages-general.admin", Type.STRING, FileType.MESSAGES),
    ADMIN_SUPPORT_HOVER("messages-general.admin", Type.STRING, FileType.MESSAGES),

    KIT_SAVED("kit.messages", Type.STRING, FileType.MESSAGES),
    KIT_LOADED("kit.messages", Type.STRING, FileType.MESSAGES),
    KIT_DELETED("kit.messages", Type.STRING, FileType.MESSAGES),

    ARENA_CREATED("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_DELETED("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_TELEPORTED("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_SPAWN_SET("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_NO_ARENAS_LIST("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_PROPERTY_SET("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_EDIT_SELECT_PROPERTY("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_EDIT_TITLE("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_EDIT_VISIT_HOVER("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_SHARED_MODE_TOGGLED("arena.messages", Type.STRING, FileType.MESSAGES),
    ARENA_KIT_SET("arena.messages", Type.STRING, FileType.MESSAGES),

    STATS_HEADER("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_TITLE("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_WINS("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_LOSSES("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_RATIO("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_STREAK_CURRENT("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_STREAK_BEST("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_FOOTER("stats.messages", Type.STRING, FileType.MESSAGES),
    STATS_SPACER("stats.messages", Type.STRING, FileType.MESSAGES),

    PREFIX("messages-general.admin", Type.STRING, FileType.MESSAGES),
    PREFIX_ENABLED("messages-general.admin", Type.BOOLEAN, FileType.MESSAGES),
    PRIMARY_COLOR("messages-general.admin", Type.MCCOLOR, FileType.MESSAGES),
    HIGHLIGHTED_COLOR("messages-general.admin", Type.MCCOLOR, FileType.MESSAGES),
    BOLD_COLOR("messages-general.admin", Type.MCCOLOR, FileType.MESSAGES),
    SUBTLE_COLOR("messages-general.admin", Type.MCCOLOR, FileType.MESSAGES),
    LINE("messages-general.admin", Type.STRING, FileType.MESSAGES),

    DUEL_LOSS_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    VICTORY_SUBTITLE_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    VICTORY_TITLE_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    DUEL_GO_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    COUNTDOWN_TITLE("dueling.messages", Type.STRING, FileType.MESSAGES),
    COUNTDOWN_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    DUEL_SPECTATE_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    PROMPT_ENTER_BET("dueling.messages", Type.STRING, FileType.MESSAGES),
    PROMPT_CONFIRM_BET("dueling.messages", Type.STRING, FileType.MESSAGES),
    CONFIRM_BUTTON("dueling.messages", Type.STRING, FileType.MESSAGES),
    EDIT_BUTTON("dueling.messages", Type.STRING, FileType.MESSAGES),
    DUEL_BET_WON("dueling.messages", Type.STRING, FileType.MESSAGES),
    BET_CANCELLED("dueling.messages", Type.STRING, FileType.MESSAGES),
    DUEL_FORFEIT_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),
    LEFT_SPECTATING_MESSAGE("dueling.messages", Type.STRING, FileType.MESSAGES),

    REWARDS_GUI_TITLE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_EMPTY_NAME("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_EMPTY_LORE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_ITEM_NAME("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_SEPARATOR("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_WINNER_LINE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_LOSER_LINE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_DATE_LINE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_MONEY_LINE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_XP_LINE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_ITEMS_LINE("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_CLAIM_BUTTON("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_ALREADY_CLAIMED("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_MONEY_RECEIVED("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_XP_RECEIVED("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_ITEMS_RECEIVED("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_CLAIMED_SUCCESS("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_PENDING_NOTIFY("dueling.messages", Type.STRING, FileType.MESSAGES),
    REWARDS_GUI_DATE_FORMAT("dueling.messages", Type.STRING, FileType.MESSAGES),

    COSMETIC_LORE_FREE("cosmetics.messages", Type.STRING, FileType.MESSAGES),
    COSMETIC_LORE_PRICE("cosmetics.messages", Type.STRING, FileType.MESSAGES),
    COSMETIC_LORE_BUY("cosmetics.messages", Type.STRING, FileType.MESSAGES),
    COSMETIC_BOUGHT_SUCCESS("cosmetics.messages", Type.STRING, FileType.MESSAGES),

    PAGINATION_PREVIOUS_PAGE("menus.pagination", Type.STRING, FileType.MESSAGES),
    PAGINATION_PREVIOUS_LORE("menus.pagination", Type.STRING, FileType.MESSAGES),
    PAGINATION_CURRENT_PAGE("menus.pagination", Type.STRING, FileType.MESSAGES),
    PAGINATION_CURRENT_LORE("menus.pagination", Type.STRING, FileType.MESSAGES),
    PAGINATION_NEXT_PAGE("menus.pagination", Type.STRING, FileType.MESSAGES),
    PAGINATION_NEXT_LORE("menus.pagination", Type.STRING, FileType.MESSAGES),

    RETURN_WINNERS("dueling.mechanics", Type.RETURNOPTIONS),
    WINNER_RETURN_LOCATION("dueling.mechanics", Type.LOCATION),
    INSTANT_RESPAWN("dueling.mechanics", Type.BOOLEAN),
    PERMISSION_REQUIRED_TO_DUEL("dueling.mechanics", Type.BOOLEAN),
    RETURN_LOSERS("dueling.mechanics", Type.RETURNOPTIONS),
    LOSER_RETURN_LOCATION("dueling.mechanics", Type.LOCATION),
    BLOCKED_COMMANDS("dueling.mechanics", Type.INVALID),
    COUNTDOWN_DURATION("dueling.mechanics", Type.NUMBER),
    POST_DUEL_DELAY("dueling.mechanics", Type.NUMBER),
    ENDER_PEARL_COOLDOWN("dueling.mechanics", Type.NUMBER),

    ANNOUNCE_DUELS("dueling.announcements", Type.BOOLEAN),
    DUEL_START_ANNOUNCEMENT("dueling.announcements", Type.STRING, FileType.MESSAGES),
    DUEL_END_ANNOUNCEMENT("dueling.announcements", Type.STRING, FileType.MESSAGES),
    STREAK_BROADCAST_ENABLED("dueling.announcements", Type.BOOLEAN),
    STREAK_BROADCAST_MINIMUM("dueling.announcements", Type.NUMBER),
    STREAK_BROADCAST_INTERVAL("dueling.announcements", Type.NUMBER),
    STREAK_BROADCAST_MESSAGE("dueling.announcements", Type.STRING, FileType.MESSAGES),

    VICTORY_EFFECTS_ENABLED("dueling.effects", Type.BOOLEAN),
    VICTORY_EFFECTS_DURATION("dueling.effects", Type.BOOLEAN),
    FIREWORKS_ENABLED("dueling.effects", Type.BOOLEAN),
    FIREWORKS_COLOR("dueling.effects", Type.COLOR),
    BLINDNESS_EFFECTS_ENABLED("dueling.effects", Type.BOOLEAN),

    FIGHT_MUSIC_ENABLED("dueling.music", Type.BOOLEAN),
    FIGHT_MUSIC("dueling.music", Type.BOOLEAN),
    VICTORY_MUSIC_ENABLED("dueling.music", Type.BOOLEAN),
    VICTORY_MUSIC("dueling.music", Type.MUSIC),

    SHIFT_CLICK_REQUESTING_ENABLED("requesting.mechanics", Type.BOOLEAN),

    DENIED_REQUEST("requesting.messages", Type.STRING, FileType.MESSAGES),
    DENIED_REQUEST_CONFIRMATION("requesting.messages", Type.STRING, FileType.MESSAGES),
    CANCEL_REQUEST("requesting.messages", Type.STRING, FileType.MESSAGES),
    SENT_REQUEST("requesting.messages", Type.STRING, FileType.MESSAGES),
    REQUEST_NOTIFICATION("requesting.messages", Type.STRING, FileType.MESSAGES),
    ACCEPT_BUTTON("requesting.messages", Type.STRING, FileType.MESSAGES),
    NO_ARENAS_AVAILABLE("requesting.messages", Type.STRING, FileType.MESSAGES),
    DENY_BUTTON("requesting.messages", Type.STRING, FileType.MESSAGES),
    
    PARTY_ALREADY_IN("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NO_INVITES("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DOES_NOT_EXIST("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_PLAYER_JOINED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_LEADER_ACCEPT_DUEL("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NO_DUEL_REQUEST("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_ENOUGH_MONEY("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_OTHER_NOT_ENOUGH_MONEY("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_CREATED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_INVITE_DECLINED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_INVITE_DECLINED_OTHER("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_LEADER_DECLINE_DUEL("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DUEL_DECLINED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DUEL_DECLINED_OTHER("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_IN("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_LEADER_DISBAND("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DISBANDED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_LEADER_DUEL("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_TARGET_NOT_IN("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_MUST_DUEL_LEADER("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_CANNOT_DUEL_SELF("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DUEL_CHALLENGE_SENT("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DUEL_CHALLENGE_RECEIVED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_IN_CREATE("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_NOT_LEADER_INVITE("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_TARGET_ALREADY_IN("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_ALREADY_INVITED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_INVITE_SENT("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_INVITE_RECEIVED("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_LEADER_MUST_DISBAND("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_PLAYER_LEFT("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_LEFT("party.messages", Type.STRING, FileType.MESSAGES),
    PARTY_DIED_SPECTATING("party.messages", Type.STRING, FileType.MESSAGES),
    
    DUELGROUP_HELP_HEADER("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_CREATE("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_INVITE("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_ACCEPT("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_DECLINE("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_LEAVE("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_DISBAND("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_DUEL("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_ACCEPTDUEL("party.help", Type.STRING, FileType.MESSAGES),
    DUELGROUP_HELP_DECLINEDUEL("party.help", Type.STRING, FileType.MESSAGES),

    ESSENTIALS_NICKNAMES_ENABLED("dependencies.placeholderAPI", Type.BOOLEAN),
    ITEMS_ADDER_FOR_KITS("dependencies.itemsadder", Type.BOOLEAN),

    RANKED_BASE_ELO("ranked", Type.NUMBER),
    RANKED_K_FACTOR("ranked", Type.NUMBER),
    RANKED_QUEUE_BASE_ELO_DIFF("ranked.queue", Type.NUMBER),
    RANKED_QUEUE_ELO_DIFF_PER_SECOND("ranked.queue", Type.NUMBER),
    RANKED_SEASON_REWARDS_TOP_1_XP("ranked.season.rewards", Type.NUMBER),
    RANKED_SEASON_REWARDS_TOP_2_XP("ranked.season.rewards", Type.NUMBER),
    RANKED_SEASON_REWARDS_TOP_3_XP("ranked.season.rewards", Type.NUMBER),
    
    RANKED_QUEUE_JOIN("ranked.messages", Type.STRING, FileType.MESSAGES),
    RANKED_QUEUE_LEAVE("ranked.messages", Type.STRING, FileType.MESSAGES),
    RANKED_QUEUE_MATCH_FOUND("ranked.messages", Type.STRING, FileType.MESSAGES),
    RANKED_ELO_CHANGE_WINNER("ranked.messages", Type.STRING, FileType.MESSAGES),
    RANKED_ELO_CHANGE_LOSER("ranked.messages", Type.STRING, FileType.MESSAGES),
    RANKED_SEASON_ENDED_BROADCAST("ranked.messages", Type.STRING, FileType.MESSAGES),
    
    RANKED_MENU_TITLE("ranked.menus.ranked", Type.STRING, FileType.MESSAGES),
    RANKED_MENU_LORE_JOIN("ranked.menus.ranked", Type.STRING, FileType.MESSAGES),
    RANKED_MENU_LORE_LEAVE("ranked.menus.ranked", Type.STRING, FileType.MESSAGES),
    RANKED_MENU_LORE_ELO("ranked.menus.ranked", Type.STRING, FileType.MESSAGES),

    TOP_MENU_TITLE("ranked.menus.top", Type.STRING, FileType.MESSAGES),
    TOP_MENU_LORE_ELO("ranked.menus.top", Type.STRING, FileType.MESSAGES),
    TOP_MENU_NO_PLAYERS("ranked.menus.top", Type.STRING, FileType.MESSAGES);

    public Object value;
    public String path;
    public Type type;
    public FileType fileType;

    Setting(String path, Type type) {
        this(path, type, FileType.CONFIG);
    }

    Setting(String path, Type type, FileType fileType) {
        this.path = path + "." + this.toString().toLowerCase();
        this.type = type;
        this.fileType = fileType;
    }

    public Object getValue() {
        if (value == null) {
            return getDefaultValue();
        }
        return value;
    }

    public Object getDefaultValue() {
        if (fileType == FileType.MESSAGES) {
            return Settings.getInstance().getMessagesConfig().getDefaults().get(getPath());
        }
        return Versus.getInstance().getConfig().getDefaults().get(getPath());
    }

    public String getPath() {
        return this.path;
    }

    /**
     * Returns whether a value was successfully set
     *
     * @param value
     * @return
     */
    public boolean setValue(Object value) {
        if (value.getClass() == myClass()) {
            this.value = value;
            return true;
        } else {
            return false;
        }
    }

    public Class myClass() {
        Object savedDefault = getDefaultValue();
        return savedDefault.getClass();
    }

    public Type getType() {
        return this.type;
    }

    public enum Type {
        BOOLEAN(List.of("true", "false")),
        STRING(List.of("<message>")),
        LOCATION(List.of("<x> <y> <z> <world name>")),
        MUSIC(List.of("creative", "credits", "disc_5", "disc_11", "disc_13", "disc_blocks",
                "disc_cat", "disc_chirp", "disc_creator", "disc_creator_music_box", "disc_far",
                "disc_mall", "disc_mellohi", "disc_otherside", "disc_pigstep", "disc_precipice",
                "disc_relic", "disc_stal", "disc_strad", "disc_wait", "disc_ward")),
        RETURNOPTIONS(Arrays.stream(ReturnOption.values()).map(value -> value.toString()).collect(Collectors.toList())),
        COLOR(List.copyOf(Settings.colorMap.keySet())),
        MCCOLOR(List.of("&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f")),
        NUMBER(List.of("<number>")),
        INVALID(null);
        List<String> options;

        Type(List<String> autoCompletions) {
            options = autoCompletions;
        }

        public List<String> getOptions() {
            return options;
        }
    }

    public enum FileType {
        CONFIG, MESSAGES
    }
}

