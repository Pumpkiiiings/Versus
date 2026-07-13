package me.robomonkey.versus.cosmetics.killeffects;

import me.robomonkey.versus.cosmetics.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class LightningKillEffect extends KillEffect {
    public LightningKillEffect() {
        super("K_" + "Lightning".toUpperCase(), "Lightning", Material.LIGHTNING_ROD);
    }
    @Override
    public void play(Location location) {
        location.getWorld().strikeLightningEffect(location);
    }
}
