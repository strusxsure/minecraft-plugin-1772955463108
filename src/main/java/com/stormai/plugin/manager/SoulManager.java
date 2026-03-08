package com.stormai.plugin.manager;

import com.stormai.plugin.SoulBoundSMP;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoulManager {

    private final JavaPlugin plugin;
    private final File file;
    private final Map<UUID, Integer> soulsMap = new HashMap<>();

    public SoulManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "souls.yml");
        loadSaves();
    }

    private void loadSaves() {
        if (!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for (String key : cfg.getKeys(true)) {
            try {
                UUID uuid = UUID.fromString(key);
                int souls = cfg.getInt(uuid.toString(), 0);
                soulsMap.put(uuid, souls);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void saveSoul(UUID uuid, int amount) {
        soulsMap.put(uuid, amount);
        saveToFile(uuid, amount);
    }

    private void saveToFile(UUID uuid, int amount) {
        YamlConfiguration cfg = new YamlConfiguration();
        if (file.exists()) {
            cfg.load(file);
        }
        cfg.set(uuid.toString(), amount);
        try {
            cfg.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getSoulCount(Player p) {
        return soulsMap.getOrDefault(p.getUniqueId(), 0);
    }

    public void addSoul(Player p, int amount) {
        int newCount = getSoulCount(p) + amount;
        soulsMap.put(p.getUniqueId(), newCount);
        saveToFile(p.getUniqueId(), newCount);
    }

    public void removeSoul(Player p, int amount) {
        int newCount = Math.max(0, getSoulCount(p) - amount);
        soulsMap.put(p.getUniqueId(), newCount);
        saveToFile(p.getUniqueId(), newCount);
    }

    public Map<UUID, Integer> getAllSouls() {
        return new HashMap<>(soulsMap);
    }

    /** Checks if the player has enough souls to spend */
    public boolean canSpendSouls(Player p, int amount) {
        return getSoulCount(p) >= amount;
    }
}