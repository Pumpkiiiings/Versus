package me.robomonkey.versus.cosmetics;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import me.robomonkey.versus.Versus;

public enum VictoryEffect {
    NONE("Ninguno", Material.BARRIER),
    FIREWORKS("Fuegos Artificiales", Material.FIREWORK_ROCKET),
    HEARTS("Corazones", Material.RED_DYE),
    FLAMES("Anillo de Fuego", Material.BLAZE_POWDER),
    MUSIC("Música Épica", Material.JUKEBOX),
    SOULS("Almas", Material.SOUL_SAND),
    DRAGON("Rugido de Dragón", Material.DRAGON_HEAD);

    private final String displayName;
    private final Material icon;

    VictoryEffect(String displayName, Material icon) {
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
            case FIREWORKS:
                new BukkitRunnable() {
                    int count = 0;
                    @Override
                    public void run() {
                        if (count++ > 3) {
                            this.cancel();
                            return;
                        }
                        Location fwLoc = location.clone().add(Math.random() * 4 - 2, 0, Math.random() * 4 - 2);
                        Firework fw = (Firework) world.spawnEntity(fwLoc, EntityType.FIREWORK);
                        FireworkMeta fwm = fw.getFireworkMeta();
                        fwm.addEffect(FireworkEffect.builder().withColor(Color.AQUA, Color.FUCHSIA).with(FireworkEffect.Type.BALL_LARGE).build());
                        fwm.setPower(1);
                        fw.setFireworkMeta(fwm);
                    }
                }.runTaskTimer(Versus.getInstance(), 0L, 10L);
                break;
            case HEARTS:
                world.spawnParticle(Particle.HEART, location.clone().add(0, 2, 0), 30, 1, 1, 1, 0);
                world.playSound(location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                break;
            case FLAMES:
                new BukkitRunnable() {
                    double angle = 0;
                    int count = 0;
                    @Override
                    public void run() {
                        if (count++ > 20) {
                            this.cancel();
                            return;
                        }
                        angle += 0.5;
                        double x = 2 * Math.cos(angle);
                        double z = 2 * Math.sin(angle);
                        world.spawnParticle(Particle.FLAME, location.clone().add(x, 1, z), 5, 0, 0, 0, 0);
                        world.spawnParticle(Particle.FLAME, location.clone().add(-x, 1, -z), 5, 0, 0, 0, 0);
                    }
                }.runTaskTimer(Versus.getInstance(), 0L, 2L);
                world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 1f, 1f);
                break;
            case MUSIC:
                world.playSound(location, Sound.MUSIC_DISC_PIGSTEP, 1f, 1f);
                break;
            case SOULS:
                world.spawnParticle(Particle.SOUL_FIRE_FLAME, location.clone().add(0, 1, 0), 100, 1, 1, 1, 0.1);
                world.spawnParticle(Particle.SOUL, location.clone().add(0, 1, 0), 30, 1, 1, 1, 0.1);
                world.playSound(location, Sound.ENTITY_GHAST_SCREAM, 1f, 1f);
                break;
            case DRAGON:
                world.spawnParticle(Particle.DRAGON_BREATH, location.clone().add(0, 1, 0), 200, 2, 2, 2, 0.1);
                world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                break;
        }
    }
}
