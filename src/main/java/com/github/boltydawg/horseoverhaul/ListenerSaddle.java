package com.github.boltydawg.horseoverhaul;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerSaddle implements Listener {
	
	@EventHandler
	public void clickEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Horse) {
			Horse horse = (Horse)event.getRightClicked();
			if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.SADDLE)
					&& horse.getInventory().getSaddle()==null && !event.getPlayer().isSneaking()) {
				horse.getInventory().setSaddle(event.getPlayer().getInventory().getItemInMainHand());
				event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
				horse.addPassenger(event.getPlayer());
			}
				
		}
	}
}
