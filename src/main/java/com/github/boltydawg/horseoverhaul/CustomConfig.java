package com.github.boltydawg.horseoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig {
	
	private static File file;
	private static YamlConfiguration customFile;
	
	private static final double VERSION = 1.0;
	private static final String NAME = "settings.yml";
	
	
	/**
	 * Set up our {@link FileConfiguration} and locally stored {@link File}
	 */
	public static void setup() {
		file = fetchConfigFile(Main.instance);
		customFile = YamlConfiguration.loadConfiguration(file);
		verifyVersion();
		
		customFile.addDefault("autoGearEquip.enabled", true);
		customFile.addDefault("betterBreeding.enabled", true);
		customFile.addDefault("betterBreeding.foodEffects",true);
		customFile.addDefault("checkStats.enabled", true);
		customFile.addDefault("checkStats.requireTamed", true);
		customFile.addDefault("dropGear.enabled", true);
		customFile.addDefault("ownership.enabled", true);
		customFile.addDefault("ownership.craftingRecipe", true);
		customFile.addDefault("ownership.coloredNames", false);
		customFile.addDefault("nerfWildSpawns.enabled", false);
		customFile.addDefault("nerfWildSpawns.factor", 1.5);
		customFile.addDefault("nerfWildSpawns.override", false);
		customFile.addDefault("whistles.enabled", true);
		customFile.addDefault("whistles.craftingRecipe", true);
		customFile.addDefault("whistles.teleport", false);
		
		customFile.options().copyDefaults(true);
		save();
	}
	
	/**
	 * getter for our custom {@link FileConfiguration}
	 * @return FileConfiguration
	 */
	public static YamlConfiguration getConfig() {
		return customFile;
	}
	
	/**
	 * save the current configuration to settings.yml
	 */
	public static void save() {
		try {
			customFile.save(file);
		} catch(IOException e) {
			Main.instance.getLogger().warning("Error saving config, please report this to BoltyDawg:\n" + e.toString());
		}
	}
	
	/**
	 * reloads the configuration
	 */
	public static void reload() {
		customFile = YamlConfiguration.loadConfiguration(file);
	}
	
	/**
	 * returns a file representing our custom configuration.
	 * imports from our plugin's resource folder if none exists
	 * @param plugin
	 * @return File
	 */
	private static File fetchConfigFile(JavaPlugin plugin) {
		File file = new File(plugin.getDataFolder(), NAME);
		
		if(!file.exists()) {
			plugin.saveResource(NAME, false);
			plugin.getLogger().info("creating " + NAME);
			return fetchConfigFile(plugin);
		}
		
		return file;
	}
	/**
	 * Verifies that the current config version is the most up to date one
	 */
	private static void verifyVersion() {
		// check if user is using the most up to date version of the config
		if( customFile.getDouble("config-version") != VERSION ) {
			Main.instance.getLogger().warning("Invalid config version!");
		}
	}
}
