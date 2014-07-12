package com.tenjava.entries.DeprecatedNether.t2.commands;

import com.tenjava.entries.DeprecatedNether.t2.TenJava;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class MobPowersCommandHandler implements CommandExecutor {

    private TenJava main;

    public MobPowersCommandHandler(TenJava main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "If you aren't a player, how are you supposed to kill mobs to get tokens?");
            return true;
        }
        Player player = (Player) sender;
        if (args.length == 0) {
            main.methods.openGUI(player);
            return true;
        }
        if (args[0].equalsIgnoreCase("wand")) {
            ItemStack wand = new ItemStack(Material.STICK, 1);
            wand.setDurability((byte)2); // We'll use durability for identifying
            ItemMeta meta = wand.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD + "MobPowers Wand");
            wand.setItemMeta(meta);
            player.getInventory().addItem(wand);
            player.sendMessage(ChatColor.GREEN + "Right click with your wand to bring up the user interface.");
            return true;
        }
        player.sendMessage(ChatColor.RED + "Invalid argument '" + args[0] + "', try '/mobpowers' or '/mobpowers wand'");
        return true;
    }
}
