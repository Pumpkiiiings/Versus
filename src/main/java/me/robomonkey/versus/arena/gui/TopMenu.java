package me.robomonkey.versus.arena.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.kit.model.Kit;
import me.robomonkey.versus.storage.manager.LeaderboardManager;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.stream.Collectors;

public class TopMenu {
    private final Player viewer;
    private final Kit kit;
    private final SGMenu menu;

    public TopMenu(Player viewer, Kit kit) {
        this.viewer = viewer;
        this.kit = kit;

        FileConfiguration config = MenuManager.getInstance().getMenuConfig("ranked_top.yml");

        String titleFormat = config.getString("title", "&6🏆 &lTop 10 ELO — {kit} &6🏆");
        String title = titleFormat.replace("{kit}", kit.getName());
        int rows = config.getInt("rows", 3);

        this.menu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        loadTop(config);
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }

    private void loadTop(FileConfiguration config) {
        List<LeaderboardManager.LeaderboardEntry> top = LeaderboardManager.getInstance().getTop(kit.getName());

        // Empty-slot button
        String empMatStr = config.getString("empty-button.material", "GRAY_STAINED_GLASS_PANE");
        Material empMat = Material.matchMaterial(empMatStr) != null ? Material.matchMaterial(empMatStr) : Material.GRAY_STAINED_GLASS_PANE;
        SGButton emptyBtn = new SGButton(new ItemBuilder(empMat).name(" ").build());

        for (int i = 0; i < 10; i++) {
            if (i < top.size()) {
                LeaderboardManager.LeaderboardEntry entry = top.get(i);
                final int rank = i + 1;

                String iconName = MessageUtil.color(
                        config.getString("player-icon.name", "&e#{rank} &b{player}")
                                .replace("{rank}", String.valueOf(rank))
                                .replace("{player}", entry.getPlayerName())
                                .replace("{elo}", String.valueOf(entry.getElo()))
                                .replace("{kit}", kit.getName())
                );

                List<String> lore = config.getStringList("player-icon.lore").stream()
                        .map(line -> MessageUtil.color(
                                line.replace("{rank}", String.valueOf(rank))
                                        .replace("{player}", entry.getPlayerName())
                                        .replace("{elo}", String.valueOf(entry.getElo()))
                                        .replace("{kit}", kit.getName())
                        ))
                        .collect(Collectors.toList());

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                if (meta != null) {
                    meta.setOwner(entry.getPlayerName());
                    meta.setDisplayName(iconName);
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                }

                menu.setButton(i, new SGButton(head));
            } else {
                menu.setButton(i, emptyBtn);
            }
        }
    }
}
