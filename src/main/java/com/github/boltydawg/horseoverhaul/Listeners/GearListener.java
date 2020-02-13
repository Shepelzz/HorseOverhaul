package com.github.boltydawg.horseoverhaul.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class GearListener implements Listener {
	
	@EventHandler
	public void clickEntity(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof Horse) {
			
			Horse horse = (Horse)event.getRightClicked();
			
			if(!event.getPlayer().isSneaking()) {
				
				ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
				
				if( hand.getType().equals(Material.SADDLE) && horse.getInventory().getSaddle() == null ) {
					
					horse.getInventory().setSaddle(hand);
					event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
					event.setCancelled(true);
					
				}
				
				else if(hand.getType().name().contains("HORSE_ARMOR") && horse.getInventory().getArmor() == null) {
					
					horse.getInventory().setArmor(hand);
					event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
					event.setCancelled(true);
					
				}
			}
		}
	}
}