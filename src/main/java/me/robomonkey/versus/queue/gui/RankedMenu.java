package me.robomonkey.versus.queue.gui;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.kit.manager.KitManager;
import me.robomonkey.versus.kit.model.Kit;
import me.robomonkey.versus.queue.QueueManager;
import me.robomonkey.versus.ranked.manager.RankManager;
import me.robomonkey.versus.ranked.model.Rank;
import me.robomonkey.versus.storage.manager.StatsManager;
import me.robomonkey.versus.storage.model.PlayerStats;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class RankedMenu {
    private final Player viewer;
    private final SGMenu menu;

    public RankedMenu(Player viewer) {
        this.viewer = viewer;

        FileConfiguration config = MenuManager.getInstance().getMenuConfig("ranked_queue.yml");

        String title = config.getString("title", "&6⚔ &lRanked Matchmaking &6⚔");
        int rows = config.getInt("rows", 3);

        this.menu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        this.menu.setAutomaticPaginationEnabled(true);
        loadKits(config);
    }

    public void open() {
        viewer.openInventory(menu.getInventory());
    }

    private void loadKits(FileConfiguration config) {
        menu.clearAllButStickiedSlots();
        PlayerStats stats = StatsManager.getInstance().getStats(viewer);

        // Empty-slot button
        String empMatStr = config.getString("empty-button.material", "GRAY_STAINED_GLASS_PANE");
        Material empMat = Material.matchMaterial(empMatStr) != null ? Material.matchMaterial(empMatStr) : Material.GRAY_STAINED_GLASS_PANE;
        SGButton emptyBtn = new SGButton(new ItemBuilder(empMat).name(" ").build());

        for (Kit kit : KitManager.getInstance().getAllKits()) {
            int elo = stats.getElo(kit.getName());

            String iconName = MessageUtil.color(
                    fill(config.getString("kit-icon.name", "&p{kit} Ranked"), kit, elo));

            List<String> lore = config.getStringList("kit-icon.lore").stream()
                    .map(line -> MessageUtil.color(fill(line, kit, elo)))
                    .collect(Collectors.toList());

            ItemStack displayItem = kit.getDisplayItem();
            ItemStack kitIcon = new ItemBuilder(displayItem.getType())
                    .name(iconName)
                    .lore(lore.toArray(new String[0]))
                    .build();

            SGButton button = new SGButton(kitIcon).withListener(event -> {
                viewer.closeInventory();
                QueueManager.getInstance().addPlayer(viewer, kit, elo);
            });
            menu.addButton(button);
        }
    }

    /**
     * Substitutes the placeholders available to ranked_queue.yml.
     */
    private String fill(String line, Kit kit, int elo) {
        RankManager rankManager = RankManager.getInstance();
        Rank next = rankManager.getNextRank(elo);
        return line.replace("{kit}", kit.getName())
                .replace("{elo}", String.valueOf(elo))
                .replace("{rank}", rankManager.getDisplayName(elo))
                .replace("{next_rank}", next != null ? next.getDisplayName() : "")
                .replace("{progress}", String.valueOf(rankManager.getEloToNextRank(elo)));
    }
}
