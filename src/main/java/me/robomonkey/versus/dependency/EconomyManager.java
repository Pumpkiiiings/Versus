package me.robomonkey.versus.dependency;

import me.robomonkey.versus.Versus;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * Thin wrapper around Vault's {@link Economy} API.
 * Only activated when Vault is present on the server.
 */
public class EconomyManager {

    private static Economy economy;
    private static boolean available = false;

    /**
     * Attempts to hook into Vault's economy service.
     * Call once during {@code onEnable} after Dependencies.refresh().
     */
    public static void setup() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            Versus.log("Vault not found – betting feature disabled.");
            return;
        }
        RegisteredServiceProvider<Economy> rsp =
                Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Versus.log("No economy provider registered in Vault – betting feature disabled.");
            return;
        }
        economy = rsp.getProvider();
        available = true;
        Versus.log("Vault economy hooked successfully – betting enabled.");
    }

    /** Returns true if an economy plugin is available. */
    public static boolean isAvailable() {
        return available;
    }

    /** Checks whether {@code player} has at least {@code amount} in their balance. */
    public static boolean has(Player player, double amount) {
        return available && economy.has(player, amount);
    }

    /**
     * Withdraws {@code amount} from {@code player}.
     * Returns true on success.
     */
    public static boolean withdraw(Player player, double amount) {
        if (!available) return false;
        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    /**
     * Deposits {@code amount} to {@code player}.
     * Returns true on success.
     */
    public static boolean deposit(Player player, double amount) {
        if (!available) return false;
        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    /**
     * Formats an amount to a currency string (e.g. "$1,000.00").
     */
    public static String format(double amount) {
        if (!available) return String.valueOf(amount);
        return economy.format(amount);
    }
}
