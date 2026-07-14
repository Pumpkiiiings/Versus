package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import me.robomonkey.versus.util.DisplayAnimationTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class DragonVictoryEffect extends VictoryEffect {
    public DragonVictoryEffect() {
        super("V_" + "Dragon".toUpperCase(), "Dragon", Material.DRAGON_HEAD);
    }
    
    @Override
    public void play(Location location) {
        location.getWorld().playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        
        Location displayLoc = location.clone().add(0, 2.5, 0);
        ItemDisplay dragonHead = location.getWorld().spawn(displayLoc, ItemDisplay.class, entity -> {
            entity.setItemStack(new ItemStack(Material.DRAGON_HEAD));
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            Transformation transform = entity.getTransformation();
            transform.getScale().set(2.0f, 2.0f, 2.0f);
            entity.setTransformation(transform);
            entity.setTeleportDuration(1);
        });

        int durationSeconds = Settings.getNumber(Setting.POST_DUEL_DELAY);
        if (durationSeconds <= 0) durationSeconds = 5;
        int maxTicks = durationSeconds * 20;

        DisplayAnimationTask task = new DisplayAnimationTask(maxTicks) {
            float angle = 0;

            @Override
            public void tick(int currentTick) {
                angle += 0.2f; // Spin speed
                if (angle > Math.PI * 2) angle -= Math.PI * 2;

                // Update rotation smoothly
                Transformation transform = dragonHead.getTransformation();
                transform.getLeftRotation().set(new AxisAngle4f(angle, 0, 1, 0));
                dragonHead.setTransformation(transform);

                // Spawn dragon breath particles in a ring
                if (currentTick % 2 == 0) {
                    for (int i = 0; i < 8; i++) {
                        double pAngle = angle + (i * Math.PI / 4);
                        double x = Math.cos(pAngle) * 1.5;
                        double z = Math.sin(pAngle) * 1.5;
                        Location particleLoc = displayLoc.clone().add(x, -0.5, z);
                        particleLoc.getWorld().spawnParticle(Particle.DRAGON_BREATH, particleLoc, 1, 0, 0, 0, 0.05);
                    }
                }
                
                // Final explosion
                if (currentTick == maxTicks - 1) {
                    displayLoc.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, displayLoc, 2);
                    displayLoc.getWorld().playSound(displayLoc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.0f);
                }
            }
        };
        task.addEntity(dragonHead);
        task.start();
    }
}

