package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class DragonVictoryEffect extends VictoryEffect {
    public DragonVictoryEffect() {
        super("V_" + "Dragon".toUpperCase(), "Dragon", Material.DRAGON_HEAD);
    }
    @Override
    public void play(Location location) {
        location.getWorld().playSound(location, org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
    }
}
