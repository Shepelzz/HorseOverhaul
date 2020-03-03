package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.boltydawg.horseoverhaul.Main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class WhistleListener implements Listener {
	
	public static HashMap<UUID,Integer> whistleBlowers = new HashMap<UUID,Integer>();
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		
		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			
			ItemStack item = event.getItem();
			
			if(Main.blankWhistle.isSimilar(item)) {
				
				Player player = event.getPlayer();
				
				if( !whistleBlowers.containsKey(player.getUniqueId()) ) {
					
					//TODO
					player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WOLF_WHINE, 0.8f, 5.0f);
					
					ArrayList<Horse> horses = new ArrayList<Horse>();
					
					for(Entity e : player.getNearbyEntities(75, 25, 75)) {
						
						if(e.getType().equals(EntityType.HORSE)) {
							
							Horse horse = (Horse)e;
							
							if( !Main.ownership || ( horse.getScoreboardTags().contains("ho.isOwned") && player.equals(horse.getOwner()) ) ) {
								
								horses.add(horse);
								
							}
						}
						
					}
					
					if(horses.size() == 0) {
						
						player.sendMessage(ChatColor.RED + "No nearby horses found...");
						
					}
					
					else {
						//TODO: move this, and test it!!
						ItemMeta met = item.getItemMeta();
						met.getPersistentDataContainer().set(new NamespacedKey(Main.instance, "whistle"), PersistentDataType.STRING, horses.get(0).getUniqueId().toString());
						item.setItemMeta(met);
						player.sendMessage(met.getPersistentDataContainer().get(new NamespacedKey(Main.instance, "whistle"), PersistentDataType.STRING));
						
						
						for(Horse horse : horses) {
							
							//TODO
							horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0f, 1.0f);
							horse.addPotionEffect( new PotionEffect( PotionEffectType.GLOWING, 200, 1, false, false ) );
							
						}
					}
					
					whistleBlowers.put(player.getUniqueId(), 10);
					
					new BukkitRunnable() {

						@Override
						public void run() {
							
							int time = whistleBlowers.get(player.getUniqueId());
							
							if(time <= 1) {
								whistleBlowers.remove(player.getUniqueId());
								this.cancel();
							}
							else {
								whistleBlowers.put(player.getUniqueId(), time - 1);
							}
						}
					}.runTaskTimer(Main.instance, 20L, 20L);
					
				}
				
				else {
					
					TextComponent txt = new TextComponent(ChatColor.YELLOW + "You must wait " + whistleBlowers.get(player.getUniqueId()) + " more seconds");
					player.spigot().sendMessage(ChatMessageType.ACTION_BAR, txt);
					
				}
				
			}
		}
	}
	
	@EventHandler
	public void onJoin (PlayerJoinEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta met = item.getItemMeta();
		//UUID horseId = UUID.fromString(met.getPersistentDataContainer().get(new NamespacedKey(Main.instance, "whistle"), PersistentDataType.STRING));
	}
}
