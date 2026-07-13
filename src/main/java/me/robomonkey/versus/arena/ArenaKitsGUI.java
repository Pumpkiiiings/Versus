package me.robomonkey.versus.arena;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.buttons.SGButtonListener;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.kit.Kit;
import me.robomonkey.versus.kit.KitManager;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArenaKitsGUI {
    
    private Player viewer;
    private Arena arena;
    private List<Kit> selectedKits;
    private SGMenu mainMenu;
    private SGButton EMPTY;

    public ArenaKitsGUI(Player viewer, Arena arena, Runnable onConfirm) {
        this.viewer = viewer;
        this.arena = arena;
        this.selectedKits = new ArrayList<>(arena.getKits());
        
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("arena_kits.yml");
        
        String empMatStr = config.getString("empty-button.material", "WHITE_STAINED_GLASS_PANE");
        Material empMat = Material.matchMaterial(empMatStr) != null ? Material.matchMaterial(empMatStr) : Material.WHITE_STAINED_GLASS_PANE;
        EMPTY = new SGButton(new ItemBuilder(empMat).name(MessageUtil.color(config.getString("empty-button.name", " "))).build());
        
        String titleFormat = config.getString("title", "Edit Kits: {arena}");
        String title = titleFormat.replace("{arena}", arena.getName());
        int rows = config.getInt("rows", 2);
        
        mainMenu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        mainMenu.setAutomaticPaginationEnabled(true);
        mainMenu.setToolbarBuilder((slot, page, type, sgMenu) -> {
            if (type == SGToolbarButtonType.CURRENT_BUTTON) {
                String confMatStr = config.getString("confirm-button.material", "LIME_STAINED_GLASS_PANE");
                Material confMat = Material.matchMaterial(confMatStr) != null ? Material.matchMaterial(confMatStr) : Material.LIME_STAINED_GLASS_PANE;
                String confName = config.getString("confirm-button.name", "&a&lCONFIRM");
                List<String> confLore = config.getStringList("confirm-button.lore").stream().map(MessageUtil::color).collect(java.util.stream.Collectors.toList());
                
                ItemStack confirmIcon = new ItemBuilder(confMat).amount(1)
                        .name(MessageUtil.color(confName))
                        .lore(confLore.toArray(new String[0]))
                        .build();
                return new SGButton(confirmIcon).withListener(listener -> {
                    arena.setKits(selectedKits);
                    if (onConfirm != null) onConfirm.run();
                    viewer.closeInventory();
                });
            } else if (type == SGToolbarButtonType.UNASSIGNED || sgMenu.getMaxPage() == 1) {
                return EMPTY;
            } else {
                return Versus.spiGUI.getDefaultToolbarBuilder().buildToolbarButton(slot, page, type, sgMenu);
            }
        });
        
        loadKits();
    }

    public void open() {
        viewer.openInventory(mainMenu.getInventory());
    }

    private void loadKits() {
        mainMenu.clearAllButStickiedSlots();
        KitManager.getInstance().getAllKits().forEach(kit -> {
            boolean selected = selectedKits.contains(kit);
            mainMenu.addButton(getKitButton(kit, selected));
        });
    }

    private SGButton getKitButton(Kit kit, boolean selected) {
        SGButtonListener listener = (e) -> {
            if (selectedKits.contains(kit)) {
                selectedKits.remove(kit);
            } else {
                selectedKits.add(kit);
            }
            loadKits();
            mainMenu.refreshInventory(viewer);
        };
        
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("arena_kits.yml");
        String loreSel = MessageUtil.color(config.getString("kit-icon.lore-selected", "&a&lSELECTED"));
        String loreUnsel = MessageUtil.color(config.getString("kit-icon.lore-unselected", "&7UNSELECTED"));
        
        List<String> finalLore = new ArrayList<>();
        finalLore.add(selected ? loreSel : loreUnsel);
        
        List<String> appendLore = config.getStringList("kit-icon.lore-append");
        if (appendLore != null) {
            appendLore.forEach(s -> finalLore.add(MessageUtil.color(s)));
        }
        
        ItemStack displayItem = kit.getDisplayItem();
        ItemStack kitIcon = new ItemBuilder(displayItem.getType())
                .name(MessageUtil.color("&p" + kit.getName()))
                .lore(finalLore.toArray(new String[0]))
                .build();
        return new SGButton(kitIcon).withListener(listener);
    }
}
