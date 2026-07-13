package me.robomonkey.versus.cosmetics.killeffects;

import me.robomonkey.versus.cosmetics.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class ExplosionKillEffect extends KillEffect {
    public ExplosionKillEffect() {
        super("K_" + "Explosion".toUpperCase(), "Explosion", Material.TNT);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_NORMAL, location, 1); location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
    }
}
