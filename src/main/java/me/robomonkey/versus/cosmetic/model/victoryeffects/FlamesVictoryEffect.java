package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import me.robomonkey.versus.util.DisplayAnimationTask;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;

public class FlamesVictoryEffect extends VictoryEffect {
    public FlamesVictoryEffect() {
        super("V_" + "Flames".toUpperCase(), "Flames", Material.BLAZE_POWDER);
    }
    
    @Override
    public void play(Location location) {
        Location displayLoc = location.clone().add(0, 2.5, 0);
        
        TextDisplay textDisplay = location.getWorld().spawn(displayLoc, TextDisplay.class, entity -> {
            entity.setText("§c§l\u2668 §6§lVICTORY §c§l\u2668");
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0)); // Transparent background
            entity.setShadowed(true);
            entity.setDefaultBackground(false);
            Transformation transform = entity.getTransformation();
            transform.getScale().set(2.5f, 2.5f, 2.5f);
            entity.setTransformation(transform);
            entity.setTeleportDuration(1);
        });

        int durationSeconds = Settings.getNumber(Setting.POST_DUEL_DELAY);
        if (durationSeconds <= 0) durationSeconds = 5;
        int maxTicks = durationSeconds * 20;

        DisplayAnimationTask task = new DisplayAnimationTask(maxTicks) {
            float heightOffset = 0;

            @Override
            public void tick(int currentTick) {
                heightOffset = (float) Math.sin(currentTick * 0.15) * 0.3f;

                // Bobbing animation
                Transformation transform = textDisplay.getTransformation();
                transform.getTranslation().set(0, heightOffset, 0);
                textDisplay.setTransformation(transform);

                // Play fire sound occasionally
                if (currentTick % 20 == 0) {
                    displayLoc.getWorld().playSound(displayLoc, Sound.BLOCK_FIRE_AMBIENT, 1.0f, 1.0f);
                }
                if (currentTick % 10 == 0) {
                    displayLoc.getWorld().playSound(displayLoc, Sound.BLOCK_LAVA_POP, 1.0f, 1.0f);
                }

                // Flame and Lava eruptions
                if (currentTick % 2 == 0) {
                    for (int i = 0; i < 4; i++) {
                        double offsetX = (Math.random() - 0.5) * 3.0;
                        double offsetZ = (Math.random() - 0.5) * 3.0;
                        Location particleLoc = location.clone().add(offsetX, 0.1, offsetZ);
                        
                        particleLoc.getWorld().spawnParticle(Particle.FLAME, particleLoc, 3, 0.1, 0.5, 0.1, 0.05);
                        if (Math.random() > 0.7) {
                            particleLoc.getWorld().spawnParticle(Particle.LAVA, particleLoc, 1, 0.2, 0.5, 0.2, 0);
                        }
                        if (Math.random() > 0.8) {
                            particleLoc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, particleLoc, 1, 0.1, 0.5, 0.1, 0.05);
                        }
                    }
                }
            }
        };
        task.addEntity(textDisplay);
        task.start();
    }
}
