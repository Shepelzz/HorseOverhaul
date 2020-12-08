package com.github.boltydawg.horseoverhaul;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomConfig {
	
	private static File file;
	private static FileConfiguration customFile;
	
	private static final double VERSION = 1.0;
	private static final String NAME = "settings.yml";
	
	
	/**
	 * Set up our {@link FileConfiguration} and locally stored {@link File}
	 */
	public static void setup() {
		file = fetchConfigFile(Main.instance);
		customFile = YamlConfiguration.loadConfiguration(file);
		verifyVersion();
	}
	
	/**
	 * getter for our custom {@link FileConfiguration}
	 * @return FileConfiguration
	 */
	public static FileConfiguration getConfig() {
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
			plugin.getLogger().info("created " + NAME);
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
