package me.robomonkey.versus.listener.visibility;

import me.robomonkey.versus.arena.manager.ArenaVisibilityManager;
import me.robomonkey.versus.duel.model.Duel;
import me.robomonkey.versus.duel.manager.DuelManager;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.Effect;

public class EntityTagListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player shooter = (Player) projectile.getShooter();
            Duel duel = DuelManager.getInstance().getDuel(shooter);
            if (duel != null && duel.getArena().isShared()) {
                ArenaVisibilityManager.trackEntity(projectile.getEntityId(), duel);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        Duel duel = DuelManager.getInstance().getDuel(player);
        if (duel != null && duel.getArena().isShared()) {
            ArenaVisibilityManager.trackEntity(event.getItemDrop().getEntityId(), duel);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        ArenaVisibilityManager.untrackEntity(event.getEntity().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemDespawn(ItemDespawnEvent event) {
        ArenaVisibilityManager.untrackEntity(event.getEntity().getEntityId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onItemPickup(EntityPickupItemEvent event) {
        ArenaVisibilityManager.untrackEntity(event.getItem().getEntityId());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ArenaVisibilityManager.updateVisibility(event.getPlayer());
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ArenaVisibilityManager.removeSpectator(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPotionSplash(PotionSplashEvent event) {
        Projectile projectile = event.getEntity();
        if (!(projectile.getShooter() instanceof Player)) return;

        Player shooter = (Player) projectile.getShooter();
        Duel shooterDuel = DuelManager.getInstance().getDuel(shooter);

        if (shooterDuel != null && shooterDuel.getArena().isShared()) {
            // Remove affected entities not in the same duel
            event.getAffectedEntities().removeIf(entity -> {
                if (!(entity instanceof Player)) return true;
                Player p = (Player) entity;
                Duel pDuel = DuelManager.getInstance().getDuel(p);
                return pDuel == null || !pDuel.equals(shooterDuel);
            });

            // Cancel the event so vanilla doesn't broadcast sound/particles to everyone
            event.setCancelled(true);
            
            // Manually apply potion effects to the filtered players
            event.getAffectedEntities().forEach(entity -> {
                event.getPotion().getEffects().forEach(effect -> {
                    double intensity = event.getIntensity(entity);
                    int duration = (int) (effect.getDuration() * intensity);
                    if (duration > 0) {
                        entity.addPotionEffect(new org.bukkit.potion.PotionEffect(
                                effect.getType(), duration, effect.getAmplifier(),
                                effect.isAmbient(), effect.hasParticles(), effect.hasIcon()));
                    }
                });
            });

            // Manually play the break effect only to the players in the duel and spectators
            org.bukkit.Location loc = projectile.getLocation();
            for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
                if (ArenaVisibilityManager.canSee(shooter, p)) {
                    // Play effect (2002 is potion break)
                    p.playEffect(loc, Effect.POTION_BREAK, event.getPotion().getItem().getDurability());
                }
            }
        }
    }
}
