package com.github.boltydawg.horseoverhaul;

import org.bukkit.ChatColor;
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
			StatHorse mother = new StatHorse(event.getMother());
			StatHorse father = new StatHorse(event.getFather());
			player.sendMessage(printHorseStats(mother,"Mom"));
			player.sendMessage(printHorseStats(father,"Dad"));
		}
	}
	private String printHorseStats(StatHorse roach, String name) {
		String msg = name+":\n";
		msg += ChatColor.GREEN + ("S: " + roach.getSpeed() + " m/s\n");
		msg += ChatColor.RED + ("H: " + roach.getHealth() + " hearts\n");
		msg += ChatColor.BLUE + ("J: " + roach.getJumpHeight() + " blocks");
		return msg;
	}
}
