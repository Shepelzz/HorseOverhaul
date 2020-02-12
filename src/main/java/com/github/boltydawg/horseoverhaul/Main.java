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
 * A plugin that improves many aspects of owning/breeding horses
 * 
 * @author BoltyDawg
 */

//TODO test breeding algorithm some more
//TODO name horse upon birth, or with deed?
//TODO store stats in a book
//TODO fix ownership
//TODO documentation, cleaning, organizing
//TODO bypass protection permission  ??
//TODO test mixing food types

/**
 * FEATURES:
 * AutoGearEquip:
 * 	Saddles and armor get equipped on the horse when you right click it, rather than opening up their inventory. Can shift click to open the inventory still
 * Better Breeding:
 * 	new breeding algorithm that provides more generational improvements rather than complete luck
 * BetterBreedingFoodMultipliers:
 * 	get better results from your breeding by using better foods. 
 * 	Enchanted golden apples give you a maxed out horse, and golden apples make it so you can't do worse than the lower of your two parents!
 * checkHorseStats:
 * 	right click a tamed horse to check its stats. Compatible with ownership feature
 * dropHorseGear:
 * 	Horses drop their gear upon death
 * Ownership:
 * 	can't damage your own horse if it's wearing armor
 *
 */

public class Main extends JavaPlugin{
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static FileConfiguration config;
	public static JavaPlugin instance;
	public static ItemStack deed;
	
	@Override
	public void onEnable() {
		instance = this;
		
		config = this.getConfig();
		
		config.addDefault("autoGearEquip", true);
		config.addDefault("betterBreeding", true);
		config.addDefault("betterBreedingFoodMultipliers",true);
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
		
		if(config.getBoolean("autoGearEquip"))
			this.getServer().getPluginManager().registerEvents(new ListenerGear(), this);
		
		if(config.getBoolean("betterBreeding"))
			this.getServer().getPluginManager().registerEvents(new FoalListener(), this);
		
		if(config.getBoolean("checkHorseStats"))
			this.getServer().getPluginManager().registerEvents(new ListenerStats(), this);
		
		if(config.getBoolean("dropHorseGear"))
			this.getServer().getPluginManager().registerEvents(new ListenerHorseDeath(), this);
		
		if(config.getBoolean("horseOwnership")) {
			this.getServer().getPluginManager().registerEvents(new ListenerHorseOwnership(), this);
			
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
