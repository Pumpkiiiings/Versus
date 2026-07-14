package me.robomonkey.versus.arena.manager;

import me.robomonkey.versus.arena.editor.ArenaBuilderCoordinator;
import me.robomonkey.versus.arena.model.ArenaProperty;
import me.robomonkey.versus.arena.editor.ArenaBuilder;
import me.robomonkey.versus.arena.model.Arena;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.duel.model.Duel;
import me.robomonkey.versus.kit.model.Kit;
import me.robomonkey.versus.kit.manager.KitManager;
import me.robomonkey.versus.util.JsonUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class ArenaManager {
    private ArrayList<Arena> arenaList = new ArrayList<>();
    public File dataFile;
    public Versus plugin = Versus.getInstance();
    private static ArenaManager instance;

    public static ArenaManager getInstance() {
        if (instance == null) {
            instance = new ArenaManager();
        }
        return instance;
    }

    public Arena getArena(String name) {
        Optional<Arena> matching = arenaList.stream()
                .filter((arena) -> arena.getName().equalsIgnoreCase(name))
                .findFirst();
        if (matching.isPresent()) return matching.get();
        else return null;
    }

    public void addArena(Arena arena) {
        arenaList.add(arena);
    }

    public ArrayList<Arena> getAllArenas() {
        return arenaList;
    }

    public List<Arena> getAvailableArenas() {
        return arenaList.stream().filter(arena -> arena.isAvailable() && arena.isEnabled()).collect(Collectors.toList());
    }

    /**
     * Returns first available arena in ArenaManager.arenaList.
     * May return Arena.empty or null if no arenas are available.
     *
     * @return
     */
    public Arena getAvailableArena() {
        List<Arena> availableArenas = getAvailableArenas();
        if (availableArenas.size() > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(availableArenas.size());
            return availableArenas.get(randomIndex);
        } else {
            return Arena.empty;
        }
    }

    public void loadArenas() {
        // Migrate old arena.json if it exists
        File oldDataFile = JsonUtil.getDataFile(plugin, "arena.json");
        if (oldDataFile.exists()) {
            Versus.log("Found old arena.json, starting migration to YAML...");
            try (Reader reader = new FileReader(oldDataFile)) {
                JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
                for (JsonElement element : jsonArray) {
                    JsonObject obj = element.getAsJsonObject();
                    String name = obj.get("name").getAsString();
                    Arena arena = new Arena(name);
                    
                    arena.setLocationProperty(ArenaProperty.SPAWN_LOCATION_ONE, parseLocation(obj.getAsJsonObject("spawnLocationOne")));
                    arena.setLocationProperty(ArenaProperty.SPAWN_LOCATION_TWO, parseLocation(obj.getAsJsonObject("spawnLocationTwo")));
                    arena.setLocationProperty(ArenaProperty.CENTER_LOCATION, parseLocation(obj.getAsJsonObject("centerLocation")));
                    arena.setLocationProperty(ArenaProperty.SPECTATE_LOCATION, parseLocation(obj.getAsJsonObject("spectateLocation")));
                    
                    if (obj.has("enabled")) arena.setEnabled(obj.get("enabled").getAsBoolean());
                    if (obj.has("kit")) {
                        Kit legacyKit = KitManager.getInstance().getKit(obj.get("kit").getAsString());
                        if (legacyKit != null) arena.addKit(legacyKit);
                    }
                    
                    arenaList.add(arena);
                }
                saveAllArenas(); // Save to YAML format
                boolean deleted = oldDataFile.delete();
                if (!deleted) {
                    oldDataFile.renameTo(new File(plugin.getDataFolder(), "arena.json.old"));
                }
                Versus.log("Migration completed successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
            arenaList.clear(); // Clear so the next block can load them from YAML normally
        }

        File arenasFolder = new File(plugin.getDataFolder(), "arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }

        File[] files = arenasFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null || files.length == 0) {
            Versus.log("There are no arenas in the arenas folder.");
            return;
        }
        
        arenaList.clear();

        for (File file : files) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                Arena arena = Arena.loadFromYaml(config);
                arenaList.add(arena);
                Versus.log("Loaded " + arena.getName() + " arena.");
            } catch (Exception e) {
                Versus.log("Failed to load arena file: " + file.getName());
                e.printStackTrace();
            }
        }
        Versus.log("Successfully loaded arenas.");
    }

    private Location parseLocation(JsonObject obj) {
        if (obj == null) return null;
        return new Location(
                Bukkit.getWorld(obj.get("world").getAsString()),
                obj.get("x").getAsDouble(),
                obj.get("y").getAsDouble(),
                obj.get("z").getAsDouble(),
                obj.get("yaw").getAsFloat(),
                obj.get("pitch").getAsFloat()
        );
    }

    public void saveAllArenas() {
        File arenasFolder = new File(plugin.getDataFolder(), "arenas");
        if (!arenasFolder.exists()) {
            arenasFolder.mkdirs();
        }

        for (Arena arena : arenaList) {
            if (!arena.isEnabled()) continue;
            File arenaFile = new File(arenasFolder, arena.getName() + ".yml");
            YamlConfiguration config = new YamlConfiguration();
            arena.saveToYaml(config);
            try {
                config.save(arenaFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Versus.log("Successfully saved arenas.");
    }

    public void sendToSpectatingArea(Player p, Arena a) {
        Location spectateLocation = a.getSpectateLocation();
        p.teleport(spectateLocation);
    }

    public void deleteArena(Arena arena) {
        arenaList.remove(arena);
        saveAllArenas();
    }

    public void registerDuel(Arena arena, Duel duel) {
        arena.addDuel(duel);
    }

    public void removeDuel(Duel duel) {
        Arena arena = duel.getArena();
        arena.removeDuel(duel);
    }

    public void notifyKitSelection(Kit kit, Player whoClicked) {
        if (ArenaBuilderCoordinator.getInstance().hasArenaBuilder(whoClicked)) {
            ArenaBuilder builder = ArenaBuilderCoordinator.getInstance().getArenaBuilder(whoClicked);
            builder.getTargetArena().addKit(kit);
        }
    }

    public Arena getAvailableArenaForKit(Kit kit) {
        List<Arena> availableArenas = getAvailableArenas().stream()
                .filter(arena -> arena.getKits().contains(kit))
                .collect(Collectors.toList());
        if (availableArenas.size() > 0) {
            Random random = new Random();
            int randomIndex = random.nextInt(availableArenas.size());
            return availableArenas.get(randomIndex);
        } else {
            return null;
        }
    }
}
