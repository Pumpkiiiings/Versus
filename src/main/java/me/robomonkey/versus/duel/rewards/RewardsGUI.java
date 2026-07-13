package me.robomonkey.versus.duel.rewards;

import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.menu.SGMenu;
import me.robomonkey.versus.Versus;
import me.robomonkey.versus.dependency.EconomyManager;
import me.robomonkey.versus.util.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * GUI that shows pending duel rewards to the winner.
 * All layout, materials, names, lore and messages are read from menus/rewards_menu.yml
 * — fully editable without recompiling.
 * Anti-dupe is enforced by RewardManager's atomic H2 UPDATE WHERE claimed=FALSE.
 */
public class RewardsGUI {

    public static void open(Player player) {
        FileConfiguration cfg = MenuManager.getInstance().getMenuConfig("rewards_menu.yml");
        List<DuelReward> rewards = RewardManager.getInstance().getUnclaimedRewards(player.getUniqueId());

        int rows = cfg.getInt("menu-size", 6);

        String title = Versus.color(cfg.getString("title", "&6⚔ &lRecompensas de Duelo &6⚔"));
        SGMenu menu = Versus.spiGUI.create(title, rows);

        if (rewards.isEmpty()) {
            String emptyMat = cfg.getString("empty-slot.material", "GRAY_STAINED_GLASS_PANE");
            Material mat = Material.matchMaterial(emptyMat);
            if (mat == null) mat = Material.GRAY_STAINED_GLASS_PANE;
            String emptyName = cfg.getString("empty-slot.name", "&7Sin recompensas pendientes");
            List<String> emptyLore = cfg.getStringList("empty-slot.lore");
            menu.setButton(4, new SGButton(buildItem(mat, emptyName, emptyLore)));
        } else {
            for (int i = 0; i < rewards.size() && i < rows * 9; i++) {
                menu.setButton(i, buildRewardButton(rewards.get(i), cfg));
            }
        }

        player.openInventory(menu.getInventory());
    }

    private static SGButton buildRewardButton(DuelReward reward, FileConfiguration cfg) {
        String loserName  = getPlayerName(reward.getLoserUuid());
        String winnerName = getPlayerName(reward.getWinnerUuid());

        // Date format is configurable — e.g. "dd/MM/yyyy HH:mm" or "MM-dd-yyyy"
        String datePattern = cfg.getString("reward-item.date-format", "dd/MM/yyyy HH:mm");
        SimpleDateFormat fmt;
        try {
            fmt = new SimpleDateFormat(datePattern);
        } catch (Exception e) {
            fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        }
        String dateStr = fmt.format(new Date(reward.getTimestamp()));
        String sep = cfg.getString("reward-item.separator", "&8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");

        long expirationTime = reward.getTimestamp() + (7L * 24 * 60 * 60 * 1000);
        long remaining = expirationTime - System.currentTimeMillis();
        String expiresInStr = formatTimeRemaining(remaining, cfg);

        // Build money/xp/items lines (only included if the reward has them)
        String moneyLine = "";
        if (reward.getMoney() > 0) {
            moneyLine = cfg.getString("reward-item.money-line", "&7Dinero: &6${amount}")
                    .replace("{amount}", EconomyManager.format(reward.getMoney()));
        }
        String xpLine = "";
        if (reward.getXp() > 0) {
            xpLine = cfg.getString("reward-item.xp-line", "&7XP: &b{xp} niveles")
                    .replace("{xp}", String.valueOf(reward.getXp()));
        }
        String itemsLine = "";
        if (reward.hasItems()) {
            itemsLine = cfg.getString("reward-item.items-line", "&7Ítems: &f{count} tipo(s)")
                    .replace("{count}", String.valueOf(reward.getItems().size()));
        }

        // Build lore from the template, substituting placeholders
        List<String> loreTpl = cfg.getStringList("reward-item.lore");
        List<String> lore = new ArrayList<>();
        for (String line : loreTpl) {
            // Skip optional lines that are empty (no money/xp/items)
            if (line.contains("{money_line}") && moneyLine.isEmpty()) continue;
            if (line.contains("{xp_line}") && xpLine.isEmpty()) continue;
            if (line.contains("{items_line}") && itemsLine.isEmpty()) continue;

            String built = line
                    .replace("{winner}", winnerName)
                    .replace("{loser}", loserName)
                    .replace("{date}", dateStr)
                    .replace("{separator}", sep)
                    .replace("{money_line}", moneyLine)
                    .replace("{xp_line}", xpLine)
                    .replace("{items_line}", itemsLine)
                    .replace("{expires_in}", expiresInStr);
            lore.add(built);
        }

        // Item icon
        String matStr = cfg.getString("reward-item.material", "GOLDEN_SWORD");
        Material mat = Material.matchMaterial(matStr);
        if (mat == null) mat = Material.GOLDEN_SWORD;

        String itemName = cfg.getString("reward-item.name", "&6⚔ &lVICTORIA &7vs &c{loser}")
                .replace("{loser}", loserName)
                .replace("{winner}", winnerName);

        ItemStack icon = buildItem(mat, itemName, lore);

        // Freeze references for lambda
        final DuelReward r = reward;
        final FileConfiguration config = cfg;

        return new SGButton(icon).withListener(event -> {
            event.setCancelled(true);
            Player clicker = (Player) event.getWhoClicked();
            clicker.closeInventory();

            // Anti-dupe: atomic UPDATE WHERE claimed=FALSE — only returns true once per reward
            boolean success = RewardManager.getInstance().claimReward(r.getId(), clicker.getUniqueId());
            if (!success) {
                clicker.sendMessage(Versus.color(config.getString(
                        "messages.already-claimed", "&cEsta recompensa ya fue reclamada.")));
                return;
            }

            // Give money immediately (no inventory loss risk)
            if (r.getMoney() > 0) {
                EconomyManager.deposit(clicker, r.getMoney());
                String msg = config.getString("messages.money-received", "&a+&6${amount} &arecibido.")
                        .replace("{amount}", EconomyManager.format(r.getMoney()));
                clicker.sendMessage(Versus.color(msg));
            }

            // Give XP
            if (r.getXp() > 0) {
                clicker.setLevel(clicker.getLevel() + r.getXp());
                String msg = config.getString("messages.xp-received", "&a+{xp} &bniveles de XP &arecibidos.")
                        .replace("{xp}", String.valueOf(r.getXp()));
                clicker.sendMessage(Versus.color(msg));
            }

            // Give items on main thread — drop leftovers if inventory is full
            if (r.hasItems()) {
                Bukkit.getScheduler().runTask(Versus.getInstance(), () -> {
                    for (ItemStack item : r.getItems()) {
                        if (item == null || item.getType() == org.bukkit.Material.AIR) continue;
                        java.util.HashMap<Integer, ItemStack> leftover = clicker.getInventory().addItem(item);
                        for (ItemStack drop : leftover.values()) {
                            clicker.getWorld().dropItem(clicker.getLocation(), drop);
                        }
                    }
                    clicker.sendMessage(Versus.color(config.getString(
                            "messages.items-received", "&a¡Ítems apostados entregados!")));
                });
            }

            clicker.sendMessage(Versus.color(config.getString(
                    "messages.claimed-success", "&a¡Recompensa reclamada exitosamente!")));
            clicker.playSound(clicker.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
        });
    }

    private static ItemStack buildItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName(Versus.color(name));
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(Versus.color(line));
        }
        meta.setLore(coloredLore);
        item.setItemMeta(meta);
        return item;
    }

    private static String getPlayerName(java.util.UUID uuid) {
        Player online = Bukkit.getPlayer(uuid);
        if (online != null) return online.getName();
        org.bukkit.OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
        String name = offline.getName();
        return name != null ? name : uuid.toString().substring(0, 8);
    }

    private static String formatTimeRemaining(long millis, FileConfiguration cfg) {
        if (millis <= 0) return cfg.getString("time-formats.expired", "Expirado");
        long days = millis / (1000 * 60 * 60 * 24);
        long hours = (millis / (1000 * 60 * 60)) % 24;
        long minutes = (millis / (1000 * 60)) % 60;
        
        String dStr = cfg.getString("time-formats.days", "d");
        String hStr = cfg.getString("time-formats.hours", "h");
        String mStr = cfg.getString("time-formats.minutes", "m");
        
        StringBuilder sb = new StringBuilder();
        if (days > 0) sb.append(days).append(dStr).append(" ");
        if (hours > 0 || days > 0) sb.append(hours).append(hStr).append(" ");
        sb.append(minutes).append(mStr);
        
        String result = sb.toString().trim();
        return result.isEmpty() ? "< 1" + mStr : result;
    }
}
