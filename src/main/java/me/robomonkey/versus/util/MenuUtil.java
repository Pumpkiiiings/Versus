package me.robomonkey.versus.util;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build {@link ItemStack} objects from YAML {@link ConfigurationSection}s.
 *
 * Supported YAML keys:
 * <pre>
 *   material: DIAMOND_SWORD
 *   name: "&aName"
 *   lore:
 *     - "&7Line 1"
 *   flags:
 *     - HIDE_ATTRIBUTES
 *     - HIDE_ENCHANTS
 * </pre>
 */
public class MenuUtil {

    private MenuUtil() {}

    /**
     * Builds an ItemStack from a YAML section.
     *
     * @param section  the config section containing item properties
     * @param fallback the Material to use if the configured one is invalid
     * @return a fully built ItemStack
     */
    public static ItemStack buildItem(ConfigurationSection section, Material fallback) {
        if (section == null) return new ItemStack(fallback);

        // Material
        Material mat = fallback;
        String matName = section.getString("material");
        if (matName != null) {
            Material parsed = Material.matchMaterial(matName);
            if (parsed != null) mat = parsed;
        }

        // Name
        String name = MessageUtil.color(section.getString("name", " "));

        // Lore
        List<String> rawLore = section.getStringList("lore");
        List<String> lore = new ArrayList<>();
        for (String line : rawLore) {
            lore.add(MessageUtil.color(line));
        }

        // Build item
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (!lore.isEmpty()) meta.setLore(lore);

            // ItemFlags (e.g. HIDE_ATTRIBUTES)
            List<String> flagNames = section.getStringList("flags");
            for (String flagName : flagNames) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(flagName.toUpperCase());
                    meta.addItemFlags(flag);
                } catch (IllegalArgumentException ignored) {
                    // Unknown flag — skip silently
                }
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Convenience overload using GRAY_STAINED_GLASS_PANE as fallback.
     */
    public static ItemStack buildItem(ConfigurationSection section) {
        return buildItem(section, Material.GRAY_STAINED_GLASS_PANE);
    }
}
