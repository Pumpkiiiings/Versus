package me.robomonkey.versus.cosmetic.manager;

import me.robomonkey.versus.cosmetic.model.victoryeffects.FireworksVictoryEffect;
import me.robomonkey.versus.cosmetic.model.victoryeffects.DragonVictoryEffect;
import me.robomonkey.versus.cosmetic.model.victoryeffects.FlamesVictoryEffect;
import me.robomonkey.versus.cosmetic.model.victoryeffects.HeartsVictoryEffect;
import me.robomonkey.versus.cosmetic.model.victoryeffects.MusicVictoryEffect;
import me.robomonkey.versus.cosmetic.model.victoryeffects.SoulsVictoryEffect;
import me.robomonkey.versus.cosmetic.model.victoryeffects.NoneVictoryEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.ExplosionKillEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.LightningKillEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.ConfettiKillEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.BloodKillEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.MagicKillEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.SmokeKillEffect;
import me.robomonkey.versus.cosmetic.model.killeffects.NoneKillEffect;
import me.robomonkey.versus.cosmetic.model.CosmeticEffect;
import me.robomonkey.versus.cosmetic.model.VictoryEffect;
import me.robomonkey.versus.cosmetic.model.KillEffect;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;




public class CosmeticsManager {

    private static CosmeticsManager instance;
    
    private final Map<String, KillEffect> killEffects = new LinkedHashMap<>();
    private final Map<String, VictoryEffect> victoryEffects = new LinkedHashMap<>();

    private CosmeticsManager() {
        registerDefaultEffects();
    }

    public static void init() {
        if (instance == null) {
            instance = new CosmeticsManager();
        }
        instance.loadConfigs();
    }

    public static CosmeticsManager getInstance() {
        return instance;
    }

    private void registerDefaultEffects() {
        // Kill Effects
        registerKillEffect(new NoneKillEffect());
        registerKillEffect(new BloodKillEffect());
        registerKillEffect(new ConfettiKillEffect());
        registerKillEffect(new LightningKillEffect());
        registerKillEffect(new ExplosionKillEffect());
        registerKillEffect(new SmokeKillEffect());
        registerKillEffect(new MagicKillEffect());

        // Victory Effects
        registerVictoryEffect(new NoneVictoryEffect());
        registerVictoryEffect(new FireworksVictoryEffect());
        registerVictoryEffect(new HeartsVictoryEffect());
        registerVictoryEffect(new FlamesVictoryEffect());
        registerVictoryEffect(new MusicVictoryEffect());
        registerVictoryEffect(new SoulsVictoryEffect());
        registerVictoryEffect(new DragonVictoryEffect());
    }

    public void loadConfigs() {
        me.robomonkey.versus.Versus plugin = me.robomonkey.versus.Versus.getInstance();
        java.io.File effectsFolder = new java.io.File(plugin.getDataFolder(), "effects");
        if (!effectsFolder.exists()) {
            effectsFolder.mkdirs();
        }
        
        loadEffectConfig(plugin, effectsFolder, "killeffects.yml", killEffects);
        loadEffectConfig(plugin, effectsFolder, "victoryeffects.yml", victoryEffects);
    }

    private void loadEffectConfig(me.robomonkey.versus.Versus plugin, java.io.File folder, String fileName, Map<String, ? extends CosmeticEffect> effectsMap) {
        java.io.File file = new java.io.File(folder, fileName);
        if (!file.exists()) {
            plugin.saveResource("effects/" + fileName, false);
        }
        org.bukkit.configuration.file.FileConfiguration config = org.bukkit.configuration.file.YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            CosmeticEffect effect = effectsMap.get(key);
            if (effect != null) {
                String name = config.getString(key + ".name");
                if (name != null) effect.setDisplayName(name);
                String iconName = config.getString(key + ".icon");
                if (iconName != null) {
                    org.bukkit.Material mat = org.bukkit.Material.matchMaterial(iconName);
                    if (mat != null) effect.setIcon(mat);
                }
                // Support price: none / price: 0 / price: 1000 / price: 1000.0
                String priceStr = config.getString(key + ".price", "0");
                double price = 0.0;
                if (priceStr != null && !priceStr.equalsIgnoreCase("none")) {
                    try { price = Double.parseDouble(priceStr); } catch (NumberFormatException ignored) {}
                }
                effect.setPrice(price);
            }
        }
    }

    public void registerKillEffect(KillEffect effect) {
        killEffects.put(effect.getId(), effect);
    }

    public void registerVictoryEffect(VictoryEffect effect) {
        victoryEffects.put(effect.getId(), effect);
    }

    public KillEffect getKillEffect(String id) {
        return killEffects.get(id);
    }

    public VictoryEffect getVictoryEffect(String id) {
        return victoryEffects.get(id);
    }

    public Collection<KillEffect> getKillEffects() {
        return killEffects.values();
    }

    public Collection<VictoryEffect> getVictoryEffects() {
        return victoryEffects.values();
    }
}
