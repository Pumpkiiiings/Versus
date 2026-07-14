package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class FlamesVictoryEffect extends VictoryEffect {
    public FlamesVictoryEffect() {
        super("V_" + "Flames".toUpperCase(), "Flames", Material.BLAZE_POWDER);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.FLAME, location.clone().add(0, 1, 0), 100, 1.5, 0.5, 1.5, 0.1);
    }
}
