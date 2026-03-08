package com.stormai.plugin.ability;

import com.stormai.plugin.SoulBoundSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityHandler {

    private final JavaPlugin plugin;
    // Store active ability tasks to cancel later if needed
    private final Map<UUID, BukkitRunnable> activeTasks = new HashMap<>();

    public AbilityHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Apply a temporary ability (strength, speed, or extra health) for a set duration */
    public void applyAbility(Player p, AbilityType type, int durationSeconds) {
        if (!SoulBoundSMP.getInstance().getSoulManager().canSpendSouls(p, 1)) {
            // Not enough souls, cancel
            return;
        }
        SoulBoundSMP.getInstance().getSoulManager().removeSoul(p, 1);

        switch (type) {
            case STRENGTH -> applyStrength(p, durationSeconds);
            case SPEED -> applySpeed(p, durationSeconds);
            case EXTRA_HEART -> applyExtraHealth(p, durationSeconds);
        }
    }

    private void applyStrength(Player p, int seconds) {
        p.addAttachment(SoulBoundSMP.getInstance(), "tempStrength", true);
        p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() + 3);
        scheduleTask(p.getUniqueId(), () -> {
            p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(p.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue() - 3);
            p.removeAttachment(SoulBoundSMP.getInstance());
        }, seconds);
    }

    private void applySpeed(Player p, int seconds) {
        p.addAttachment(SoulBoundSMP.getInstance(), "tempSpeed", true);
        p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() + 0.2);
        scheduleTask(p.getUniqueId(), () -> {
            p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(p.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getValue() - 0.2);
            p.removeAttachment(SoulBoundSMP.getInstance());
        }, seconds);
    }

    private void applyExtraHealth(Player p, int seconds) {
        org.bukkit.attribute.AttributeInstance attr = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
        if (attr != null) {
            double base = attr.getBaseValue();
            double newMax = base + 2; // each extra heart = +2 health
            attr.setBaseValue(newMax);
            // Optionally heal the player to full
            p.setHealth(p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue());
        }
        scheduleTask(p.getUniqueId(), () -> {
            // revert health max
            org.bukkit.attribute.AttributeInstance rev = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
            if (rev != null) rev.setBaseValue(rev.getBaseValue() - 2);
            // optionally restore health if needed
        }, seconds);
    }

    private void scheduleTask(UUID uuid, Runnable runnable, int seconds) {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
                activeTasks.remove(uuid);
            }
        };
        task.runTaskLater(SoulBoundSMP.getInstance(), 20L * seconds);
        activeTasks.put(uuid, task);
    }

    /** Cancel all active tasks for a player (e.g., on logout or death) */
    public void cancelTasks(UUID uuid) {
        activeTasks.remove(uuid);
    }
}