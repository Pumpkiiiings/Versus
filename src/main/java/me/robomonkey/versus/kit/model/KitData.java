package me.robomonkey.versus.kit.model;

import dev.lone.itemsadder.api.CustomStack;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.dependency.Dependencies;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KitData {
    private final Versus plugin;
    private final File kitsFolder;

    public KitData() {
        plugin = Versus.getInstance();
        kitsFolder = new File(plugin.getDataFolder(), "kits");
        if (!kitsFolder.exists()) {
            kitsFolder.mkdirs();
        }
    }

    public void reload() {
        // Dynamic loading when getKit is called
    }

    public Kit getKit(String kitName) {
        File kitFile = new File(kitsFolder, kitName + ".yml");
        if (!kitFile.exists()) return null;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(kitFile);
        try {
            String name = config.getString("display.default.name", kitName);
            ItemStack displayItem = getItemStack(config.getConfigurationSection("display.default"));
            if (displayItem == null) displayItem = new ItemStack(Material.DIAMOND_SWORD);
            
            ItemStack[] items = new ItemStack[41];
            ConfigurationSection itemsSec = config.getConfigurationSection("items");
            if (itemsSec != null) {
                for (String key : itemsSec.getKeys(false)) {
                    int slot = Integer.parseInt(key);
                    items[slot] = getItemStack(itemsSec.getConfigurationSection(key));
                }
            }
            return new Kit(name, items, displayItem);
        } catch (Exception e) {
            Versus.error("Failed retrieving kit " + kitName + ". Check YAML formatting.");
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Kit> getAllKits() {
        ArrayList<Kit> loadedKits = new ArrayList<>();
        File[] files = kitsFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".yml")) {
                    Kit k = getKit(file.getName().replace(".yml", ""));
                    if (k != null) loadedKits.add(k);
                }
            }
        }
        return loadedKits;
    }

    public void saveKit(Kit kit) {
        File kitFile = new File(kitsFolder, kit.getName() + ".yml");
        YamlConfiguration config = new YamlConfiguration();
        
        ConfigurationSection displaySec = config.createSection("display.default");
        displaySec.set("name", kit.getName());
        saveItemStack(kit.getDisplayItem(), displaySec);
        
        ConfigurationSection itemsSec = config.createSection("items");
        ItemStack[] items = kit.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null && items[i].getType() != Material.AIR) {
                ConfigurationSection itemSec = itemsSec.createSection(String.valueOf(i));
                saveItemStack(items[i], itemSec);
            }
        }
        
        try {
            config.save(kitFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteKit(Kit kit) {
        deleteKit(kit.getName());
    }

    public void deleteKit(String kitName) {
        File kitFile = new File(kitsFolder, kitName + ".yml");
        if (kitFile.exists()) kitFile.delete();
    }

    private ItemStack getItemStack(ConfigurationSection section) {
        if (section == null) return null;
        String id = section.getString("id");
        if (id == null) return null;
        int amount = section.getInt("amount", 1);
        
        ItemStack item;
        if (Dependencies.ITEMS_ADDER_ENABLED && CustomStack.isInRegistry(id)) {
            CustomStack stack = CustomStack.getInstance(id);
            if (stack != null) {
                item = stack.getItemStack();
                item.setAmount(amount);
            } else {
                item = new ItemStack(Material.STONE);
            }
        } else {
            Material mat = Material.matchMaterial(id.toUpperCase());
            if (mat == null) mat = Material.STONE;
            item = new ItemStack(mat, amount);
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String name = section.getString("name");
            if (name != null) meta.setDisplayName(MessageUtil.color(name));
            
            if (section.contains("enchants")) {
                for (String enchString : section.getStringList("enchants")) {
                    String[] parts = enchString.split(";");
                    if (parts.length == 2) {
                        try {
                            Enchantment ench = Enchantment.getByName(parts[0].toUpperCase());
                            if (ench != null) {
                                int level = Integer.parseInt(parts[1]);
                                meta.addEnchant(ench, level, true);
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
            
            if (meta instanceof PotionMeta && section.contains("potion_type")) {
                String potionTypeStr = section.getString("potion_type");
                try {
                    PotionType type = PotionType.valueOf(potionTypeStr.toUpperCase());
                    ((PotionMeta) meta).setBasePotionData(new PotionData(type));
                } catch (Exception ignored) {}
            }
            
            item.setItemMeta(meta);
        }
        return item;
    }

    private void saveItemStack(ItemStack item, ConfigurationSection section) {
        if (item == null || item.getType() == Material.AIR) return;
        
        String id = item.getType().name();
        if (Dependencies.ITEMS_ADDER_ENABLED) {
            CustomStack customStack = CustomStack.byItemStack(item);
            if (customStack != null) {
                id = customStack.getId();
            }
        }
        section.set("id", id);
        if (item.getAmount() > 1) section.set("amount", item.getAmount());
        
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            
            if (meta.hasEnchants()) {
                List<String> enchants = new ArrayList<>();
                for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                    enchants.add(entry.getKey().getName() + ";" + entry.getValue());
                }
                section.set("enchants", enchants);
            }
            
            if (meta instanceof PotionMeta) {
                PotionData potionData = ((PotionMeta) meta).getBasePotionData();
                if (potionData != null && potionData.getType() != null) {
                    section.set("potion_type", potionData.getType().name());
                }
            }
        }
    }
}
