package me.robomonkey.versus.cosmetics.killeffects;

import me.robomonkey.versus.cosmetics.KillEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class ConfettiKillEffect extends KillEffect {
    public ConfettiKillEffect() {
        super("K_" + "Confetti".toUpperCase(), "Confetti", Material.PAPER);
    }
    @Override
    public void play(Location location) {
        location.getWorld().spawnParticle(org.bukkit.Particle.ITEM_CRACK, location.clone().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1, new org.bukkit.inventory.ItemStack(org.bukkit.Material.YELLOW_DYE)); location.getWorld().spawnParticle(org.bukkit.Particle.ITEM_CRACK, location.clone().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1, new org.bukkit.inventory.ItemStack(org.bukkit.Material.BLUE_DYE)); location.getWorld().spawnParticle(org.bukkit.Particle.ITEM_CRACK, location.clone().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.1, new org.bukkit.inventory.ItemStack(org.bukkit.Material.RED_DYE));
    }
}
