package com.stormai.plugin.command;

import com.stormai.plugin.SoulBoundSMP;
import com.stormai.plugin.gui.SoulGUI;
import com.stormai.plugin.manager.AbilityHandler;
import com.stormai.plugin.manager.CooldownManager;
import com.stormai.plugin.manager.SoulManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class SoulCommand implements CommandExecutor {

    private final SoulBoundSMP plugin;
    private final SoulManager soulManager;
    private final AbilityHandler abilityHandler;
    private final CooldownManager cooldownManager;

    public SoulCommand(SoulBoundSMP plugin, SoulManager soulManager, AbilityHandler abilityHandler, CooldownManager cooldownManager) {
        this.plugin = plugin;
        this.soulManager = soulManager;
        this.abilityHandler = abilityHandler;
        this.cooldownManager = cooldownManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command.");
            return true;
        }

        if (args.length == 0) {
            // Open GUI
            openSoulGUI(p);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "info" -> {
                int souls = soulManager.getSoulCount(p);
                p.sendMessage(ChatColor.AQUA + "You have " + ChatColor.YELLOW + souls + ChatColor.AQUA + " Soul(s).");
            }
            case "add" -> {
                if (!p.hasPermission("souls.add")) {
                    p.sendMessage(ChatColor.RED + "You lack permission.");
                    return true;
                }
                int amount = args.length > 1 ? Integer.parseInt(args[1]) : 1;
                soulManager.addSoul(p, amount);
                p.sendMessage(ChatColor.GREEN + "Added " + amount + " Soul(s).");
            }
            case "use" -> {
                if (args.length > 1) {
                    AbilityType type = AbilityType.valueOf(args[1].toUpperCase());
                    int secs = args.length > 2 ? Integer.parseInt(args[2]) : 30;
                    abilityHandler.applyAbility(p, type, secs);
                }
            }
            case "cooldown" -> {
                boolean can = cooldownManager.canGainSoul(p);
                p.sendMessage(ChatColor.AQUA + "Cooldown: " + (can ? "Ready" : "Active") + ChatColor.GRAY + " (" + (can ? "0s" : "...") + ")");
            }
            default -> p.sendMessage(ChatColor.RED + "Unknown subcommand.");
        }
        return true;
    }

    private void openSoulGUI(Player p) {
        Inventory inv = new SoulGUI(p.getUniqueId()).createInventory();
        p.openInventory(inv);
    }
}