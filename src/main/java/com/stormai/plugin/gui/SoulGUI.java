package com.stormai.plugin.gui;

import com.stormai.plugin.SoulBoundSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class SoulGUI {

    private final UUID owner;
    private final Inventory inventory;

    public SoulGUI(UUID owner) {
        this.owner = owner;
        int size = 27; // 3 rows
        this.inventory = Bukkit.createInventory(null, size, ChatColor.DARK_BLUE + "Soul Menu");
    }

    public Inventory createInventory() {
        // Example: Fill background
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, createItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GRAY + " ", ""));
        }

        // Soul count display
        ItemStack soulItem = createItem(Material.ENDER_PEARL, ChatColor.YELLOW + "Souls: " + ChatColor.AQUA + SoulBoundSMP.getInstance().getSoulManager().getSoulCount(Bukkit.getPlayer(owner)), "");
        ItemMeta meta = soulItem.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        soulItem.setItemMeta(meta);
        inventory.setItem(11, soulItem);

        // Example ability slots
        inventory.setItem(13, createAbilityItem(Material.DIAMOND_SWORD, "Strength (30s)", ChatColor.GREEN + "+3 Attack"));
        inventory.setItem(15, createAbilityItem(Material.ARROW, "Speed (30s)", ChatColor.AQUA + "+0.2 Speed"));
        inventory.setItem(17, createAbilityItem(Material.HEART_OF_THE_SEA, "Extra Heart (30s)", ChatColor.WHITE + "+1 Heart"));

        // Close placeholder
        inventory.setItem(26, createItem(Material.BARRIER, "Close", ChatColor.RED + "Close"));
        return inventory;
    }

    private ItemStack createItem(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(java.util.Arrays.asList(ChatColor.GRAY + lore));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createAbilityItem(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name);
        meta.setLore(java.util.Arrays.asList(ChatColor.GRAY + lore));
        meta.addEnchant(org.bukkit.enchantment.Enchantment.DURABILITY, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    /** Handle clicks on GUI items */
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p) || !e.getInventory().getHolder() instanceof SoulGUI) return;

        e.setCancelled(true);
        org.bukkit.Material mat = e.getCurrentItem() != null ? e.getCurrentItem().getType() : null;

        switch (mat) {
            case DIAMOND_SWORD -> {
                // Consume a soul and apply strength for 30s
                SoulBoundSMP.getInstance().getAbilityHandler().applyAbility(p, AbilityType.STRENGTH, 30);
                p.sendMessage(ChatColor.GREEN + "Strength granted for 30 seconds!");
            }
            case ARROW -> {
                SoulBoundSMP.getInstance().getAbilityHandler().applyAbility(p, AbilityType.SPEED, 30);
                p.sendMessage(ChatColor.AQUA + "Speed granted for 30 seconds!");
            }
            case HEART_OF_THE_SEA -> {
                SoulBoundSMP.getInstance().getAbilityHandler().applyAbility(p, AbilityType.EXTRA_HEART, 30);
                p.sendMessage(ChatColor.WHITE + "Extra Heart granted for 30 seconds!");
            }
            case BARRIER -> p.closeInventory();
            case ENDER_PEARL -> {
                // Just display info, no action
            }
        }
    }
}