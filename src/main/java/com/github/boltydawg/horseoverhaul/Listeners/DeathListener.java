package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		
		if(event.getEntity() instanceof AbstractHorse) {
			
			AbstractHorse abHorse = (AbstractHorse)event.getEntity();
			if(!event.getDrops().contains(abHorse.getInventory().getSaddle()))
				event.getDrops().add(abHorse.getInventory().getSaddle());
			
			if (event.getEntity() instanceof Horse) {
				Horse horse = (Horse)event.getEntity();
				if(!event.getDrops().contains(horse.getInventory().getArmor()))
					event.getDrops().add(horse.getInventory().getArmor());
			}	
		}
	}
}