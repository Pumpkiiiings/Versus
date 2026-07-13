package me.robomonkey.versus.cosmetics.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.cosmetics.KillEffect;
import me.robomonkey.versus.cosmetics.CosmeticsManager;
import me.robomonkey.versus.dependency.EconomyManager;
import me.robomonkey.versus.duel.playerdata.PlayerStats;
import me.robomonkey.versus.duel.playerdata.StatsManager;
import me.robomonkey.versus.settings.Placeholder;
import me.robomonkey.versus.settings.Setting;
import me.robomonkey.versus.settings.Settings;
import me.robomonkey.versus.util.EffectUtil;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MenuUtil;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class KillEffectsMenu {
    private final Player viewer;
    private final SGMenu menu;
    private final int rows;

    public KillEffectsMenu(Player viewer) {
        this.viewer = viewer;
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("cosmetics_kill.yml");
        String title = config.getString("title", "&cEfectos de Asesinato");
        this.rows = Math.max(1, Math.min(6, config.getInt("rows", 4)));
        this.menu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        loadEffects();
    }

    private void loadEffects() {
        menu.clearAllButStickiedSlots();
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("cosmetics_kill.yml");
        PlayerStats stats = StatsManager.getInstance().getStats(viewer);
        String currentEffect = stats != null ? stats.getActiveKillEffect() : "K_NONE";

        String loreSel   = MessageUtil.color(config.getString("effect-lore.selected",  "&a&l✔ SELECCIONADO"));
        String loreUnsel = MessageUtil.color(config.getString("effect-lore.unselected", "&7Haz clic para seleccionar"));

        for (KillEffect effect : CosmeticsManager.getInstance().getKillEffects()) {
            boolean isSelected = effect.getId().equals(currentEffect);
            boolean isFree     = effect.getPrice() <= 0;
            boolean isOwned    = isFree || (stats != null && stats.hasCosmetic(effect.getId()));

            ItemStack icon = buildEffectIcon(effect.getIcon() != null ? new ItemStack(effect.getIcon()) : new ItemStack(org.bukkit.Material.BARRIER),
                    effect.getDisplayName(), isOwned, isSelected, isFree, effect.getPrice(), loreSel, loreUnsel);

            SGButton btn = new SGButton(icon).withListener(e -> {
                if (stats == null) return;
                if (isOwned) {
                    stats.setActiveKillEffect(effect.getId());
                    StatsManager.getInstance().savePlayer(stats);
                    loadEffects();
                    menu.refreshInventory(viewer);
                    EffectUtil.playSound(viewer, Sound.UI_BUTTON_CLICK);
                } else {
                    if (!EconomyManager.isAvailable()) {
                        viewer.sendMessage(Settings.getMessage(Setting.ERROR_ECONOMY_DISABLED));
                        return;
                    }
                    if (EconomyManager.withdraw(viewer, effect.getPrice())) {
                        stats.unlockCosmetic(effect.getId());
                        stats.setActiveKillEffect(effect.getId());
                        StatsManager.getInstance().savePlayer(stats);
                        viewer.sendMessage(Settings.getMessage(Setting.COSMETIC_BOUGHT_SUCCESS,
                                Placeholder.of("%cosmetic%", effect.getDisplayName()),
                                Placeholder.of("%price%", EconomyManager.format(effect.getPrice()))));
                        EffectUtil.playSound(viewer, Sound.ENTITY_PLAYER_LEVELUP);
                        loadEffects();
                        menu.refreshInventory(viewer);
                    } else {
                        viewer.sendMessage(Settings.getMessage(Setting.ERROR_NOT_ENOUGH_MONEY));
                        EffectUtil.playSound(viewer, Sound.ENTITY_VILLAGER_NO);
                    }
                }
            });
            menu.addButton(btn);
        }

        // Back button
        ConfigurationSection backSec = config.getConfigurationSection("back-button");
        int backSlot = config.getInt("back-button.slot", 31);
        ItemStack backItem = MenuUtil.buildItem(backSec);
        menu.setButton(backSlot, new SGButton(backItem).withListener(e -> new CosmeticsMenu(viewer).open()));

        // Filler
        ConfigurationSection emptySec = config.getConfigurationSection("empty-button");
        if (emptySec != null && emptySec.getBoolean("enabled", false)) {
            ItemStack emptyItem = MenuUtil.buildItem(emptySec);
            SGButton emptyBtn = new SGButton(emptyItem);
            List<Integer> fillSlots = (List<Integer>) (List<?>) emptySec.getIntegerList("fill-slots");
            if (fillSlots != null && !fillSlots.isEmpty()) {
                for (int slot : fillSlots) {
                    if (menu.getButton(slot) == null) menu.setButton(slot, emptyBtn);
                }
            } else {
                for (int i = 0; i < rows * 9; i++) {
                    if (menu.getButton(i) == null) menu.setButton(i, emptyBtn);
                }
            }
        }
    }

    /** Builds the effect icon with dynamic lore based on ownership/price. */
    private ItemStack buildEffectIcon(ItemStack base, String displayName,
                                      boolean isOwned, boolean isSelected,
                                      boolean isFree, double price,
                                      String loreSel, String loreUnsel) {
        ItemStack item = base.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(MessageUtil.color(displayName));
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (isOwned) {
            lore.add(isSelected ? loreSel : loreUnsel);
            if (isFree && !isSelected) {
                lore.add(Settings.getMessage(Setting.COSMETIC_LORE_FREE));
            }
        } else {
            lore.add(Settings.getMessage(Setting.COSMETIC_LORE_PRICE,
                    Placeholder.of("%price%", EconomyManager.isAvailable()
                            ? EconomyManager.format(price) : String.valueOf(price))));
            lore.add(Settings.getMessage(Setting.COSMETIC_LORE_BUY));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }
}

