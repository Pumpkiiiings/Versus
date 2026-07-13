package me.robomonkey.versus.duel;

import me.robomonkey.versus.arena.Arena;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.UUID;

public class BoundaryVisualizerTask implements Runnable {

    private final DuelManager duelManager;

    public BoundaryVisualizerTask(DuelManager duelManager) {
        this.duelManager = duelManager;
    }

    @Override
    public void run() {
        for (Arena arena : me.robomonkey.versus.arena.ArenaManager.getInstance().getAllArenas()) {
            if (!arena.hasBounds()) continue;
            for (Duel duel : arena.getActiveDuels()) {
                if (!duel.isActive()) continue;

            double minX = Math.min(arena.getPosOne().getX(), arena.getPosTwo().getX());
            double maxX = Math.max(arena.getPosOne().getX(), arena.getPosTwo().getX());
            double minY = Math.min(arena.getPosOne().getY(), arena.getPosTwo().getY());
            double maxY = Math.max(arena.getPosOne().getY(), arena.getPosTwo().getY());
            double minZ = Math.min(arena.getPosOne().getZ(), arena.getPosTwo().getZ());
            double maxZ = Math.max(arena.getPosOne().getZ(), arena.getPosTwo().getZ());

            java.util.List<Player> viewers = new java.util.ArrayList<>(duel.getPlayers());
            viewers.addAll(me.robomonkey.versus.arena.ArenaVisibilityManager.getSpectators(duel));

            for (Player player : viewers) {
                if (!player.isOnline()) continue;
                Location eyeLoc = player.getEyeLocation();
                Vector eyeDir = eyeLoc.getDirection();
                double viewDistance = 6.0;

                // For each coordinate, check if player is close enough to the wall
                checkAndSpawnParticles(player, eyeLoc, eyeDir, minX, maxX, minY, maxY, minZ, maxZ, viewDistance);
            }
            }
        }
    }

    private void checkAndSpawnParticles(Player player, Location eye, Vector dir, double minX, double maxX, double minY, double maxY, double minZ, double maxZ, double dist) {
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1.2F);

        // Min X Wall
        if (Math.abs(eye.getX() - minX) < dist) {
            spawnWallParticles(player, eye, dir, minX, minX, minY, maxY, minZ, maxZ, dustOptions);
        }
        // Max X Wall
        if (Math.abs(eye.getX() - maxX) < dist) {
            spawnWallParticles(player, eye, dir, maxX, maxX, minY, maxY, minZ, maxZ, dustOptions);
        }
        // Min Z Wall
        if (Math.abs(eye.getZ() - minZ) < dist) {
            spawnWallParticles(player, eye, dir, minX, maxX, minY, maxY, minZ, minZ, dustOptions);
        }
        // Max Z Wall
        if (Math.abs(eye.getZ() - maxZ) < dist) {
            spawnWallParticles(player, eye, dir, minX, maxX, minY, maxY, maxZ, maxZ, dustOptions);
        }
    }

    private void spawnWallParticles(Player player, Location eye, Vector eyeDir, double startX, double endX, double startY, double endY, double startZ, double endZ, Particle.DustOptions dustOptions) {
        double step = 0.5; // Particle density
        for (double x = startX; x <= endX; x += (startX == endX ? 1 : step)) {
            for (double y = Math.max(startY, eye.getY() - 4); y <= Math.min(endY, eye.getY() + 4); y += step) {
                for (double z = startZ; z <= endZ; z += (startZ == endZ ? 1 : step)) {
                    Location point = new Location(eye.getWorld(), x, y, z);
                    double distSq = point.distanceSquared(eye);
                    
                    if (distSq > 36.0) continue; // Only within 6 blocks

                    Vector toPoint = point.toVector().subtract(eye.toVector()).normalize();
                    double dot = eyeDir.dot(toPoint);

                    if (dot > 0.5) { // Roughly 60 degree FoV
                        player.spawnParticle(Particle.REDSTONE, point, 1, 0, 0, 0, 0, dustOptions);
                    }
                }
            }
        }
    }
}
