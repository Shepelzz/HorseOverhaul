package club.tesseract.horseoverhaul;

import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import club.tesseract.horseoverhaul.command.CommandHorseo;
import club.tesseract.horseoverhaul.config.ConfigManager;
import club.tesseract.horseoverhaul.config.impl.GeneralConfig;
import club.tesseract.horseoverhaul.listener.*;
import club.tesseract.horseoverhaul.metric.Metrics;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;


/**
 * A plugin that improves many aspects of owning/breeding horses
 * 
 * @author BoltyDawg
 * @author TropicalShadow
 */
public class HorseOverhaul extends JavaPlugin{
	
	public static DecimalFormat statNumberFormat = new DecimalFormat("0.00");
	private Metrics metrics;
	private BreedingListener breeding;
	private StatsListener stats;
	private OwnershipListener ownership;
	private NerfListener nerf;
	private WhistleListener whistle;

	private BukkitAudiences adventure;

	public @NotNull BukkitAudiences adventure() {
		if(this.adventure == null) {
			throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
		}
		return this.adventure;
	}

	@Override
	public void onEnable() {
		this.adventure = BukkitAudiences.create(this);
		ConfigManager.get().loadConfigs();
		metrics = new Metrics(this, 10086);

		//set constant listeners
		getServer().getPluginManager().registerEvents(new GearListener(), this);
		
		// commands
		getCommand("horseoverhaul").setExecutor(new CommandHorseo());
	}
	
	@Override
	public void onDisable() {
		this.getLogger().info("Plugin shutting down!");
		metrics.shutdown();
		if(this.adventure != null) {
			this.adventure.close();
			this.adventure = null;
		}
	}

	/**
	 * read and set the config's values
	 */
	public void loadConfig() {
		GeneralConfig generalConfig = ConfigManager.get().generalConfig;

		if(generalConfig.isBetterBreedingEnabled()) {
			//initialize
			this.breeding = new BreedingListener();

			//register listener
			this.getServer().getPluginManager().registerEvents(breeding, this);

			//set other fields
			BreedingListener.BETTER_BREEDING_ENABLED = true;

			BreedingListener.FOOD_EFFECTS = generalConfig.isFoodEffectsEnabled();
		}

		if(generalConfig.isCheckStatsEnabled()) {
			//initialize
			this.stats = new StatsListener();

			//register listener
			this.getServer().getPluginManager().registerEvents(stats, this);

			//set other fields
			StatsListener.CHECK_STATS_ENABLED = true;

			StatsListener.REQUIRE_TAMED = generalConfig.isCheckStatsRequireTamed();
		}

		if(generalConfig.isOwnershipEnabled()) {
			//initialize
			this.ownership = new OwnershipListener();

			//register the listener
			this.getServer().getPluginManager().registerEvents(ownership, this);

			//set other fields
			OwnershipListener.OWNERSHIP_ENABLED = true;

			OwnershipListener.COLOURED_NAMES = generalConfig.isColoredNamesEnabled();

		}

		if(generalConfig.isNerfWildSpawnsEnabled()) {
			//initialize
			this.nerf = new NerfListener();

			//register the listener
			this.getServer().getPluginManager().registerEvents(nerf, this);

			//set other fields
			NerfListener.NERF_DIVISOR = generalConfig.getNerfWildSpawnsDivisor();

			NerfListener.NERF_OVERRIDE = generalConfig.isNerfWildSpawnsOverride();
			if(NerfListener.NERF_OVERRIDE) {
				for (World world: Bukkit.getServer().getWorlds()){
					for(LivingEntity entity: world.getLivingEntities()) {
						if(entity.isValid() && entity instanceof AbstractHorse
								&& PersistentAttribute.NERFED.getData(entity, (byte)0) == (byte)0) {
							NerfListener.nerf((AbstractHorse)entity);
						}
					}
				}
			}
		}

		if(generalConfig.isWhistleEnabled()) {
			//initialize
			this.whistle = new WhistleListener();

			//register the listener
			this.getServer().getPluginManager().registerEvents(whistle, this);

			//set other fields
			WhistleListener.WHISTLE_ENABLED = true;
			WhistleListener.WHISTLE_TP = generalConfig.isWhistleTeleportEnabled();
			WhistleListener.WHISTLE_COOLDOWN = generalConfig.getWhistleCooldown();
		}
	}
	
	public void removeListeners() {
		// Unregister and unload the all listeners, for the case of usage of /horseoverhaulverhaul reload
		if (this.breeding != null) {
			
			HandlerList.unregisterAll(this.breeding);
			
			this.breeding = null;
			
			BreedingListener.BETTER_BREEDING_ENABLED = false;
		}
		
		if (this.stats != null) {
			HandlerList.unregisterAll(stats);
			
			this.stats = null;
			
			StatsListener.CHECK_STATS_ENABLED = false;
		}
		
		if (this.ownership != null) {
			HandlerList.unregisterAll(ownership);
			
			this.ownership = null;
			
			OwnershipListener.OWNERSHIP_ENABLED = true;
			OwnershipListener.craftDeed = false;
			OwnershipListener.COLOURED_NAMES = false;
		}
		
		if (this.nerf != null) {
			HandlerList.unregisterAll(nerf);
			
			this.nerf = null;
		}
		
		if (this.whistle != null){
			HandlerList.unregisterAll(whistle);
			
			this.whistle = null;
			
			WhistleListener.WHISTLE_ENABLED = false;
		}

		ConfigManager.get().unregisterRecipes();
	}

	@NotNull
	public static HorseOverhaul getInstance() {
		return HorseOverhaul.getPlugin(HorseOverhaul.class);
	}

}