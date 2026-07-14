package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class MusicVictoryEffect extends VictoryEffect {
    public MusicVictoryEffect() {
        super("V_" + "Music".toUpperCase(), "Music", Material.MUSIC_DISC_PIGSTEP);
    }
    @Override
    public void play(Location location) {
        location.getWorld().playSound(location, org.bukkit.Sound.MUSIC_DISC_PIGSTEP, 1.0f, 1.0f);
    }
}
