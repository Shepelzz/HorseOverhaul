package com.github.boltydawg.horseoverhaul;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ListenerStats implements Listener {
	
	@EventHandler
	public void onClick(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Horse) {
			if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CARROT_ON_A_STICK)) {
				event.setCancelled(true);
				Horse horse = (Horse)event.getRightClicked();
				if(horse.getOwner() != null && horse.getOwner().getUniqueId().equals(event.getPlayer().getUniqueId())) {
					event.getPlayer().sendMessage(new StatHorse(horse).printStats(true));
				}
				else {
					event.getPlayer().sendMessage(ChatColor.RED + "You can't check the stats of someone else's horse");
				}
			}
		}
	}
}
