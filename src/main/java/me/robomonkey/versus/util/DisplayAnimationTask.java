package me.robomonkey.versus.util;

import me.robomonkey.versus.Versus;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public abstract class DisplayAnimationTask extends BukkitRunnable {
    
    private final List<Entity> entities = new ArrayList<>();
    private int ticks = 0;
    private final int maxTicks;

    public DisplayAnimationTask(int maxTicks) {
        this.maxTicks = maxTicks;
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    @Override
    public void run() {
        if (ticks >= maxTicks) {
            cleanup();
            this.cancel();
            return;
        }
        
        tick(ticks);
        ticks++;
    }
    
    public abstract void tick(int currentTick);
    
    public void cleanup() {
        for (Entity e : entities) {
            if (e != null && e.isValid()) {
                e.remove();
            }
        }
        entities.clear();
    }
    
    public void start() {
        this.runTaskTimer(Versus.getInstance(), 0L, 1L);
    }
}
