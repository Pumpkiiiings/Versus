package me.robomonkey.versus.betting.gui;

import me.robomonkey.versus.betting.manager.BettingManager;
import me.robomonkey.versus.betting.model.BettingSession;

import me.robomonkey.versus.Versus;
import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BettingGUI implements Listener {

    private final BettingSession session;
    private final Player player;
    private Inventory inventory;

    private static final int READY_BUTTON_SLOT = 22;
    private static final int MONEY_BUTTON_SLOT = 39;
    private static final int XP_BUTTON_SLOT = 41;
    private static final int ENEMY_MONEY_SLOT = 48;
    private static final int ENEMY_XP_SLOT = 50;

    public BettingGUI(BettingSession session, Player player) {
        this.session = session;
        this.player = player;
        Bukkit.getPluginManager().registerEvents(this, Versus.getInstance());
    }

    public void open() {
        Player opponent = player.getUniqueId().equals(session.getPlayer1()) ? session.getPlayer2Online() : session.getPlayer1Online();
        org.bukkit.configuration.file.FileConfiguration config = me.robomonkey.versus.util.MenuManager.getInstance().getMenuConfig("betting_menu.yml");
        String title = config.getString("title", "&8Apuesta vs {opponent}");
        title = title.replace("{opponent}", opponent != null ? opponent.getName() : "Jugador");
        int size = config.getInt("rows", 6) * 9;
        inventory = Bukkit.createInventory(null, size, Versus.color(title));
        updateInventory();
        player.openInventory(inventory);
    }

    private void updateInventory() {
        if (inventory == null) return;
        inventory.clear();
        
        org.bukkit.configuration.file.FileConfiguration config = me.robomonkey.versus.util.MenuManager.getInstance().getMenuConfig("betting_menu.yml");

        boolean isP1 = player.getUniqueId().equals(session.getPlayer1());

        // Draw separator
        String sepMat = config.getString("separator.material", "BLACK_STAINED_GLASS_PANE");
        ItemStack glass = new ItemStack(Material.matchMaterial(sepMat) != null ? Material.matchMaterial(sepMat) : Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(Versus.color(config.getString("separator.name", "&7-")));
        glass.setItemMeta(meta);

        for (int i = 4; i < inventory.getSize(); i += 9) {
            if (i != READY_BUTTON_SLOT) {
                inventory.setItem(i, glass);
            }
        }

        // Draw Ready Button
        boolean myReady = session.isReady(player.getUniqueId());
        String readyPath = myReady ? "ready-button.ready" : "ready-button.unready";
        String readyMat = config.getString(readyPath + ".material", myReady ? "GREEN_WOOL" : "RED_WOOL");
        ItemStack readyBtn = new ItemStack(Material.matchMaterial(readyMat) != null ? Material.matchMaterial(readyMat) : (myReady ? Material.GREEN_WOOL : Material.RED_WOOL));
        ItemMeta readyMeta = readyBtn.getItemMeta();
        readyMeta.setDisplayName(Versus.color(config.getString(readyPath + ".name", myReady ? "&a&l¡LISTO!" : "&c&lNO LISTO")));
        List<String> readyLore = new ArrayList<>();
        if (config.contains(readyPath + ".lore")) {
            for (String line : config.getStringList(readyPath + ".lore")) {
                readyLore.add(Versus.color(line));
            }
        }
        readyMeta.setLore(readyLore);
        readyBtn.setItemMeta(readyMeta);
        inventory.setItem(READY_BUTTON_SLOT, readyBtn);

        // Draw Money and XP buttons (Mine)
        inventory.setItem(MONEY_BUTTON_SLOT, createMoneyItem(isP1, config));
        inventory.setItem(XP_BUTTON_SLOT, createXpItem(isP1, config));

        // Draw Enemy Money and XP (View only)
        inventory.setItem(ENEMY_MONEY_SLOT, createMoneyItem(!isP1, config));
        inventory.setItem(ENEMY_XP_SLOT, createXpItem(!isP1, config));

        // Draw Items
        List<ItemStack> myItems = session.getItems(player.getUniqueId());
        drawItems(myItems, true);
        
        // Draw Enemy Items
        drawItems(session.getItems(isP1 ? session.getPlayer2() : session.getPlayer1()), false);
    }
    
    private void drawItems(List<ItemStack> items, boolean isMine) {
        int[] slotsMine = {0,1,2,3, 9,10,11,12, 18,19,20,21, 27,28,29,30, 36,37,38, 45,46,47};
        int[] slotsEnemy = {5,6,7,8, 14,15,16,17, 23,24,25,26, 32,33,34,35, 42,43,44, 51,52,53};
        
        int[] slots = isMine ? slotsMine : slotsEnemy;
        for (int i = 0; i < Math.min(items.size(), slots.length); i++) {
            inventory.setItem(slots[i], items.get(i));
        }
    }

    private ItemStack createMoneyItem(boolean forP1, org.bukkit.configuration.file.FileConfiguration config) {
        double money = session.getMoney(forP1 ? session.getPlayer1() : session.getPlayer2());
        String matStr = config.getString("money-button.material", "GOLD_INGOT");
        ItemStack item = new ItemStack(Material.matchMaterial(matStr) != null ? Material.matchMaterial(matStr) : Material.GOLD_INGOT);
        ItemMeta meta = item.getItemMeta();
        String name = config.getString("money-button.name", "&eDinero Apostado: &a${money}");
        meta.setDisplayName(Versus.color(name.replace("{money}", String.valueOf(money))));
        List<String> lore = new ArrayList<>();
        if (config.contains("money-button.lore")) {
            for (String line : config.getStringList("money-button.lore")) {
                lore.add(Versus.color(line.replace("{money}", String.valueOf(money))));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createXpItem(boolean forP1, org.bukkit.configuration.file.FileConfiguration config) {
        int xp = session.getXp(forP1 ? session.getPlayer1() : session.getPlayer2());
        String matStr = config.getString("xp-button.material", "EXPERIENCE_BOTTLE");
        ItemStack item = new ItemStack(Material.matchMaterial(matStr) != null ? Material.matchMaterial(matStr) : Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = item.getItemMeta();
        String name = config.getString("xp-button.name", "&bXP Apostada: &a{xp} niveles");
        meta.setDisplayName(Versus.color(name.replace("{xp}", String.valueOf(xp))));
        List<String> lore = new ArrayList<>();
        if (config.contains("xp-button.lore")) {
            for (String line : config.getStringList("xp-button.lore")) {
                lore.add(Versus.color(line.replace("{xp}", String.valueOf(xp))));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getView().getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        if (event.getView().getTopInventory() != inventory) return;
        if (event.getClickedInventory() == null) return;
        
        if (event.getClickedInventory().equals(inventory)) {
            event.setCancelled(true);
            
            // If they are ready, they can't change anything
            if (session.isReady(player.getUniqueId()) && event.getSlot() != READY_BUTTON_SLOT) {
                return;
            }

            if (event.getSlot() == READY_BUTTON_SLOT) {
                // Toggle ready
                boolean current = session.isReady(player.getUniqueId());
                
                // Validate minimum bet before allowing ready
                if (!current) {
                    double myMoney = session.getMoney(player.getUniqueId());
                    int myXp = session.getXp(player.getUniqueId());
                    List<ItemStack> myItems = session.getItems(player.getUniqueId());
                    
                    if (myMoney <= 0 && myXp <= 0 && myItems.isEmpty()) {
                        player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_BET_MINIMUM));
                        return;
                    }
                }
                
                session.setReady(player.getUniqueId(), !current);
                updateInventory();
                
                // Sync to opponent
                syncOpponent();
                
                if (session.areBothReady()) {
                    HandlerList.unregisterAll(this);
                    BettingManager.getInstance().endSession(session, true);
                }
                return;
            }

            if (event.getSlot() == MONEY_BUTTON_SLOT) {
                double currentMoney = session.getMoney(player.getUniqueId());
                if (event.isLeftClick()) {
                    if (me.robomonkey.versus.dependency.EconomyManager.has(player, currentMoney + 100)) {
                        session.setMoney(player.getUniqueId(), currentMoney + 100);
                    } else {
                        player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_NOT_ENOUGH_MONEY));
                    }
                } else if (event.isRightClick()) {
                    session.setMoney(player.getUniqueId(), Math.max(0, currentMoney - 100));
                }
                session.unreadyBoth();
                updateInventory();
                syncOpponent();
            }
            
            if (event.getSlot() == XP_BUTTON_SLOT) {
                int currentXp = session.getXp(player.getUniqueId());
                if (event.isLeftClick()) {
                    if (player.getLevel() >= currentXp + 1) {
                        session.setXp(player.getUniqueId(), currentXp + 1);
                    } else {
                        player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_BET_NOT_ENOUGH_XP));
                    }
                } else if (event.isRightClick()) {
                    session.setXp(player.getUniqueId(), Math.max(0, currentXp - 1));
                }
                session.unreadyBoth();
                updateInventory();
                syncOpponent();
            }
            
            // Allow clicking own item slots to remove items
            int[] slotsMine = {0,1,2,3, 9,10,11,12, 18,19,20,21, 27,28,29,30, 36,37,38, 45,46,47};
            for (int s : slotsMine) {
                if (event.getSlot() == s && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                    List<ItemStack> items = session.getItems(player.getUniqueId());
                    ItemStack toReturn = event.getCurrentItem().clone();
                    items.remove(event.getCurrentItem());
                    player.getInventory().addItem(toReturn);
                    session.setItems(player.getUniqueId(), items);
                    session.unreadyBoth();
                    updateInventory();
                    syncOpponent();
                    break;
                }
            }
            
        } else if (event.getClickedInventory().equals(player.getInventory())) {
            // Player clicks their own inventory to add an item
            if (session.isReady(player.getUniqueId())) {
                event.setCancelled(true);
                return;
            }
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
                event.setCancelled(true);
                
                // Check blacklist
                List<String> blacklist = Settings.getInstance().getConfig().getStringList("dueling.bet_blacklist");
                if (blacklist != null && blacklist.contains(event.getCurrentItem().getType().name())) {
                    player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_BET_BLACKLISTED_ITEM));
                    return;
                }
                
                List<ItemStack> items = session.getItems(player.getUniqueId());
                if (items.size() >= 22) {
                    player.sendMessage(me.robomonkey.versus.config.model.Settings.getMessage(me.robomonkey.versus.config.model.Setting.ERROR_BET_MAX_ITEMS));
                    return;
                }
                items.add(event.getCurrentItem().clone());
                event.getCurrentItem().setAmount(0); // Remove from their inventory
                session.setItems(player.getUniqueId(), items);
                session.unreadyBoth();
                updateInventory();
                syncOpponent();
            }
        }
    }
    
    private void syncOpponent() {
        BettingManager.getInstance().refreshOpponent(player, session);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!event.getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        if (!event.getInventory().equals(inventory)) return;
        
        if (session.areBothReady()) return; // Already ending successfully

        HandlerList.unregisterAll(this);
        
        // If one closes without both being ready, cancel the session
        Bukkit.getScheduler().runTask(Versus.getInstance(), () -> {
            BettingManager.getInstance().endSession(session, false);
        });
    }
    
    public void refresh() {
        updateInventory();
    }
}
