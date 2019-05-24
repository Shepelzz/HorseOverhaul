package com.github.boltydawg.horseoverhaul;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class FoalListener implements Listener{
	@EventHandler
	public void onSpawn(EntityBreedEvent event) {
		if(event.getEntityType().equals(EntityType.HORSE) && event.getBreeder() instanceof Player) {
			if(!(event.getFather() instanceof Horse && event.getMother() instanceof Horse)) return;
			Player player = (Player)event.getBreeder();
			StatHorse foal = new StatHorse(event.getEntity());
			foal.calculateBirth((Horse)event.getMother(),(Horse)event.getFather());
			
			StatHorse father = new StatHorse(event.getFather());
			StatHorse mother = new StatHorse(event.getMother());
			player.sendMessage(foal.printStats("Foal"));
			player.sendMessage(father.printStats("Father"));
			player.sendMessage(mother.printStats("Mother"));
		}
	}
}
