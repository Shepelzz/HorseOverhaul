package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;

public class NerfListener implements Listener{
	
	
	public static double nerf;
	public static boolean override;
	
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event){
		
		if(event.getEntityType().equals(EntityType.HORSE)) {
			
			if(event.getSpawnReason().equals(SpawnReason.NATURAL)) {
					
				nerf((Horse)event.getEntity());
			}
			
//			else if(override) {
//				
//				event.getEntity().addScoreboardTag("isNerfed");
//				
//			}
		}
	}
	
	
	@EventHandler
	public void onNewChunk(ChunkLoadEvent event) {
		
		if(event.isNewChunk()) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof Horse) {
					
					nerf((Horse)e);
				}
			}
		}
		else if(override) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof Horse) {
					
					if(!e.getScoreboardTags().contains("isNerfed"))
						nerf((Horse)e);
					
				}
			}
		}
	}
	
	public static void nerf(Horse horse) {
		
		if(override) {
			
			horse.addScoreboardTag("isNerfed");
			
		}
		
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue( horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / nerf );
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() / nerf );
		
		horse.setJumpStrength( horse.getJumpStrength() / nerf );
		
	}
}
