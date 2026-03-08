package com.stormai.plugin.listener;

import com.stormai.plugin.SoulBoundSMP;
import com.stormai.plugin.manager.SoulManager;
import com.stormai.plugin.manager.CooldownManager;
import com.stormai.plugin.ability.AbilityHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
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
        if (!(e.getEntity() instanceof Player victim)) return;
        Player victimPlayer = (Player) e.getEntity();
        Player killerPlayer = victimPlayer.getKiller();
        if (killerPlayer == null) return;
        if (!killerPlayer.getWorld().equals(victimPlayer.getWorld())) return;

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
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Holding a captured soul.",
                ChatColor.GRAY + "Right-click to pick up."
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
    public void onItemPickup(org.bukkit.event.player.PlayerPickupItemEvent e) {
        if (e.getItem().getItemMeta() != null && e.getItem().getItemMeta().getDisplayName() != null) {
            if (e.getItem().getItemMeta().getDisplayName().contains("Soul")) {
                // Give the player a soul (increase count)
                soulManager.addSoul(e.getPlayer(), 1);
                e.getItem().setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.AQUA + "You absorbed a Soul!");
            }
        }
    }
}