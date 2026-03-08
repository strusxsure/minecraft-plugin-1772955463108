package com.stormai.plugin.manager;

import com.stormai.plugin.SoulBoundSMP;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private final JavaPlugin plugin;
    private final Map<UUID, Long> lastKillCooldown = new HashMap<>();
    private final long cooldownMillis = 30_000L; // 30 seconds

    public CooldownManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean canGainSoul(Player p) {
        long now = System.currentTimeMillis();
        long last = lastKillCooldown.getOrDefault(p.getUniqueId(), 0L);
        return now - last >= cooldownMillis;
    }

    public void registerKill(Player killer, Player victim) {
        lastKillCooldown.put(killer.getUniqueId(), System.currentTimeMillis());
        // Add a soul to the killer
        SoulBoundSMP.getInstance().getSoulManager().addSoul(killer, 1);
    }

    public void resetCooldown(Player p) {
        lastKillCooldown.remove(p.getUniqueId());
    }
}