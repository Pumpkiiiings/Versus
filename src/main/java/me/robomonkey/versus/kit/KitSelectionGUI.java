package me.robomonkey.versus.kit;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.buttons.SGButtonListener;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import com.samjakob.spigui.toolbar.SGToolbarButtonType;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.util.MenuManager;
import me.robomonkey.versus.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class KitSelectionGUI {

    private KitManager kitManager = KitManager.getInstance();
    private Player viewer;
    private Kit selectedKit;
    private SGMenu mainMenu;
    private SGButton EMPTY;
    private List<Kit> allowedKits = null;

    public KitSelectionGUI(Player viewer, BiConsumer<Kit, Player> onSelect) {
        this(viewer, null, onSelect);
    }

    public KitSelectionGUI(Player viewer, List<Kit> allowedKits, BiConsumer<Kit, Player> onSelect) {
        this.viewer = viewer;
        this.allowedKits = allowedKits;
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("kit_selection.yml");
        
        String empMatStr = config.getString("empty-button.material", "WHITE_STAINED_GLASS_PANE");
        Material empMat = Material.matchMaterial(empMatStr) != null ? Material.matchMaterial(empMatStr) : Material.WHITE_STAINED_GLASS_PANE;
        EMPTY = new SGButton(new ItemBuilder(empMat).name(MessageUtil.color(config.getString("empty-button.name", " "))).build());
        
        String title = config.getString("title", "Kits (Page {currentPage}/{maxPage})");
        int rows = config.getInt("rows", 2);
        
        mainMenu = Versus.spiGUI.create(MessageUtil.color(title), rows);
        mainMenu.setAutomaticPaginationEnabled(true);
        mainMenu.setToolbarBuilder((slot, page, type, sgMenu) -> {
            if (type == SGToolbarButtonType.CURRENT_BUTTON) {
                String confMatStr = config.getString("confirm-button.material", "LIME_STAINED_GLASS_PANE");
                Material confMat = Material.matchMaterial(confMatStr) != null ? Material.matchMaterial(confMatStr) : Material.LIME_STAINED_GLASS_PANE;
                String confName = config.getString("confirm-button.name", "&a&lCONFIRM");
                List<String> confLore = config.getStringList("confirm-button.lore").stream().map(MessageUtil::color).collect(Collectors.toList());
                
                ItemStack confirmIcon = new ItemBuilder(confMat).amount(1).name(MessageUtil.color(confName)).lore(confLore.toArray(new String[0])).build();
                SGButton confirmButton = new SGButton(confirmIcon).withListener(listener -> {
                    if (selectedKit == null) selectedKit = KitManager.getInstance().getDefaultKit();
                    onSelect.accept(selectedKit, viewer);
                    viewer.closeInventory();
                });
                return confirmButton;
            } else if (type == SGToolbarButtonType.UNASSIGNED || sgMenu.getMaxPage() == 1) {
                return EMPTY;
            } else {
                return Versus.spiGUI.getDefaultToolbarBuilder().buildToolbarButton(slot, page, type, sgMenu);
            }
        });
        kitManager.verifyDefaultKit();
        selectedKit = kitManager.getDefaultKit();
        loadKits();
    }

    public void open() {
        viewer.openInventory(mainMenu.getInventory());
    }

    private void toggleSelection(Kit kit) {
        if (kit.equals(selectedKit)) {
            selectedKit = null;
        } else {
            selectedKit = kit;
        }
    }

    public void loadKits() {
        mainMenu.clearAllButStickiedSlots();
        kitManager.getAllKits().stream()
                .filter(kit -> allowedKits == null || allowedKits.contains(kit))
                .forEach(kit -> {
                    boolean selected = kit.equals(selectedKit);
                    mainMenu.addButton(getKitButton(kit, selected));
                });
    }

    private SGButton getKitButton(Kit kit, boolean selected) {
        SGButtonListener listener = (inventoryClickEvent) -> {
            if (inventoryClickEvent.getClick().isLeftClick()) {
                toggleSelection(kit);
                loadKits();
                mainMenu.refreshInventory(viewer);
            } else if (inventoryClickEvent.getClick().isRightClick()) {
                openViewingGUI(kit);
            }
        };
        FileConfiguration config = MenuManager.getInstance().getMenuConfig("kit_selection.yml");
        String loreSel = MessageUtil.color(config.getString("kit-icon.lore-selected", "&a&lSELECTED"));
        String loreUnsel = MessageUtil.color(config.getString("kit-icon.lore-unselected", "&7UNSELECTED"));
        List<String> loreAppend = config.getStringList("kit-icon.lore-append");
        
        List<String> finalLore = new ArrayList<>();
        finalLore.add(selected ? loreSel : loreUnsel);
        for (String s : loreAppend) {
            finalLore.add(MessageUtil.color(s.replace("{kit}", kit.getName())));
        }
        
        ItemStack displayItem = kit.getDisplayItem();
        ItemStack kitIcon = new ItemBuilder(displayItem.getType())
                .name(MessageUtil.color("&p" + kit.getName()))
                .lore(finalLore.toArray(new String[0]))
                .build();
        SGButton kitButton = new SGButton(kitIcon);
        kitButton.withListener(listener);
        return kitButton;
    }

    public void openViewingGUI(Kit kit) {
        FileConfiguration viewerConfig = MenuManager.getInstance().getMenuConfig("kit_viewer.yml");
        String title = viewerConfig.getString("title", "Viewing {kit}").replace("{kit}", kit.getName());
        int rows = viewerConfig.getInt("rows", 6);
        
        SGMenu viewingGUI = Versus.spiGUI.create(MessageUtil.color(title), rows);
        IntStream.range((rows - 1) * 9, rows * 9).forEach(index -> {
            viewingGUI.setButton(index, EMPTY);
        });
        
        String exitMatStr = viewerConfig.getString("exit-button.material", "BARRIER");
        Material exitMat = Material.matchMaterial(exitMatStr) != null ? Material.matchMaterial(exitMatStr) : Material.BARRIER;
        String exitName = viewerConfig.getString("exit-button.name", "&c&lExit");
        List<String> exitLore = viewerConfig.getStringList("exit-button.lore").stream().map(MessageUtil::color).collect(Collectors.toList());
        int exitSlot = viewerConfig.getInt("exit-button.slot", (rows - 1) * 9 + 4);
        
        ItemStack exitIcon = new ItemBuilder(exitMat).amount(1)
                .name(MessageUtil.color(exitName))
                .lore(exitLore.toArray(new String[0])).build();
        SGButton exitButton = new SGButton(exitIcon).withListener(inventoryClickEvent -> this.open());
        viewingGUI.setButton(exitSlot, exitButton);
        for (int index = 0; index < kit.getItems().length; index++) {
            SGButton itemButton = new SGButton(kit.getItems()[index]);
            viewingGUI.setButton(index, itemButton);
        }
        viewer.openInventory(viewingGUI.getInventory());
    }
}
