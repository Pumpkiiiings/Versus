package me.robomonkey.versus.cosmetics.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.cosmetics.KillEffect;
import me.robomonkey.versus.duel.playerdata.PlayerStats;
import me.robomonkey.versus.duel.playerdata.StatsManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KillEffectsMenu {
    private final Player viewer;
    private final SGMenu menu;

    public KillEffectsMenu(Player viewer) {
        this.viewer = viewer;
        this.menu = Versus.spiGUI.create(MessageUtil.color("&cEfectos de Asesinato"), 3);
        loadEffects();
    }

    private void loadEffects() {
        menu.clearAllButStickiedSlots();
        PlayerStats stats = StatsManager.getInstance().getStats(viewer);
        String currentEffect = stats != null ? stats.getActiveKillEffect() : "NONE";

        for (KillEffect effect : KillEffect.values()) {
            boolean isSelected = effect.name().equals(currentEffect);
            
            ItemStack icon = new ItemBuilder(effect.getIcon())
                    .name(MessageUtil.color("&e" + effect.getDisplayName()))
                    .lore(
                            "",
                            isSelected ? MessageUtil.color("&a&lSELECCIONADO") : MessageUtil.color("&7Haz clic para seleccionar")
                    )
                    .build();

            SGButton btn = new SGButton(icon).withListener(e -> {
                if (stats != null) {
                    stats.setActiveKillEffect(effect.name());
                    StatsManager.getInstance().savePlayer(stats);
                    loadEffects();
                    menu.refreshInventory(viewer);
                }
            });
            menu.addButton(btn);
        }

        // Back button
        SGButton backBtn = new SGButton(new ItemBuilder(Material.ARROW).name(MessageUtil.color("&cVolver")).build())
                .withListener(e -> new CosmeticsMenu(viewer).open());
        menu.setButton(22, backBtn);
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }
}
