package com.stormai.plugin.listener;

import com.stormai.plugin.SoulBoundSMP;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final SoulBoundSMP plugin;
    private final SoulManager soulManager;
    private final CooldownManager cooldownManager;
    private final AbilityHandler abilityHandler;

    public PlayerListener(SoulBoundSMP plugin, SoulManager soulManager, CooldownManager cooldownManager, AbilityHandler abilityHandler) {
        this.plugin = plugin;
        this.soulManager = soulManager;
        this.cooldownManager = cooldownManager;
        this.abilityHandler = abilityHandler;
    }

    /** Handle player kill to grant a soul */
    @EventHandler
    public void onPlayerKill(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player killer) || !(e.getEntity() instanceof Player victim)) return;
        // Ensure both are players and are not NPCs
        if (!killer.getWorld().equals(victim.getWorld())) return;

        Player killerPlayer = (Player) killer;
        Player victimPlayer = (Player) victim;

        // Grant soul if cooldown passed
        if (cooldownManager.canGainSoul(killerPlayer)) {
            cooldownManager.registerKill(killerPlayer, victimPlayer);
            killerPlayer.sendMessage(ChatColor.GREEN + "You absorbed a Soul!");
        } else {
            killerPlayer.sendMessage(ChatColor.YELLOW + "You must wait before absorbing another Soul.");
        }

        // Drop a physical Soul item at victim's location
        ItemStack soulItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = soulItem.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE + "Soul");
        meta.setLore(java.util.Arrays.asList(
                ChatColor.GRAY + "Holding a captured soul.",
                ChatColor.GRAY + "Right‑click to pick up."
        ));
        soulItem.setItemMeta(meta);

        victimPlayer.getWorld().dropItemNaturally(victimPlayer.getLocation(), soulItem);
    }

    /** Handle player death when they have no souls */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        int souls = soulManager.getSoulCount(p);
        if (souls == 0) {
            // Mark as weakened: reduce max health permanently until revived via ritual
            p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(10);
            // Optional: reduce current health to match new max
            org.bukkit.attribute.AttributeInstance attr = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
            if (attr != null) p.setHealth(attr.getValue());

            // Notify player
            p.sendMessage(ChatColor.RED + "You have entered a weakened state! Your max health is now 10.");
        }
    }

    /** Listen for players picking up the Soul item */
    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if (e.getItem().getItemMeta().getDisplayName().contains("Soul")) {
            // Give the player a soul (increase count)
            soulManager.addSoul(e.getPlayer(), 1);
            e.getItem().setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.AQUA + "You absorbed a Soul!");
        }
    }

    /** Handle custom ritual command to revive weakened players */
    @EventHandler
    public void onCommand(org.bukkit.event.block.ActionEvent e) {
        // Not needed here; we will add a ritual command elsewhere.
    }

    /** When a player disconnects while weakened, persist the weakened state */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        // No special action needed; persisted souls are saved via SoulManager
    }
}