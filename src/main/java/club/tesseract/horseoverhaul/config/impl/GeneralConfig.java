package club.tesseract.horseoverhaul.config.impl;

import club.tesseract.horseoverhaul.config.AbstractConfig;

public class GeneralConfig extends AbstractConfig {

	public GeneralConfig() {
		super();
	}
	
	@Override
	public String configName() {
		return "settings.yml";
	}
	

	@Override
	protected void addDefaults() {
		customFile.addDefault("better-breeding.enabled", true);
		customFile.addDefault("better-breeding.food-effects",true);
		customFile.addDefault("check-stats.enabled", true);
		customFile.addDefault("check-stats.require-tamed", true);
		customFile.addDefault("ownership.enabled", true);
		customFile.addDefault("ownership.colored-names", false);
		customFile.addDefault("nerf-wild-spawns.enabled", false);
		customFile.addDefault("nerf-wild-spawns.divisor", 1.5);
		customFile.addDefault("nerf-wild-spawns.override", false);
		customFile.addDefault("whistles.enabled", true);
		customFile.addDefault("whistles.cooldown", 10);
		customFile.addDefault("whistles.teleport", false);
		
		customFile.options().copyDefaults(true);
		customFile.options().header("HorseOverhaul Configuration\n\nAnytime you change an option here be sure to run the command \"/horseoverhaul reload\"\n\n");
		
		save();
	}

	public boolean isBetterBreedingEnabled() {
		return customFile.getBoolean("better-breeding.enabled");
	}

	public boolean isFoodEffectsEnabled() {
		return customFile.getBoolean("better-breeding.food-effects");
	}

	public boolean isCheckStatsEnabled() {
		return customFile.getBoolean("check-stats.enabled");
	}

	public boolean isCheckStatsRequireTamed() {
		return customFile.getBoolean("check-stats.require-tamed");
	}

	public boolean isOwnershipEnabled() {
		return customFile.getBoolean("ownership.enabled");
	}

	public boolean isColoredNamesEnabled() {
		return customFile.getBoolean("ownership.colored-names");
	}

	public boolean isNerfWildSpawnsEnabled() {
		return customFile.getBoolean("nerf-wild-spawns.enabled");
	}

	public double getNerfWildSpawnsDivisor() {
		return customFile.getDouble("nerf-wild-spawns.divisor");
	}

	public boolean isNerfWildSpawnsOverride() {
		return customFile.getBoolean("nerf-wild-spawns.override");
	}

	public boolean isWhistleEnabled(){
		return customFile.getBoolean("whistles.enabled");
	}

	public int getWhistleCooldown() {
		return customFile.getInt("whistles.cooldown");
	}

	public boolean isWhistleTeleportEnabled() {
		return customFile.getBoolean("whistles.teleport");
	}

}
