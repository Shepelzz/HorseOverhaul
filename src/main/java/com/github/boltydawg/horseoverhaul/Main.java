package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A plugin that allows you to drop and trade your hard-earned xp
 * 
 * @author BoltyDawg
 */

//TODO add feature for viewing any horse's stats
//TODO re-work horse breeding algorithm
//TODO improve printHorseStats(), add symbols
//TODO food type specific breeding
//TODO name horse upon birth

public class Main extends JavaPlugin{
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static FileConfiguration config;
	public static JavaPlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		config = this.getConfig();
		
		config.addDefault("autoSaddleMount", true);
		config.addDefault("betterBreeding", true);
		config.addDefault("checkHorseStats", true);
		config.addDefault("dropHorseGear", true);
		
		config.options().copyDefaults(true);
		saveConfig();
		
		if(config.getBoolean("autoSaddleMount"))
			this.getServer().getPluginManager().registerEvents(new ListenerSaddle(), this);
		
		if(config.getBoolean("betterBreeding"))
			this.getServer().getPluginManager().registerEvents(new FoalListener(), this);
		
		if(config.getBoolean("checkHorseStats"))
			this.getServer().getPluginManager().registerEvents(new ListenerStats(), this);
		
		if(config.getBoolean("dropHorseGear"))
			this.getServer().getPluginManager().registerEvents(new ListenerHorseDeath(), this);
	}
	@Override
	public void onDisable() {
		
	}
}
