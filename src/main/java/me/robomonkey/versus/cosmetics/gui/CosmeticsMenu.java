package me.robomonkey.versus.cosmetics.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MenuUtil;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class CosmeticsMenu {
    private final Player viewer;
    private final SGMenu menu;

    public CosmeticsMenu(Player viewer) {
        this.viewer = viewer;
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("cosmetics_main.yml");

        String title = config.getString("title", "&8Menú de Cosméticos");
        int rows = Math.max(1, Math.min(6, config.getInt("rows", 3)));

        this.menu = Versus.spiGUI.create(MessageUtil.color(title), rows);

        // Kill button
        ConfigurationSection killSec = config.getConfigurationSection("kill-button");
        int killSlot = config.getInt("kill-button.slot", 11);
        ItemStack killItem = MenuUtil.buildItem(killSec);
        menu.setButton(killSlot, new SGButton(killItem).withListener(e -> new KillEffectsMenu(viewer).open()));

        // Victory button
        ConfigurationSection vicSec = config.getConfigurationSection("victory-button");
        int vicSlot = config.getInt("victory-button.slot", 15);
        ItemStack vicItem = MenuUtil.buildItem(vicSec);
        menu.setButton(vicSlot, new SGButton(vicItem).withListener(e -> new VictoryEffectsMenu(viewer).open()));

        // Empty filler
        ConfigurationSection emptySec = config.getConfigurationSection("empty-button");
        if (emptySec != null && emptySec.getBoolean("enabled", true)) {
            ItemStack emptyItem = MenuUtil.buildItem(emptySec);
            SGButton emptyBtn = new SGButton(emptyItem);
            List<Integer> fillSlots = (List<Integer>) (List<?>) emptySec.getIntegerList("fill-slots");
            if (fillSlots != null && !fillSlots.isEmpty()) {
                for (int slot : fillSlots) {
                    if (menu.getButton(slot) == null) menu.setButton(slot, emptyBtn);
                }
            } else {
                for (int i = 0; i < rows * 9; i++) {
                    if (i != killSlot && i != vicSlot) menu.setButton(i, emptyBtn);
                }
            }
        }
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }
}
