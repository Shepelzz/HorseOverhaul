package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import com.github.boltydawg.horseoverhaul.StatHorse;

public class StatsListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOW)
	public void onClick(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Horse) {
			Player player = event.getPlayer();
			Horse horse = (Horse)event.getRightClicked();
			if(!horse.isTamed() || event.isCancelled()) {
				return;
			}
			else if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CARROT_ON_A_STICK)) {
				event.setCancelled(true);
				player.sendMessage(new StatHorse(horse).printStats(true));
			}
		}
	}
}
