package me.robomonkey.versus;

import me.robomonkey.versus.duel.task.BoundaryVisualizerTask;
import me.robomonkey.versus.cosmetic.manager.CosmeticsManager;
import me.robomonkey.versus.reward.manager.RewardManager;
import me.robomonkey.versus.duel.command.DuelGroupCommand;
import me.robomonkey.versus.command.RootVersusCommand;
import me.robomonkey.versus.command.RootVersusTestCommand;
import me.robomonkey.versus.command.RootVersusStatsCommand;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonParseException;
import com.samjakob.spigui.SpiGUI;
import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.arena.command.RootArenaCommand;
import me.robomonkey.versus.dependency.Dependencies;
import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.duel.command.RootDuelCommand;
import me.robomonkey.versus.duel.command.RootSpectateCommand;
import me.robomonkey.versus.listener.visibility.PacketVisibilityListener;
import me.robomonkey.versus.storage.adapter.ConfigurationSerializableAdapter;
import me.robomonkey.versus.storage.adapter.ItemStackAdapter;
import me.robomonkey.versus.storage.manager.DatabaseManager;
import me.robomonkey.versus.storage.manager.StatsManager;
import me.robomonkey.versus.storage.adapter.ItemStackArrayAdapter;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
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
    private boolean packetEventsLoaded = false;

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
        return me.robomonkey.versus.util.MessageUtil.color(message);
    }

    public static Versus getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        try {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
            PacketEvents.getAPI().getSettings().bStats(true);
            ensureCompatibleServerVersion();
            PacketEvents.getAPI().load();
            packetEventsLoaded = true;
        } catch (Throwable t) {
            error("Could not load PacketEvents: " + t.getMessage());
            t.printStackTrace();
        }
    }

    private void ensureCompatibleServerVersion() {
        try {
            com.github.retrooper.packetevents.manager.server.ServerManager serverManager = PacketEvents.getAPI().getServerManager();
            com.github.retrooper.packetevents.manager.server.ServerVersion detected = serverManager.getVersion();
            String bukkitVersion = Bukkit.getBukkitVersion();

            boolean isModern = false;
            try {
                Class.forName("org.bukkit.entity.Display");
                isModern = true;
            } catch (ClassNotFoundException ignored) {
                if (!bukkitVersion.contains("1.8")) {
                    isModern = true;
                }
            }

            if (detected == com.github.retrooper.packetevents.manager.server.ServerVersion.V_1_8_8 && isModern) {
                com.github.retrooper.packetevents.manager.server.ServerVersion target = com.github.retrooper.packetevents.manager.server.ServerVersion.getLatest();
                for (com.github.retrooper.packetevents.manager.server.ServerVersion val : com.github.retrooper.packetevents.manager.server.ServerVersion.reversedValues()) {
                    if (bukkitVersion.contains(val.getReleaseName())) {
                        target = val;
                        break;
                    }
                }

                java.lang.reflect.Field field = serverManager.getClass().getDeclaredField("serverVersion");
                field.setAccessible(true);
                field.set(serverManager, target);
                log("Adjusted PacketEvents ServerVersion fallback from V_1_8_8 to " + target.name() + " for: " + bukkitVersion);
            }
        } catch (Throwable t) {
            log("ServerVersion check notice: " + t.getMessage());
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        if (packetEventsLoaded) {
            try {
                PacketEvents.getAPI().init();
                PacketEvents.getAPI().getEventManager().registerListener(new PacketVisibilityListener());
            } catch (Throwable t) {
                error("Could not initialize PacketEvents listener: " + t.getMessage());
                t.printStackTrace();
            }
        } else {
            error("PacketEvents was not loaded. Entity hiding in shared arenas will be disabled.");
        }
        log("Versus has been enabled!");
        DatabaseManager.getInstance();
        me.robomonkey.versus.reward.manager.RewardManager.getInstance().startCleanupTask();
        StatsManager.getInstance();
        me.robomonkey.versus.cosmetic.manager.CosmeticsManager.init();
        MenuManager.init(this);
        Settings.getInstance().registerConfig();
        spiGUI = new SpiGUI(this);
        spiGUI.setDefaultToolbarBuilder(new me.robomonkey.versus.util.CustomPaginationBuilder());
        duelManager = DuelManager.getInstance();
        arenaManager = ArenaManager.getInstance();
        Bukkit.getScheduler().runTask(this, () -> arenaManager.loadArenas());
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new me.robomonkey.versus.duel.task.BoundaryVisualizerTask(duelManager), 2L, 2L);
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
        if (packetEventsLoaded) {
            try {
                PacketEvents.getAPI().terminate();
            } catch (Throwable t) {
                error("Error terminating PacketEvents: " + t.getMessage());
            }
        }
        log("Versus has been disabled!");
        if (arenaManager != null) {
            arenaManager.saveAllArenas();
        }
        if (DatabaseManager.hasInstance()) {
            DatabaseManager.getInstance().closeConnection();
        }
    }

    public void registerCommands() {
        new RootArenaCommand();
        new RootVersusCommand();
        new RootDuelCommand();
        new RootSpectateCommand();
        new me.robomonkey.versus.duel.command.DuelGroupCommand();
        new RootVersusTestCommand();
        new RootVersusStatsCommand();
        new me.robomonkey.versus.ranked.command.RootRankedCommand();
    }

    private void registerMetrics() {
        Metrics metrics = new Metrics(this, pluginId);
        List<Setting> noted = List.of(Setting.FIGHT_MUSIC_ENABLED, Setting.VICTORY_MUSIC_ENABLED, Setting.RETURN_WINNERS, Setting.RETURN_LOSERS, Setting.ANNOUNCE_DUELS, Setting.FIREWORKS_ENABLED, Setting.VICTORY_EFFECTS_ENABLED);
        // Collects config metrics to inform development priorities in the future. Opt out in bstats config.
        noted.stream()
                .forEach(setting -> metrics.addCustomChart(new SimplePie(setting.toString().toLowerCase(), () -> Settings.getStringVersion(setting))));
    }

}
