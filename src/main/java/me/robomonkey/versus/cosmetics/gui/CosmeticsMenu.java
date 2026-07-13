package me.robomonkey.versus.cosmetics.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class CosmeticsMenu {
    private final Player viewer;
    private final SGMenu menu;

    public CosmeticsMenu(Player viewer) {
        this.viewer = viewer;
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("cosmetics_main.yml");
        
        String title = config.getString("title", "&8Menú de Cosméticos");
        int rows = config.getInt("rows", 3);
        
        this.menu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        
        // Kill Button
        Material killMat = Material.matchMaterial(config.getString("kill-button.material", "DIAMOND_SWORD"));
        if (killMat == null) killMat = Material.DIAMOND_SWORD;
        String killName = config.getString("kill-button.name", "&c&lEfectos de Asesinato");
        List<String> killLore = config.getStringList("kill-button.lore").stream().map(MessageUtil::color).collect(Collectors.toList());
        int killSlot = config.getInt("kill-button.slot", 11);

        SGButton killButton = new SGButton(new ItemBuilder(killMat)
                .name(MessageUtil.color(killName))
                .lore(killLore.toArray(new String[0]))
                .build())
                .withListener(e -> new KillEffectsMenu(viewer).open());

        // Victory Button
        Material vicMat = Material.matchMaterial(config.getString("victory-button.material", "FIREWORK_ROCKET"));
        if (vicMat == null) vicMat = Material.FIREWORK_ROCKET;
        String vicName = config.getString("victory-button.name", "&b&lEfectos de Victoria");
        List<String> vicLore = config.getStringList("victory-button.lore").stream().map(MessageUtil::color).collect(Collectors.toList());
        int vicSlot = config.getInt("victory-button.slot", 15);

        SGButton victoryButton = new SGButton(new ItemBuilder(vicMat)
                .name(MessageUtil.color(vicName))
                .lore(vicLore.toArray(new String[0]))
                .build())
                .withListener(e -> new VictoryEffectsMenu(viewer).open());

        menu.setButton(killSlot, killButton);
        menu.setButton(vicSlot, victoryButton);
        
        // Fill empty
        Material empMat = Material.matchMaterial(config.getString("empty-button.material", "GRAY_STAINED_GLASS_PANE"));
        if (empMat == null) empMat = Material.GRAY_STAINED_GLASS_PANE;
        String empName = config.getString("empty-button.name", " ");
        SGButton empty = new SGButton(new ItemBuilder(empMat).name(MessageUtil.color(empName)).build());
        
        for (int i = 0; i < rows * 9; i++) {
            if (i != killSlot && i != vicSlot) {
                menu.setButton(i, empty);
            }
        }
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }
}
