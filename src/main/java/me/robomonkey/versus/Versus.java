package me.robomonkey.versus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import com.samjakob.spigui.SpiGUI;
import me.robomonkey.versus.arena.ArenaManager;
import me.robomonkey.versus.arena.command.RootArenaCommand;
import me.robomonkey.versus.dependency.Dependencies;
import me.robomonkey.versus.duel.DuelManager;
import me.robomonkey.versus.duel.command.RootDuelCommand;
import me.robomonkey.versus.duel.command.RootSpectateCommand;
import me.robomonkey.versus.duel.eventlisteners.PacketVisibilityListener;
import me.robomonkey.versus.duel.playerdata.adapter.ConfigurationSerializableAdapter;
import me.robomonkey.versus.duel.playerdata.adapter.ItemStackAdapter;
import me.robomonkey.versus.duel.playerdata.DatabaseManager;
import me.robomonkey.versus.duel.playerdata.StatsManager;
import me.robomonkey.versus.duel.playerdata.adapter.ItemStackArrayAdapter;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.MenuManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;

public final class Versus extends JavaPlugin {

    private static Gson gson;
    private ArenaManager arenaManager;
    private DuelManager duelManager;
    private static Versus instance;
    private final static String prefix = "[Versus]";
    private static final int pluginId = 23279;
    public static SpiGUI spiGUI;

    public static void log(String message) {
        Bukkit.getServer().getLogger().info(prefix + " " + message);
    }

    public static void error(String message) {
        log("Error: " + message);
    }

    public static Gson getGSON() {
        if (gson == null) {
            GsonBuilder builder = new GsonBuilder()
                    .setPrettyPrinting()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter())
                    .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                    .registerTypeAdapter(ItemStack[].class, new ItemStackArrayAdapter())
                    .registerTypeHierarchyAdapter(Optional.class, new JsonSerializer<Optional>() {
                        @Override
                        public JsonElement serialize(Optional src, Type typeOfSrc, JsonSerializationContext context) {
                            return src.isPresent() ? context.serialize(src.get()) : com.google.gson.JsonNull.INSTANCE;
                        }
                    })
                    .registerTypeHierarchyAdapter(Optional.class, new JsonDeserializer<Optional>() {
                        @Override
                        public Optional deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            if (json.isJsonNull()) return Optional.empty();
                            return Optional.ofNullable(context.deserialize(json, Object.class));
                        }
                    });
            gson = builder.create();
        }
        return gson;
    }

    public static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static Versus getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().bStats(true);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketVisibilityListener());
        log("Versus has been enabled!");
        instance = this;
        DatabaseManager.getInstance();
        me.robomonkey.versus.duel.rewards.RewardManager.getInstance().startCleanupTask();
        StatsManager.getInstance();
        MenuManager.init(this);
        Settings.getInstance().registerConfig();
        spiGUI = new SpiGUI(this);
        duelManager = DuelManager.getInstance();
        arenaManager = ArenaManager.getInstance();
        Bukkit.getScheduler().runTask(this, () -> arenaManager.loadArenas());
        registerCommands();
        Dependencies.refresh(getServer());
        me.robomonkey.versus.dependency.EconomyManager.setup();
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new me.robomonkey.versus.dependency.VersusPlaceholderExpansion().register();
        }
        registerMetrics();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        PacketEvents.getAPI().terminate();
        log("Versus has been disabled!");
        if (arenaManager != null) {
            arenaManager.saveAllArenas();
        }
    }

    public void registerCommands() {
        new RootArenaCommand();
        new RootVersusCommand();
        new RootDuelCommand();
        new RootSpectateCommand();
        new me.robomonkey.versus.command.DuelGroupCommand();
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, pluginId);
        List<Setting> noted = List.of(Setting.FIGHT_MUSIC_ENABLED, Setting.VICTORY_MUSIC_ENABLED, Setting.RETURN_WINNERS, Setting.RETURN_LOSERS, Setting.ANNOUNCE_DUELS, Setting.FIREWORKS_ENABLED, Setting.VICTORY_EFFECTS_ENABLED);
        // Collects config metrics to inform development priorities in the future. Opt out in bstats config.
        noted.stream()
                .forEach(setting -> metrics.addCustomChart(new SimplePie(setting.toString().toLowerCase(), () -> Settings.getStringVersion(setting))));
    }

}
