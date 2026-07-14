package me.robomonkey.versus.cosmetic.model.victoryeffects;

import me.robomonkey.versus.config.model.Setting;
import me.robomonkey.versus.config.model.Settings;
import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import me.robomonkey.versus.util.DisplayAnimationTask;
import me.robomonkey.versus.util.EffectUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Transformation;

public class FireworksVictoryEffect extends VictoryEffect {
    public FireworksVictoryEffect() {
        super("V_" + "Fireworks".toUpperCase(), "Fireworks", Material.FIREWORK_ROCKET);
    }
    
    @Override
    public void play(Location location) {
        Location displayLoc = location.clone().add(0, 2.5, 0);
        
        TextDisplay textDisplay = location.getWorld().spawn(displayLoc, TextDisplay.class, entity -> {
            entity.setText("§b§l\u2728 §f§l¡GG! §b§l\u2728");
            entity.setBillboard(Display.Billboard.CENTER);
            entity.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
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
                heightOffset = (float) Math.sin(currentTick * 0.2) * 0.4f;

                // Bobbing animation
                Transformation transform = textDisplay.getTransformation();
                transform.getTranslation().set(0, heightOffset, 0);
                textDisplay.setTransformation(transform);

                // Shoot fireworks
                if (currentTick % 20 == 0) {
                    EffectUtil.spawnFireWorks(location, 1, 50, Color.AQUA);
                    if (Math.random() > 0.5) {
                        EffectUtil.spawnFireWorks(location.clone().add(Math.random()*4-2, 0, Math.random()*4-2), 1, 30, Color.YELLOW);
                    }
                }
            }
        };
        task.addEntity(textDisplay);
        task.start();
    }
}

