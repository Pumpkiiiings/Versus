package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class NoneVictoryEffect extends VictoryEffect {
    public NoneVictoryEffect() {
        super("V_" + "None".toUpperCase(), "None", Material.BARRIER);
    }
    @Override
    public void play(Location location) {
        
    }
}
