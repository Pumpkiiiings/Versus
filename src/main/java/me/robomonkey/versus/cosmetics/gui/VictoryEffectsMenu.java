package me.robomonkey.versus.cosmetics.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.cosmetics.VictoryEffect;
import me.robomonkey.versus.cosmetics.CosmeticsManager;
import me.robomonkey.versus.duel.playerdata.PlayerStats;
import me.robomonkey.versus.duel.playerdata.StatsManager;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class VictoryEffectsMenu {
    private final Player viewer;
    private final SGMenu menu;

    public VictoryEffectsMenu(Player viewer) {
        this.viewer = viewer;
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("cosmetics_victory.yml");
        String title = config.getString("title", "&bEfectos de Victoria");
        int rows = config.getInt("rows", 3);
        
        this.menu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        loadEffects();
    }

    private void loadEffects() {
        menu.clearAllButStickiedSlots();
        PlayerStats stats = StatsManager.getInstance().getStats(viewer);
        String currentEffect = stats != null ? stats.getActiveVictoryEffect() : "V_NONE";

        FileConfiguration config = MenuManager.getInstance().getMenuConfig("cosmetics_victory.yml");
        String loreSel = MessageUtil.color(config.getString("effect-lore.selected", "&a&lSELECCIONADO"));
        String loreUnsel = MessageUtil.color(config.getString("effect-lore.unselected", "&7Haz clic para seleccionar"));

        for (VictoryEffect effect : CosmeticsManager.getInstance().getVictoryEffects()) {
            boolean isSelected = effect.getId().equals(currentEffect);
            boolean isOwned = effect.getPrice() <= 0 || (stats != null && stats.hasCosmetic(effect.getId()));
            
            ItemBuilder iconBuilder = new ItemBuilder(effect.getIcon())
                    .name(MessageUtil.color(effect.getDisplayName()));

            if (isOwned) {
                iconBuilder.lore("", isSelected ? loreSel : loreUnsel);
                if (effect.getPrice() <= 0 && !isSelected) {
                    iconBuilder.lore(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.COSMETIC_LORE_FREE));
                }
            } else {
                iconBuilder.lore("", 
                    me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.COSMETIC_LORE_PRICE, me.robomonkey.versus.settings.Placeholder.of("%price%", String.valueOf(effect.getPrice()))),
                    me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.COSMETIC_LORE_BUY)
                );
            }

            SGButton btn = new SGButton(iconBuilder.build()).withListener(e -> {
                if (stats != null) {
                    if (isOwned) {
                        stats.setActiveVictoryEffect(effect.getId());
                        StatsManager.getInstance().savePlayer(stats);
                        loadEffects();
                        menu.refreshInventory(viewer);
                        me.robomonkey.versus.util.EffectUtil.playSound(viewer, org.bukkit.Sound.UI_BUTTON_CLICK);
                    } else {
                        if (!me.robomonkey.versus.dependency.EconomyManager.isAvailable()) {
                            viewer.sendMessage(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ERROR_ECONOMY_DISABLED));
                            return;
                        }
                        if (me.robomonkey.versus.dependency.EconomyManager.withdraw(viewer, effect.getPrice())) {
                            stats.unlockCosmetic(effect.getId());
                            stats.setActiveVictoryEffect(effect.getId());
                            StatsManager.getInstance().savePlayer(stats);
                            viewer.sendMessage(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.COSMETIC_BOUGHT_SUCCESS, 
                                me.robomonkey.versus.settings.Placeholder.of("%cosmetic%", effect.getDisplayName()),
                                me.robomonkey.versus.settings.Placeholder.of("%price%", String.valueOf(effect.getPrice()))));
                            me.robomonkey.versus.util.EffectUtil.playSound(viewer, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP);
                            loadEffects();
                            menu.refreshInventory(viewer);
                        } else {
                            viewer.sendMessage(me.robomonkey.versus.settings.Settings.getMessage(me.robomonkey.versus.settings.Setting.ERROR_NOT_ENOUGH_MONEY));
                            me.robomonkey.versus.util.EffectUtil.playSound(viewer, org.bukkit.Sound.ENTITY_VILLAGER_NO);
                        }
                    }
                }
            });
            menu.addButton(btn);
        }

        // Back button
        Material backMat = Material.matchMaterial(config.getString("back-button.material", "ARROW"));
        if (backMat == null) backMat = Material.ARROW;
        String backName = config.getString("back-button.name", "&cVolver");
        List<String> backLore = config.getStringList("back-button.lore").stream().map(MessageUtil::color).collect(Collectors.toList());
        int backSlot = config.getInt("back-button.slot", 22);

        SGButton backBtn = new SGButton(new ItemBuilder(backMat).name(MessageUtil.color(backName)).lore(backLore.toArray(new String[0])).build())
                .withListener(e -> new CosmeticsMenu(viewer).open());
        menu.setButton(backSlot, backBtn);
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }
}
