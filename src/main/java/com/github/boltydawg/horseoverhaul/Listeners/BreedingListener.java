package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import com.github.boltydawg.horseoverhaul.StatHorse;

public class BreedingListener implements Listener{
	
	public static boolean betterBreeding, foodEffects;

	@EventHandler
	public void onBreed(EntityBreedEvent event) {
		
		if(event.getEntityType().equals(EntityType.HORSE) && event.getBreeder() instanceof Player) {
			
			//ensures that the two parents are horses
			if(!(event.getFather() instanceof Horse && event.getMother() instanceof Horse)) return;
			
			Player player = (Player)event.getBreeder();
			
			//handle if either of the parents are neutered
			boolean fneut = event.getFather().getScoreboardTags().contains("ho.isNeutered");
			boolean mneut = event.getMother().getScoreboardTags().contains("ho.isNeutered");
			if( fneut || mneut ) {
				
				Horse father = (Horse)event.getFather();
				Horse mother = (Horse)event.getMother();
				
				String r = ChatColor.RESET + ChatColor.RED.toString();
				if ( fneut && mneut ) {
					player.sendMessage( r + "Both " + father.getName() + r + " and " + mother.getName() + r + " are neutered! The breed attempt fails");
				}
				else if( fneut ) {
					player.sendMessage( r + father.getName() + r + " is neutered! The breed attempt fails");
				}
				else {
					player.sendMessage( r + mother.getName() + r + " is neutered! The breed attempt fails");
				}
				
				father.setLoveModeTicks(0);
				mother.setLoveModeTicks(0);
				event.setCancelled(true);
				return;
				
			}
			
			//set the stats based off what what breeding food was used
			StatHorse foal;
			if(event.getBredWith().getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
				
				foal = new StatHorse(event.getEntity(), (byte)2);
				event.getEntity().getScoreboardTags().add("ho.isNeutered");
				
			}

			else if(event.getBredWith().getType().equals(Material.GOLDEN_APPLE))
				foal = new StatHorse(event.getEntity(), (byte)1);
			else
				foal = new StatHorse(event.getEntity());
			
			
			foal.calculateBirth((Horse)event.getMother(),(Horse)event.getFather());
			foal.roach.setTamed(true);
			
//			StatHorse father = new StatHorse(event.getFather());
//			StatHorse mother = new StatHorse(event.getMother());
//			player.sendMessage(foal.printStats("Foal"));
//			player.sendMessage(father.printStats("Father"));
//			player.sendMessage(mother.printStats("Mother"));
		}
	}
}