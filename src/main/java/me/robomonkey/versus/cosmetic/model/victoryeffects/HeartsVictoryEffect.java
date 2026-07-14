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

public class HeartsVictoryEffect extends VictoryEffect {
    public HeartsVictoryEffect() {
        super("V_" + "Hearts".toUpperCase(), "Hearts", Material.POPPY);
    }
    
    @Override
    public void play(Location location) {
        Location displayLoc = location.clone().add(0, 2.0, 0);
        
        ItemDisplay heartApple = location.getWorld().spawn(displayLoc, ItemDisplay.class, entity -> {
            entity.setItemStack(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE));
            entity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.HEAD);
            Transformation transform = entity.getTransformation();
            transform.getScale().set(1.5f, 1.5f, 1.5f);
            entity.setTransformation(transform);
            entity.setTeleportDuration(1);
        });

        int durationSeconds = Settings.getNumber(Setting.POST_DUEL_DELAY);
        if (durationSeconds <= 0) durationSeconds = 5;
        int maxTicks = durationSeconds * 20;

        DisplayAnimationTask task = new DisplayAnimationTask(maxTicks) {
            @Override
            public void tick(int currentTick) {
                // Heartbeat pulse animation
                float pulse = 1.5f + (float) Math.max(0, Math.sin(currentTick * 0.4) * 0.5f);

                Transformation transform = heartApple.getTransformation();
                transform.getScale().set(pulse, pulse, pulse);
                heartApple.setTransformation(transform);

                // Play heartbeat sound when it gets big
                if (pulse > 1.9f && currentTick % 5 == 0) {
                    displayLoc.getWorld().playSound(displayLoc, Sound.ENTITY_WARDEN_HEARTBEAT, 1.0f, 1.0f);
                    
                    // Burst hearts
                    for (int i = 0; i < 5; i++) {
                        double offsetX = (Math.random() - 0.5) * 2.0;
                        double offsetY = (Math.random() - 0.5) * 2.0;
                        double offsetZ = (Math.random() - 0.5) * 2.0;
                        Location particleLoc = displayLoc.clone().add(offsetX, offsetY, offsetZ);
                        particleLoc.getWorld().spawnParticle(Particle.HEART, particleLoc, 1, 0, 0, 0, 0);
                        particleLoc.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, particleLoc, 1, 0, 0, 0, 0);
                    }
                }
            }
        };
        task.addEntity(heartApple);
        task.start();
    }
}
