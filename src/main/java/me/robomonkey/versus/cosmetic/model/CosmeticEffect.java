package me.robomonkey.versus.cosmetic.model;

import org.bukkit.Location;
import org.bukkit.Material;

public abstract class CosmeticEffect {

    private final String id;
    private String displayName;
    private Material icon;
    private double price = 0.0;

    public CosmeticEffect(String id, String displayName, Material icon) {
        this.id = id;
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public abstract void play(Location location);

}
