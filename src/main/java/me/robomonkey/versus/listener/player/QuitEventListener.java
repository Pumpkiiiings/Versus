package me.robomonkey.versus.listener.player;

import me.robomonkey.versus.duel.manager.DuelManager;
import me.robomonkey.versus.storage.manager.StatsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEventListener implements Listener {

    DuelManager duelManager = DuelManager.getInstance();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // The forfeit must run before the stats are unloaded. Unloading first evicts the
        // player from the cache, so the loss would be applied to a blank PlayerStats and
        // that blank record would then overwrite the real one.
        if (duelManager.isDueling(player)) {
            DuelManager.getInstance().registerQuitter(player);
        }
        if (duelManager.isSpectating(player)) {
            duelManager.removeSpectator(player);
        }
        StatsManager.getInstance().unloadPlayer(player);
    }
}
