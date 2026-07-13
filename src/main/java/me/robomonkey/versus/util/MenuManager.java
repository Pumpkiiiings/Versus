package me.robomonkey.versus.util;

import me.robomonkey.versus.Versus;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MenuManager {

    private static MenuManager instance;
    private final Versus plugin;
    private final File menusFolder;
    private final Map<String, FileConfiguration> menus = new HashMap<>();

    public MenuManager(Versus plugin) {
        this.plugin = plugin;
        this.menusFolder = new File(plugin.getDataFolder(), "menus");
        if (!menusFolder.exists()) {
            menusFolder.mkdirs();
        }
        loadDefaultMenus();
    }

    public static void init(Versus plugin) {
        if (instance == null) {
            instance = new MenuManager(plugin);
        }
    }

    public static MenuManager getInstance() {
        return instance;
    }

    private void loadDefaultMenus() {
        loadMenu("kit_selection.yml");
        loadMenu("kit_viewer.yml");
        loadMenu("betting_menu.yml");
        loadMenu("rewards_menu.yml");
    }

    public void loadMenu(String fileName) {
        File file = new File(menusFolder, fileName);
        if (!file.exists()) {
            plugin.saveResource("menus/" + fileName, false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        menus.put(fileName, config);
    }

    public FileConfiguration getMenuConfig(String fileName) {
        if (!menus.containsKey(fileName)) {
            loadMenu(fileName);
        }
        return menus.get(fileName);
    }
    
    public void reloadMenus() {
        menus.clear();
        loadDefaultMenus();
    }
}
