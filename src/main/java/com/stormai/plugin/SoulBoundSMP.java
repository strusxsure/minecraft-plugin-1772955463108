package com.stormai.plugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SoulBoundSMP extends JavaPlugin {

    private SoulManager soulManager;
    private CooldownManager cooldownManager;
    private AbilityHandler abilityHandler;

    @Override
    public void onEnable() {
        // Register config
        saveDefaultConfig();

        // Initialize managers
        soulManager = new SoulManager(this);
        cooldownManager = new CooldownManager(this);
        abilityHandler = new AbilityHandler(this);

        // Register event listener
        getServer().getPluginManager().registerEvents(new PlayerListener(this, soulManager, cooldownManager, abilityHandler), this);

        // Register command
        getCommand("souls").setExecutor(new SoulCommand(this, soulManager, abilityHandler, cooldownManager));

        getLogger().info("SoulBoundSMP enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SoulBoundSMP disabled!");
    }

    /** Helper to access the plugin instance from other classes */
    public static SoulBoundSMP getInstance() {
        return org.bukkit.Bukkit.getPluginManager().getPlugins().stream()
                .filter(p -> p instanceof SoulBoundSMP)
                .findFirst()
                .orElse(null);
    }

    /** Exposes the SoulManager for external use (e.g., commands) */
    public SoulManager getSoulManager() {
        return soulManager;
    }

    /** Exposes the CooldownManager */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    /** Exposes the AbilityHandler */
    public AbilityHandler getAbilityHandler() {
        return abilityHandler;
    }
}