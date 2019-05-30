package com.github.boltydawg.horseoverhaul;

import java.text.DecimalFormat;

import org.bukkit.configuration.file.FileConfiguration;
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
