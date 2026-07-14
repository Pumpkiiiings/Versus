package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class FireworksVictoryEffect extends VictoryEffect {
    public FireworksVictoryEffect() {
        super("V_" + "Fireworks".toUpperCase(), "Fireworks", Material.FIREWORK_ROCKET);
    }
    @Override
    public void play(Location location) {
        me.robomonkey.versus.util.EffectUtil.spawnFireWorks(location, 1, 50, org.bukkit.Color.AQUA);
    }
}
