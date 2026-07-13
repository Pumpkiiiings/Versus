package me.robomonkey.versus.cosmetics;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;

public enum KillEffect {
    NONE("Ninguno", Material.BARRIER),
    BLOOD("Sangre", Material.REDSTONE),
    CONFETTI("Confeti", Material.PAPER),
    LIGHTNING("Rayo", Material.LIGHTNING_ROD),
    EXPLOSION("Explosión", Material.TNT),
    SMOKE("Humo", Material.CAMPFIRE),
    MAGIC("Magia", Material.ENCHANTED_BOOK);

    private final String displayName;
    private final Material icon;

    KillEffect(String displayName, Material icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public void play(Location location) {
        if (this == NONE) return;
        World world = location.getWorld();
        if (world == null) return;

        switch (this) {
            case BLOOD:
                world.spawnParticle(Particle.BLOCK_CRACK, location.clone().add(0, 1, 0), 100, 0.5, 0.5, 0.5, Bukkit.createBlockData(Material.REDSTONE_BLOCK));
                world.playSound(location, Sound.ENTITY_PLAYER_HURT, 1f, 0.5f);
                break;
            case CONFETTI:
                world.spawnParticle(Particle.TOTEM, location.clone().add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0.5);
                world.playSound(location, Sound.ENTITY_VILLAGER_CELEBRATE, 1f, 1f);
                break;
            case LIGHTNING:
                world.strikeLightningEffect(location);
                break;
            case EXPLOSION:
                world.spawnParticle(Particle.EXPLOSION_LARGE, location, 3, 0.5, 0.5, 0.5, 0);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1f, 1f);
                break;
            case SMOKE:
                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, location.clone().add(0, 1, 0), 50, 0.5, 0.5, 0.5, 0.05);
                world.playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, 1f, 1f);
                break;
            case MAGIC:
                world.spawnParticle(Particle.SPELL_WITCH, location.clone().add(0, 1, 0), 100, 0.5, 1, 0.5, 0.1);
                world.playSound(location, Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1f);
                break;
        }
    }
}
