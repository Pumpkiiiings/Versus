package me.robomonkey.versus.cosmetics.killeffects;

import me.robomonkey.versus.cosmetics.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class MagicKillEffect extends KillEffect {
    public MagicKillEffect() {
        super("K_" + "Magic".toUpperCase(), "Magic", Material.ENCHANTED_BOOK);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.ENCHANTMENT_TABLE, location.clone().add(0, 1, 0), 100, 0.5, 1, 0.5, 0.5); location.getWorld().playSound(location, org.bukkit.Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1.0f, 1.0f);
    }
}
