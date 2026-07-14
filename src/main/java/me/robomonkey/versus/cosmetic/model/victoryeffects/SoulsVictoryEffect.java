package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class SoulsVictoryEffect extends VictoryEffect {
    public SoulsVictoryEffect() {
        super("V_" + "Souls".toUpperCase(), "Souls", Material.SOUL_SAND);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.SOUL, location.clone().add(0, 1, 0), 50, 1, 1, 1, 0.1); location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_GHAST_AMBIENT, 1.0f, 1.0f);
    }
}
