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

public class Main extends JavaPlugin{
	public static DecimalFormat df = new DecimalFormat("0.00");
	public static FileConfiguration config;
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(new FoalListener(), this);
	}
	@Override
	public void onDisable() {
		
	}
}
