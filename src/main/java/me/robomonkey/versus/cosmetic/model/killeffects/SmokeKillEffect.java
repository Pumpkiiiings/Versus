package me.robomonkey.versus.cosmetic.model.killeffects;

import me.robomonkey.versus.cosmetic.model.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class SmokeKillEffect extends KillEffect {
    public SmokeKillEffect() {
        super("K_" + "Smoke".toUpperCase(), "Smoke", Material.CAMPFIRE);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.CAMPFIRE_COSY_SMOKE, location.clone().add(0, 1, 0), 40, 0.3, 0.5, 0.3, 0.05);
    }
}
