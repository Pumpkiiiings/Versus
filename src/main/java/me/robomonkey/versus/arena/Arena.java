package me.robomonkey.versus.arena;

import me.robomonkey.versus.duel.Duel;
import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.kit.KitManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Arena {
    public static Arena empty = null;
    private final String name;
    private final List<Duel> activeDuels;
    private Location spawnLocationOne;
    private Location spawnLocationTwo;
    private Location centerLocation;
    private Location spectateLocation;
    private boolean enabled = false;
    private boolean shared = false;
    private List<Kit> kits = new ArrayList<>();

    /**
     * <h1>Creates Arena.</h1>
     * <p>Note: Arenas handle their own verification logic.</p>
     *
     * @param name
     */
    public Arena(String name) {
        this.name = name;
        this.activeDuels = new ArrayList<Duel>();
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public List<Kit> getKits() {
        return kits;
    }

    public void addKit(Kit kit) {
        if (!kits.contains(kit)) {
            kits.add(kit);
        }
        verifySelf();
    }

    public void removeKit(Kit kit) {
        kits.remove(kit);
        verifySelf();
    }

    public List<Duel> getActiveDuels() {
        return activeDuels;
    }

    public Location getSpawnLocationOne() {
        return spawnLocationOne;
    }

    public Location getSpawnLocationTwo() {
        return spawnLocationTwo;
    }

    public Location getCenterLocation() {
        return centerLocation;
    }

    public Location getSpectateLocation() {
        return spectateLocation;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAvailable() {
        if (shared) return true;
        return this.getActiveDuels().size() == 0;
    }

    public boolean isShared() {
        return shared;
    }

    public void setShared(boolean shared) {
        this.shared = shared;
    }

    private boolean isValid() {
        return (spawnLocationOne != null
                && spawnLocationTwo != null
                && centerLocation != null
                && spectateLocation != null);
    }

    public void setLocationProperty(ArenaProperty property, Location value) {
        switch (property) {
            case SPAWN_LOCATION_ONE:
                spawnLocationOne = value;
                break;
            case SPAWN_LOCATION_TWO:
                spawnLocationTwo = value;
                break;
            case SPECTATE_LOCATION:
                spectateLocation = value;
                break;
            case CENTER_LOCATION:
                centerLocation = value;
                break;
        }
        verifySelf();
    }

    public void setKits(List<Kit> kits) {
        this.kits = kits;
        verifySelf();
    }

    public void addDuel(Duel activeDuel) {
        this.activeDuels.add(activeDuel);
    }

    public void removeDuel(Duel completedDuel) {
        this.activeDuels.remove(completedDuel);
    }

    private void verifySelf() {
        if (!isValid()) enabled = false;
        else enabled = true;
    }

    public void saveToYaml(YamlConfiguration config) {
        verifySelf();
        config.set("name", this.name);
        config.set("enabled", this.enabled);
        config.set("shared", this.shared);
        
        List<String> kitNames = new ArrayList<>();
        for (Kit k : this.kits) {
            kitNames.add(k.getName());
        }
        config.set("kits", kitNames);
        config.set("spawnLocationOne", this.spawnLocationOne);
        config.set("spawnLocationTwo", this.spawnLocationTwo);
        config.set("centerLocation", this.centerLocation);
        config.set("spectateLocation", this.spectateLocation);
    }

    public static Arena loadFromYaml(YamlConfiguration config) {
        String name = config.getString("name");
        Arena newArena = new Arena(name);
        newArena.setLocationProperty(ArenaProperty.CENTER_LOCATION, (Location) config.get("centerLocation"));
        newArena.setLocationProperty(ArenaProperty.SPAWN_LOCATION_ONE, (Location) config.get("spawnLocationOne"));
        newArena.setLocationProperty(ArenaProperty.SPAWN_LOCATION_TWO, (Location) config.get("spawnLocationTwo"));
        newArena.setLocationProperty(ArenaProperty.SPECTATE_LOCATION, (Location) config.get("spectateLocation"));
        
        List<String> kitNames = config.getStringList("kits");
        List<Kit> loadedKits = new ArrayList<>();
        if (kitNames != null && !kitNames.isEmpty()) {
            for (String kitName : kitNames) {
                Kit k = KitManager.getInstance().getKit(kitName);
                if (k != null) loadedKits.add(k);
            }
        } else {
            // Legacy support
            String legacyKit = config.getString("kit");
            if (legacyKit != null) {
                Kit k = KitManager.getInstance().getKit(legacyKit);
                if (k != null) loadedKits.add(k);
            } else {
                Kit k = KitManager.getInstance().getDefaultKit();
                if (k != null) loadedKits.add(k);
            }
        }
        newArena.setKits(loadedKits);
        
        newArena.setShared(config.getBoolean("shared", false));
        return newArena;
    }

}

