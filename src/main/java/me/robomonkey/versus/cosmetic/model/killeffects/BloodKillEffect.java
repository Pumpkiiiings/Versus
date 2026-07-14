package me.robomonkey.versus.cosmetic.model.killeffects;

import me.robomonkey.versus.cosmetic.model.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class BloodKillEffect extends KillEffect {
    public BloodKillEffect() {
        super("K_" + "Blood".toUpperCase(), "Blood", Material.REDSTONE);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.BLOCK, location.clone().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0, org.bukkit.Bukkit.createBlockData(org.bukkit.Material.REDSTONE_BLOCK));
    }
}
