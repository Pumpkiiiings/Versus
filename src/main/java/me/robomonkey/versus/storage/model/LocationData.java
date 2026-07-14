package me.robomonkey.versus.storage.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationData {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public String world;

    public LocationData(Location location) {
        if (location == null) return;
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.pitch = location.getPitch();
        this.yaw = location.getYaw();
        this.world = location.getWorld().getName();
    }

    public Location toLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}
