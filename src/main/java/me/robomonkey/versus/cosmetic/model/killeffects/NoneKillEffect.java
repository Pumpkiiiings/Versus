package me.robomonkey.versus.cosmetic.model.killeffects;

import me.robomonkey.versus.cosmetic.model.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class NoneKillEffect extends KillEffect {
    public NoneKillEffect() {
        super("K_" + "None".toUpperCase(), "None", Material.BARRIER);
    }
    @Override
    public void play(Location location) {
        
    }
}
