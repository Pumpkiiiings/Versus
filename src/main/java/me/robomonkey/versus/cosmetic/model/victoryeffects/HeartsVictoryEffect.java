package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class HeartsVictoryEffect extends VictoryEffect {
    public HeartsVictoryEffect() {
        super("V_" + "Hearts".toUpperCase(), "Hearts", Material.POPPY);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.HEART, location.clone().add(0, 2, 0), 30, 1, 1, 1, 0);
    }
}
