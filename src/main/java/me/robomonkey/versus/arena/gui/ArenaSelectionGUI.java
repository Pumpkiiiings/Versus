package me.robomonkey.versus.arena.gui;

import me.robomonkey.versus.arena.manager.ArenaManager;
import me.robomonkey.versus.arena.model.Arena;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;
import me.robomonkey.versus.util.MenuManager;
import org.bukkit.configuration.file.FileConfiguration;

public class ArenaSelectionGUI {
    
    private Player viewer;
    private SGMenu mainMenu;

    public ArenaSelectionGUI(Player viewer, Consumer<Arena> onSelect) {
        this.viewer = viewer;
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("arena_selection.yml");
        
        String title = config.getString("title", "Select Arena");
        int rows = config.getInt("rows", 3);
        
        mainMenu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        mainMenu.setAutomaticPaginationEnabled(true);
        mainMenu.setToolbarBuilder(new me.robomonkey.versus.util.CustomPaginationBuilder());
        
        // Add "Random Arena" button
        String randMatStr = config.getString("random-arena-button.material", "NETHER_STAR");
        Material randMat = Material.matchMaterial(randMatStr) != null ? Material.matchMaterial(randMatStr) : Material.NETHER_STAR;
        String randName = config.getString("random-arena-button.name", "&d&lRandom Arena");
        List<String> randLore = config.getStringList("random-arena-button.lore").stream().map(MessageUtil::color).collect(Collectors.toList());
        
        ItemStack randomIcon = new ItemBuilder(randMat)
                .name(MessageUtil.color(randName))
                .lore(randLore.toArray(new String[0]))
                .build();
        mainMenu.addButton(new SGButton(randomIcon).withListener(e -> {
            onSelect.accept(null); // null means random
        }));

        // Add all enabled arenas
        String arenaMatStr = config.getString("arena-icon.material", "PAPER");
        Material arenaMat = Material.matchMaterial(arenaMatStr) != null ? Material.matchMaterial(arenaMatStr) : Material.PAPER;
        String arenaNameFormat = config.getString("arena-icon.name", "&e&l{arena}");
        List<String> arenaLoreFormat = config.getStringList("arena-icon.lore");
        String availableYes = config.getString("arena-icon.available-yes", "&aYes");
        String availableNo = config.getString("arena-icon.available-no", "&cNo");
        
        ArenaManager.getInstance().getAllArenas().stream()
                .filter(Arena::isEnabled)
                .forEach(arena -> {
                    String finalName = arenaNameFormat.replace("{arena}", arena.getName());
                    String availableStr = arena.isAvailable() ? availableYes : availableNo;
                    List<String> finalLore = arenaLoreFormat.stream()
                            .map(s -> MessageUtil.color(s.replace("{kits}", String.valueOf(arena.getKits().size())).replace("{available}", availableStr)))
                            .collect(Collectors.toList());
                            
                    ItemStack icon = new ItemBuilder(arenaMat)
                            .name(MessageUtil.color(finalName))
                            .lore(finalLore.toArray(new String[0]))
                            .build();
                    mainMenu.addButton(new SGButton(icon).withListener(e -> {
                        onSelect.accept(arena);
                    }));
                });
    }

    public void open() {
        viewer.openInventory(mainMenu.getInventory());
    }
}
