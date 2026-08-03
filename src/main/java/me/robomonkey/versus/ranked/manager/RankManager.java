package me.robomonkey.versus.ranked.manager;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.ranked.model.Rank;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Maps ELO values onto the named ranks configured under "ranked.ranks" in config.yml.
 */
public class RankManager {

    private static final String CONFIG_PATH = "ranked.ranks";

    private static volatile RankManager instance;
    private List<Rank> ranks = new ArrayList<>();

    private RankManager() {
        reload();
    }

    public static RankManager getInstance() {
        if (instance == null) {
            synchronized (RankManager.class) {
                if (instance == null) {
                    instance = new RankManager();
                }
            }
        }
        return instance;
    }

    /**
     * Rereads the rank table. Safe to call after a config reload.
     */
    public void reload() {
        List<Rank> loaded = new ArrayList<>();
        ConfigurationSection section = Settings.getInstance().getConfig().getConfigurationSection(CONFIG_PATH);

        if (section != null) {
            for (String id : section.getKeys(false)) {
                ConfigurationSection entry = section.getConfigurationSection(id);
                if (entry == null) continue;
                String displayName = entry.getString("display_name", id);
                int minimumElo = entry.getInt("minimum_elo", 0);
                loaded.add(new Rank(id, displayName, minimumElo));
            }
        }

        if (loaded.isEmpty()) {
            Versus.error("No ranks configured under '" + CONFIG_PATH + "' in config.yml. Rank placeholders will be empty.");
        }

        // Highest threshold first, so getRank can return on the first match.
        loaded.sort(Comparator.comparingInt(Rank::getMinimumElo).reversed());
        this.ranks = loaded;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    /**
     * Returns the rank an ELO value falls into, or null when no rank matches.
     */
    public Rank getRank(int elo) {
        for (Rank rank : ranks) {
            if (elo >= rank.getMinimumElo()) return rank;
        }
        return null;
    }

    /**
     * Returns the next rank above the given ELO, or null when already at the top.
     */
    public Rank getNextRank(int elo) {
        Rank next = null;
        for (Rank rank : ranks) {
            if (rank.getMinimumElo() > elo && (next == null || rank.getMinimumElo() < next.getMinimumElo())) {
                next = rank;
            }
        }
        return next;
    }

    /**
     * ELO still needed to reach the next rank, or 0 when already at the top.
     */
    public int getEloToNextRank(int elo) {
        Rank next = getNextRank(elo);
        return next == null ? 0 : next.getMinimumElo() - elo;
    }

    public String getDisplayName(int elo) {
        Rank rank = getRank(elo);
        return rank != null ? rank.getDisplayName() : "";
    }
}
