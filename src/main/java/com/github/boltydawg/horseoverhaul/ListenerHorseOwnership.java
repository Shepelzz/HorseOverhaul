package com.github.boltydawg.horseoverhaul;

import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ListenerHorseOwnership implements Listener {
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Horse) {
			Horse horse = (Horse) event.getEntity();
			if(horse.getOwner() != null) {
				if(event.getDamager() instanceof Player) {
					Player player = (Player) event.getDamager();
					if(horse.getOwner().getUniqueId().equals(player.getUniqueId())) {
						event.setCancelled(true);
						horse.getWorld().playSound(horse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 1.3F);
					}
					
				}
				else if(event.getDamager() instanceof Arrow) {
					Arrow arrow = (Arrow) event.getDamager();
					if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
						Player player = (Player) arrow.getShooter();
						if(horse.getOwner().getUniqueId().equals(player.getUniqueId())) {
							event.setCancelled(true);
							horse.getWorld().playSound(horse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7F, 1.3F);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onTame(EntityTameEvent event) {
		if(event.getEntity() instanceof Horse) {
			Horse horse = ((Horse)event.getEntity());
			new BukkitRunnable() {
				@Override
				public void run() {
					horse.setOwner(null);
					horse.setTamed(true);
				}
			}.runTaskLater(Main.instance, 1L);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Horse) {
			Player player = event.getPlayer();
			Horse horse = (Horse)event.getRightClicked();
			if(!horse.isTamed())
				return;
			else if(horse.getOwner() != null && horse.getInventory().getArmor() != null && !horse.getOwner().getUniqueId().equals(player.getUniqueId())) {
				horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0F, 1.0F);
				event.setCancelled(true);
				
				TextComponent msg = new TextComponent();
				msg.setText(player.getServer().getPlayer(horse.getOwner().getUniqueId()).getDisplayName() + " owns this horse");
				msg.setColor(ChatColor.RED);
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
			}
			//TODO replace this with armor equip event
			else if(player.getInventory().getItemInMainHand() != null && player.getInventory().getItemInMainHand().isSimilar(Main.deed)){
				if(horse.getOwner() != null && horse.getInventory().getArmor() != null)
					player.sendMessage("You already own this horse");
				else {
					horse.setOwner(player);
					player.sendMessage("ye");
					player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
				}
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void invClick(InventoryClickEvent event) {
		((Player)event.getWhoClicked()).sendMessage("in");
		if(event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)) {
			((Player)event.getWhoClicked()).sendMessage(event.getClickedInventory().getType().name());
		}
		else if(event.getCursor() != null && event.getCursor().getType().name().contains("HORSE_ARMOR")) {
			((Player)event.getWhoClicked()).sendMessage(event.getClickedInventory().getContents().length+"");
		}
	}
}
