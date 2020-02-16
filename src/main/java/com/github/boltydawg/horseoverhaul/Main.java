package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.boltydawg.horseoverhaul.Listeners.FoalListener;
import com.github.boltydawg.horseoverhaul.Listeners.GearListener;
import com.github.boltydawg.horseoverhaul.Listeners.NerfListener;
import com.github.boltydawg.horseoverhaul.Listeners.DeathListener;
import com.github.boltydawg.horseoverhaul.Listeners.OwnershipListener;
import com.github.boltydawg.horseoverhaul.Listeners.StatsListener;


/**
 * A plugin that improves many aspects of owning/breeding horses
 * 
 * @author BoltyDawg
 */

//TODO test breeding algorithm some more
//TODO test with multiple users
//TODO test nerfing
//TODO maybe change isNeutered to something like isSterile?
//TODO add whistle feature?

/**
 * FEATURES:
 * AutoGearEquip:
 * 	Saddles and armor get equipped on the horse when you right click it, rather than opening up their inventory. Can shift click to open the inventory still
 * Better Breeding:
 * 	new breeding algorithm that provides more generational improvements rather than complete luck
 * BetterBreedingFoodMultipliers:
 * 	get better results from your breeding by using better foods. 
 * 	Enchanted golden apples give you a maxed out horse (but it can't breed!), and golden apples make it so you can't do worse than the lower of your two parents!
 * checkHorseStats:
 * 	right click a tamed horse to check its stats. Compatible with ownership feature
 * 	store the stats your horses on a sign
 * dropHorseGear:
 * 	Horses drop their gear upon death
 * Ownership:
 * 	can't damage your own horse if it's wearing armor
 * 	claim a horse as yours by clicking on it with a blank deed, or click on it with its existing deed to transfer owners
 * 	blank deeds name the horse
 * 	right click an owned foal with shears to "neuter" it: it can't breed, and you can safely sell it to someone
 * 	Check an owned horse's stats by right clicking it with a carrot on a stick
 * nerfWildHorses:
 * 	reduce the starting stats of wild horses by a chosen factor (default is 1.5) in order to require players to breed to get the best horses
 * 	can also override any existing horses (treats any pre-existing horse in your world as "wild", and nerfs it)
 */

public class Main extends JavaPlugin{
	
	public static DecimalFormat df = new DecimalFormat("0.00");
	
	public static JavaPlugin instance;
	
	public static ItemStack blankDeed;
	
	public static boolean foodEffects;
	
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		FileConfiguration config = this.getConfig();
		
		config.options().header("HorseOverhaul Configuration\n\n   Please read about each option on the Spigot page.");
		
		config.addDefault("autoGearEquip", true);
		config.addDefault("betterBreeding", true);
		config.addDefault("betterBreeding_foodEffects",true);
		config.addDefault("checkHorseStats", true);
		config.addDefault("dropHorseGear", true);
		config.addDefault("horseOwnership", true);
		config.addDefault("nerfWildHorses", false);
		config.addDefault("nerfWildHorses_factor", 1.5);
		config.addDefault("nerfWildHorses_override", false);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		blankDeed = new ItemStack(Material.PAPER);
		ItemMeta met = blankDeed.getItemMeta();
		met.setDisplayName("Blank Deed");
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Right click an unclaimed");
		lore.add(ChatColor.GRAY + "horse to make it yours");
		met.setLore(lore);
		blankDeed.setItemMeta(met);
		
		
		if(config.getBoolean("autoGearEquip")) {

			this.getServer().getPluginManager().registerEvents(new GearListener(), this);
			
		}
		if(config.getBoolean("betterBreeding")) {
			
			this.getServer().getPluginManager().registerEvents(new FoalListener(), this);
			
			if(config.getBoolean("betterBreeding_foodEffects")) {
				
				foodEffects = true;
				
			}
			
		}
		if(config.getBoolean("checkHorseStats")) {
			
			this.getServer().getPluginManager().registerEvents(new StatsListener(), this);
			
		}
		
		if(config.getBoolean("dropHorseGear")) {
			
			this.getServer().getPluginManager().registerEvents(new DeathListener(), this);
			
		}
		if(config.getBoolean("horseOwnership")) {
			
			this.getServer().getPluginManager().registerEvents(new OwnershipListener(), this);
			
			ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "blankDeed"),blankDeed);
			recipe.addIngredient(1, Material.WRITABLE_BOOK);
			recipe.addIngredient(1, Material.GOLDEN_CARROT);
			this.getServer().addRecipe(recipe);
			
		}
		else {
			
			Iterator<Recipe> iter = getServer().recipeIterator();
			
			while(iter.hasNext()) {
				
				if(iter.next().getResult().equals(blankDeed)) {
					
					iter.remove();
					break;
					
				}
			}
			
		}
		if(config.getBoolean("nerfWildHorses")) {
			this.getServer().getPluginManager().registerEvents(new NerfListener(), this);
			
			NerfListener.nerf = config.getDouble("nerfWildHorses_factor", 1.5);
			
			if(config.getBoolean("nerfWildHorses_override")) {
				NerfListener.override = true;
				
				for (World w: instance.getServer().getWorlds()){
					for(LivingEntity e: w.getLivingEntities()) {
						if(e.isValid() && e instanceof Horse && !e.getScoreboardTags().contains("isNerfed")) {
							NerfListener.nerf((Horse)e);
						}
					}
				}
				
			}
			else
				NerfListener.override = false;
			
		}
	}
	
}