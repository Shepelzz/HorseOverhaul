package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.boltydawg.horseoverhaul.Listeners.FoalListener;
import com.github.boltydawg.horseoverhaul.Listeners.GearListener;
import com.github.boltydawg.horseoverhaul.Listeners.NerfListener;
import com.github.boltydawg.horseoverhaul.Listeners.DeathListener;
import com.github.boltydawg.horseoverhaul.Listeners.OwnershipListener;
import com.github.boltydawg.horseoverhaul.Listeners.StatsListener;
import com.github.boltydawg.horseoverhaul.Listeners.WhistleListener;


/**
 * A plugin that improves many aspects of owning/breeding horses
 * 
 * @author BoltyDawg
 */

//TODO horse stamina
	//TODO boosting: consumes a carrot off a carrot on a stick, speeds you up and depletes stamina
//TODO horse capturing system like pokemon?
//TODO test breeding algorithm some more?
//TODO fix bug with client side not getting nametag back?
//TODO maybe change ho.isNeutered to something like isSterile?
//TODO add whistle feature?
//TODO look into changing method of storing horse data?
//TODO add way to "unlock" a horse?


public class Main extends JavaPlugin{
	
	public static DecimalFormat df = new DecimalFormat("0.00");
	
	public static JavaPlugin instance;
	
	public static ItemStack blankDeed, blankWhistle;
	
	public static boolean foodEffects, ownership, coloredNames;
	
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
		config.addDefault("horseOwnership_craftDeeds", true);
		config.addDefault("horseOwnership_coloredNames", false);
		config.addDefault("nerfWildHorses", false);
		config.addDefault("nerfWildHorses_factor", 1.5);
		config.addDefault("nerfWildHorses_override", false);
		config.addDefault("whistle", true);
		config.addDefault("whistle_recipe", true);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		this.getCommand("horseo").setExecutor(new CommandHorseo());
		
		
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
			
			ownership = true;
			
			blankDeed = new ItemStack(Material.PAPER);
			ItemMeta met = blankDeed.getItemMeta();
			met.setDisplayName("Blank Deed");
			ArrayList<String> lore = new ArrayList<String>();
			lore.add(ChatColor.GRAY + "Right click an unclaimed");
			lore.add(ChatColor.GRAY + "horse to make it yours");
			met.setLore(lore);
			blankDeed.setItemMeta(met);
			
			if(config.getBoolean("horseOwnership_craftDeeds")) {
				
				ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "blankDeed"),blankDeed);
				recipe.addIngredient(1, Material.WRITABLE_BOOK);
				recipe.addIngredient(1, Material.GOLDEN_CARROT);
				
				this.getServer().addRecipe(recipe);
				
			}
			
			coloredNames = config.getBoolean("horseOwnership_coloredNames");
				
		}
		if(config.getBoolean("nerfWildHorses")) {
			
			this.getServer().getPluginManager().registerEvents(new NerfListener(), this);
			
			NerfListener.nerf = config.getDouble("nerfWildHorses_factor", 1.5);
			
			if(config.getBoolean("nerfWildHorses_override")) {
				
				NerfListener.override = true;
				
				for (World w: instance.getServer().getWorlds()){
					for(LivingEntity e: w.getLivingEntities()) {
						if(e.isValid() && e instanceof Horse && !e.getScoreboardTags().contains("ho.isNerfed")) {
							NerfListener.nerf((Horse)e);
						}
					}
				}
				
			}
			else
				NerfListener.override = false;
		}
		if(config.getBoolean("whistle")) {
			
			//TODO: change this to be a "hollow" whistle, then actually usable whistles store the horse's UUID?
			blankWhistle = new ItemStack(Material.IRON_NUGGET);
			ItemMeta met = blankWhistle.getItemMeta();
			met.setDisplayName(ChatColor.YELLOW + "Blank Whistle");
			met.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			blankWhistle.setItemMeta(met);
			
			this.getServer().getPluginManager().registerEvents(new WhistleListener(), this);
			
			if(config.getBoolean("whistle_recipe")) {
				
				ShapelessRecipe recipe = new ShapelessRecipe(new NamespacedKey(this, "whistle"), blankWhistle);
				recipe.addIngredient(1, Material.IRON_INGOT);
				recipe.addIngredient(1, Material.GOLDEN_CARROT);
				
				this.getServer().addRecipe(recipe);
				
			}
		}
	}
	
}