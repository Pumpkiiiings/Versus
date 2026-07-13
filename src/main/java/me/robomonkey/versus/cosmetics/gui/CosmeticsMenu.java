package me.robomonkey.versus.cosmetics.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CosmeticsMenu {
    private final Player viewer;
    private final SGMenu menu;

    public CosmeticsMenu(Player viewer) {
        this.viewer = viewer;
        this.menu = Versus.spiGUI.create(MessageUtil.color("&8Menú de Cosméticos"), 3);
        
        SGButton killButton = new SGButton(new ItemBuilder(Material.DIAMOND_SWORD)
                .name(MessageUtil.color("&c&lEfectos de Asesinato"))
                .lore(MessageUtil.color("&7Haz clic para ver y seleccionar"), MessageUtil.color("&7tus efectos de asesinato."))
                .build())
                .withListener(e -> new KillEffectsMenu(viewer).open());

        SGButton victoryButton = new SGButton(new ItemBuilder(Material.FIREWORK_ROCKET)
                .name(MessageUtil.color("&b&lEfectos de Victoria"))
                .lore(MessageUtil.color("&7Haz clic para ver y seleccionar"), MessageUtil.color("&7tus efectos de victoria."))
                .build())
                .withListener(e -> new VictoryEffectsMenu(viewer).open());

        menu.setButton(11, killButton);
        menu.setButton(15, victoryButton);
        
        // Fill empty
        SGButton empty = new SGButton(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build());
        for (int i = 0; i < 27; i++) {
            if (i != 11 && i != 15) {
                menu.setButton(i, empty);
            }
        }
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }
}
