package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import me.robomonkey.versus.util.DisplayAnimationTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

public class SoulsVictoryEffect extends VictoryEffect {
    public SoulsVictoryEffect() {
        super("V_" + "Souls".toUpperCase(), "Souls", Material.SOUL_SAND);
    }
    
    @Override
    public void play(Location location) {
        Location displayLoc = location.clone().add(0, 1.5, 0);
        
        BlockDisplay soulBlock = location.getWorld().spawn(displayLoc, BlockDisplay.class, entity -> {
            entity.setBlock(org.bukkit.Bukkit.createBlockData(Material.SOUL_CAMPFIRE));
            Transformation transform = entity.getTransformation();
            transform.getScale().set(1.5f, 1.5f, 1.5f);
            // Center the block
            transform.getTranslation().set(-0.75f, 0, -0.75f);
            entity.setTransformation(transform);
            entity.setTeleportDuration(1);
        });

        int durationSeconds = Settings.getNumber(Setting.POST_DUEL_DELAY);
        if (durationSeconds <= 0) durationSeconds = 5;
        int maxTicks = durationSeconds * 20;

        DisplayAnimationTask task = new DisplayAnimationTask(maxTicks) {
            float angle = 0;
            float height = 0;

            @Override
            public void tick(int currentTick) {
                angle += 0.3f;
                height = (float) Math.sin(currentTick * 0.1) * 0.5f;

                // Make block float and spin
                Transformation transform = soulBlock.getTransformation();
                transform.getLeftRotation().set(new AxisAngle4f(angle, 0, 1, 0));
                // Maintain centering while floating
                transform.getTranslation().set(-0.75f, height, -0.75f);
                soulBlock.setTransformation(transform);

                // Play spooky ambient sound occasionally
                if (currentTick % 40 == 0) {
                    displayLoc.getWorld().playSound(displayLoc, Sound.ENTITY_GHAST_AMBIENT, 0.5f, 0.5f);
                }

                // Tornado effect
                double radius = 1.0 + (height * 0.5);
                double tAngle = currentTick * 0.5;
                double x = Math.cos(tAngle) * radius;
                double z = Math.sin(tAngle) * radius;
                double y = (currentTick % 20) * 0.1;
                
                Location particleLoc = displayLoc.clone().add(x, y, z);
                particleLoc.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 2, 0.1, 0.1, 0.1, 0.01);
                particleLoc.getWorld().spawnParticle(Particle.SOUL, particleLoc, 1, 0.2, 0.2, 0.2, 0.05);
            }
        };
        task.addEntity(soulBlock);
        task.start();
    }
}

