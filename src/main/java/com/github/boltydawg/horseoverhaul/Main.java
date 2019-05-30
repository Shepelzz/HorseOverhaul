package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * A plugin that allows you to drop and trade your hard-earned xp
 * 
 * @author BoltyDawg
 */

//TODO test breeding algorithm, change to x*father + (1-x)*mother + y ?
//TODO food type specific breeding
//TODO name horse upon birth
//TODO store stats in a book
//TODO look more into horse ownernship and blocking other players from interacting with owned horses as long as said horse has armor on
//TODO documentation
//TODO bypass protection permission

public class Main extends JavaPlugin{
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static FileConfiguration config;
	public static JavaPlugin instance;
	public static ItemStack deed;
	
	@Override
	public void onEnable() {
		instance = this;
		
		config = this.getConfig();
		
		config.addDefault("autoSaddleMount", true);
		config.addDefault("betterBreeding", true);
		config.addDefault("checkHorseStats", true);
		config.addDefault("dropHorseGear", true);
		config.addDefault("horseOwnership", true);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		deed = new ItemStack(Material.PAPER);
		ItemMeta met = deed.getItemMeta();
		met.setDisplayName(ChatColor.YELLOW.toString() + ChatColor.ITALIC + "Blank Deed");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Right click an unclaimed");
		lore.add(ChatColor.GRAY + "horse to make it yours");
		met.setLore(lore);
		deed.setItemMeta(met);
		
		if(config.getBoolean("autoSaddleMount"))
			this.getServer().getPluginManager().registerEvents(new ListenerSaddle(), this);
		
		if(config.getBoolean("betterBreeding"))
			this.getServer().getPluginManager().registerEvents(new FoalListener(), this);
		
		if(config.getBoolean("checkHorseStats"))
			this.getServer().getPluginManager().registerEvents(new ListenerStats(), this);
		
		if(config.getBoolean("dropHorseGear"))
			this.getServer().getPluginManager().registerEvents(new ListenerHorseDeath(), this);
		
		if(config.getBoolean("horseOwnership")) {
			this.getServer().getPluginManager().registerEvents(new ListenerHorseOwnership(), this);
			this.getCommand("clearowners").setExecutor(new CommandClearOwners());
			
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "deed"),deed);
			recipe.addIngredient(1, Material.WRITABLE_BOOK);
			recipe.addIngredient(1, Material.GOLDEN_CARROT);
			this.getServer().addRecipe(recipe);
		}
		else {
			Iterator<Recipe> iter = getServer().recipeIterator();
			while(iter.hasNext()) {
				if(iter.next().getResult().equals(deed)) {
					iter.remove();
					break;
				}
			}
		}
	}
	@Override
	public void onDisable() {
		
	}
}
