package com.tenjava.entries.DeprecatedNether.t2;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MobPowersMethods {

    List<EntityType> mobs = new ArrayList<EntityType>();
    private TenJava main;
    private HashMap<EntityType, String> descriptions = new HashMap<EntityType, String>();

    public MobPowersMethods(TenJava main) {
        this.main = main;
        // load mobs we have superpowers for
        mobs.add(EntityType.CREEPER);
        mobs.add(EntityType.ENDERMAN);
        mobs.add(EntityType.GHAST);
        mobs.add(EntityType.SQUID);
        mobs.add(EntityType.HORSE);
        mobs.add(EntityType.SPIDER);

        descriptions.put(EntityType.CREEPER, "Create an explosion where you're standing.");
        descriptions.put(EntityType.ENDERMAN, "Teleport to the block you're looking at.");
        descriptions.put(EntityType.GHAST, "Shoot a fireball.");
        descriptions.put(EntityType.SQUID, "Breathe underwater.");
        descriptions.put(EntityType.HORSE, "Jump higher.");
        descriptions.put(EntityType.SPIDER, "Crawl faster.");
    }

    /**
     * Gives the player a token for killing a mob based on the percentage set in the config.
     * For example, if the entity type's drop chance is set to 0, this will never do anything.
     * @param entity
     */
    public boolean giveToken(Player player, EntityType entity) {
        if (!mobs.contains(entity)) {
            return false;
        }
        Random random = new Random();
        int chance = main.getConfig().getInt("powers." + entity.toString().toLowerCase() + ".drop-chance");
        if (random.nextInt(100) + 1 > chance) { // gives us a number between 1 and 100. If it's less than or equal to "chance", do something. Larger the chance, more likely this will happen
            return false;
        }
        FileConfiguration fileConfiguration = getPlayersFile();
        fileConfiguration.set(player.getName() + "." + entity.toString().toLowerCase(), 1);
        return savePlayersFile(fileConfiguration);
    }

    /**
     * Takes enough tokens from the player to use the mob power.
     * @param player The player.
     * @param entity The mob.
     * @return True if all good, false if fail (player doesn't have enough coins)
     */
    public boolean takeToken(Player player, EntityType entity) {
        if (mobs.contains(entity)) return false;
        FileConfiguration fileConfiguration = getPlayersFile();
        if (!fileConfiguration.isInt(player.getName() + "." + entity.toString().toLowerCase())) return false; // Haven't killed this entity yet
        int tokens = fileConfiguration.getInt(player.getName() + "." + entity.toString().toLowerCase());
        int price = main.getConfig().getInt("powers." + entity.toString().toLowerCase() + ".price");
        if (tokens < price) return false;
        fileConfiguration.set(player.getName() + "." + entity.toString().toLowerCase(), tokens-price);
        return savePlayersFile(fileConfiguration);
    }

    /**
     * Applies the mob power to the player.
     * @param player
     * @param entity
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean useSuperPower(Player player, EntityType entity) {
        if (!takeToken(player, entity)) return false;
        switch (entity) {
            case CREEPER:
                player.getWorld().createExplosion(player.getLocation(), 5);
                break;
            case ENDERMAN:
                player.teleport(player.getTargetBlock(null, 50).getLocation());
                break;
            case GHAST:
                player.launchProjectile(Fireball.class);
                break;
            case SQUID:
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 60, 1, true));
                break;
            case HORSE:
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 60, 2, true));
                break;
            case SPIDER:
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 2, true));
                break;
        }
        return true;
    }

    /**
     * Opens the GUI for the player.
     * @param player The player.
     */
    public void openGUI(Player player) {
        Inventory inventory = main.getServer().createInventory(player, 9, "MobPowers Selector");

    }

    /**
     * Gets the item for a particular mob.
     * @param type The entity.
     * @param tokens The number of tokens the player has.
     * @param price The number of tokens the player needs.
     */
    public ItemStack craftStack(EntityType type, int tokens, int price) {
        Material material = Material.AIR;
        switch (type) {
            case CREEPER:
                material = Material.SULPHUR;
                break;
            case ENDERMAN:
                material = Material.ENDER_PEARL;
                break;
            case GHAST:
                material = Material.FIREBALL;
                break;
            case SQUID:
                material = Material.INK_SACK;
                break;
            case HORSE:
                material = Material.DIAMOND_BARDING;
                break;
            case SPIDER:
                material = Material.SPIDER_EYE;
                break;
        }
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<String>();
        meta.setDisplayName(ChatColor.DARK_GREEN + type.toString().toUpperCase().substring(0, 1) + type.toString().toLowerCase().substring(1)); // Turn "ENDERMAN" into "Enderman"
        lore.add(ChatColor.RED + descriptions.get(type));
        lore.add(ChatColor.GOLD + "Price: " + (tokens < price ? ChatColor.RED : ChatColor.GREEN) + price);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    /**
     * Gets the number of tokens that player has for that particular mob.
     * @param player The player.
     * @param entity The mob.
     * @return The number of tokens.
     */
    public int getTokens(Player player, EntityType entity) {

    }

    /**
     * Gets the players file.
     * @return The players file.
     */
    public FileConfiguration getPlayersFile() {
        File file = new File(main.getDataFolder(), "players.yml");
        return YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Saves the players file.
     * @param configuration The FileConfiguration that has been modified.
     */
    public boolean savePlayersFile(FileConfiguration configuration) {
        try {
            configuration.save(new File(main.getDataFolder(), "players.yml"));
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }
}
