package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {
	
	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		
		if(event.getEntity() instanceof Horse) {
			
			Horse horse = (Horse)event.getEntity();
			if(!event.getDrops().contains(horse.getInventory().getSaddle()))
				event.getDrops().add(horse.getInventory().getSaddle());
			
			if(!event.getDrops().contains(horse.getInventory().getArmor()))
				event.getDrops().add(horse.getInventory().getArmor());
			
		}
	}
}