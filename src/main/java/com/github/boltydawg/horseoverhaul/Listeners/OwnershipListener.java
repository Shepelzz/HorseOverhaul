package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.boltydawg.horseoverhaul.Main;
import com.github.boltydawg.horseoverhaul.NamePrompt;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class OwnershipListener implements Listener {
	
	private static ItemStack getDeed(UUID horseID, String horsey, UUID pID, String pname) {
		
		ItemStack deed = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta met = (BookMeta)deed.getItemMeta();
		
		met.setDisplayName(ChatColor.GREEN.toString() + "Deed to " + horsey);
		met.setAuthor(null);
		met.setGeneration(Generation.ORIGINAL);
		
		ArrayList<String> lore = new ArrayList<String>();
		lore.add(ChatColor.BLUE + "Property of");
		lore.add(ChatColor.BLUE + pname);
		met.setLore(lore);
		
		
		met.setPages(horseID.toString(), pID.toString());
		
		deed.setItemMeta(met);
		
		return deed;
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		
		if(event.getEntity() instanceof Horse) {
			
			Horse horse = (Horse) event.getEntity();
			
			if(horse.getOwner() != null && horse.getInventory().getArmor() != null && horse.getScoreboardTags().contains("ho.isOwned")) {
				
				if(event.getDamager() instanceof Player) {

					Player player = (Player) event.getDamager();
					
					if(horse.getOwner().getUniqueId().equals(player.getUniqueId())) {
						
						event.setCancelled(true);
						horse.getWorld().playSound(horse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 1.3F);
						
					}
				}

				else if(event.getDamager() instanceof Arrow) {
					
					Arrow arrow = (Arrow) event.getDamager();
					
					if(arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
						
						Player player = (Player) arrow.getShooter();
						
						if(horse.getOwner().getUniqueId().equals(player.getUniqueId())) {
							
							event.setCancelled(true);
							horse.getWorld().playSound(horse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 1.3F);
							
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
	
	@EventHandler(priority = EventPriority.LOW)
	public void onClickHorse(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof Horse) {
			
			Player player = event.getPlayer();
			Horse horse = (Horse)event.getRightClicked();
			ItemStack main = player.getInventory().getItemInMainHand();
			ItemStack off = player.getInventory().getItemInOffHand();
			
			if(Material.NAME_TAG.equals(main.getType()) || Material.NAME_TAG.equals(off.getType())) {
				
				
				if(horse.getOwner() != null && horse.getScoreboardTags().contains("ho.isOwned") && horse.getOwner().getUniqueId().equals(player.getUniqueId()) ) {
					
					if(main != null && off != null && off.equals(getDeed(horse.getUniqueId(), horse.getCustomName(), player.getUniqueId(), player.getName())) 
							&& main.getItemMeta().hasDisplayName()) {
						
						player.getInventory().setItemInOffHand(getDeed(horse.getUniqueId(), main.getItemMeta().getDisplayName(), player.getUniqueId(), player.getName()) );
						
					}
					else {
						
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You must be holding this horse's deed in your off hand in order to rename it!");
						
					}
				}
				else {
					
					event.setCancelled(true);
					player.sendMessage("you can only rename a horse that you own!");
					
				}	
			}
			
			else if(event.getHand().equals(EquipmentSlot.HAND)) {
				
				if(horse.getOwner() != null && horse.getScoreboardTags().contains("ho.isOwned")) {
					
					if( !horse.getOwner().getUniqueId().equals(player.getUniqueId()) ) {
						
						if( main != null && main.getItemMeta() != null && main.getItemMeta().hasDisplayName() 
								&& main.getItemMeta().getDisplayName().contains("Deed to ")
								&& main.getItemMeta().getLore() != null
								&& main.getItemMeta().getLore().get(0).contains("Property of")
								&& main.getType().equals(Material.WRITTEN_BOOK) ) {
							//TODO
							player.sendMessage("hey");
							
							BookMeta met = (BookMeta)main.getItemMeta();
							
							UUID horseId = UUID.fromString(met.getPage(1));
						
							if(horse.getUniqueId().equals(horseId)) {
								
								if(Generation.ORIGINAL.equals(met.getGeneration())) {
									
									player.getInventory().setItemInMainHand(null);
									claimHorse(horse, player, horse.getCustomName());

								}
								else {
							
									TextComponent msg = new TextComponent();
									msg.setText("You need the original copy!");
									msg.setColor(ChatColor.RED);
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
									
									horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 0.8F, 1.0F);
							
								}
								
								event.setCancelled(true);
								return;
							}		
						}
						
						horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 0.8F, 1.0F);
						event.setCancelled(true);
				
						TextComponent msg = new TextComponent();
						msg.setText(player.getServer().getOfflinePlayer(horse.getOwner().getUniqueId()).getName() + " owns this horse!");
						msg.setColor(ChatColor.RED);
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						
					}
					
					else if( !horse.isAdult() && Material.SHEARS.equals(main.getType())) {
						
						if(horse.getScoreboardTags().contains("ho.isNeutered")) {
							
							player.sendMessage(ChatColor.RED + ("You've already neutered "+ horse.getCustomName() + "!"));
						}
						
						else {
							
							horse.getScoreboardTags().add("ho.isNeutered");
							
							horse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 10));
							horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_DEATH, 1.2f, 1.5f);
							
							new BukkitRunnable() {
								@Override
								public void run() {
									horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.9f, 1.3f);
									player.sendMessage(ChatColor.RED + ("You have successfully neutered "+ horse.getCustomName() + ". He/she will never breed."));
								}
							}.runTaskLater(Main.instance, 20L);
							
						}
					}
					
				}
				else if(main != null && horse.isTamed()) {
					if(main.isSimilar(Main.blankDeed)) {
					
						event.setCancelled(true);
						
						if(player.isConversing()) {
							
							player.sendRawMessage(ChatColor.RED + "You must finish naming the current horse before claiming another!");
							return;
							
						}
						
						player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
						
						ConversationFactory cf = new ConversationFactory(Main.instance);
						Conversation conv = cf.withFirstPrompt(new NamePrompt(player,horse)).withLocalEcho(true).buildConversation(player);
						conv.begin();
					}	
				}
			}
		}
	}
	
	public static void claimHorse(Horse horse, Player player, String horseName) {
		
		horse.setOwner(player);
		horse.getScoreboardTags().add("ho.isOwned");
		horse.setCustomName(horseName);
		
		horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ARMOR, 0.9f, 2.0f);
		player.sendRawMessage(ChatColor.GREEN + ("You are now the proud owner of " + horseName + "!"));
		
		player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 0.5f);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				player.getInventory().addItem(getDeed(horse.getUniqueId(), horse.getCustomName(), player.getUniqueId(), player.getName()));
				
			}
		}.runTaskLater(Main.instance, 4);
		
		
	}
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		
		if(event.getItem() != null && event.getItem().getItemMeta().hasDisplayName() 
				&& event.getItem().getItemMeta().getDisplayName().contains("Deed to ")
				&& event.getItem().getItemMeta().getLore() != null 
				&& event.getItem().getItemMeta().getLore().get(0).contains("Property of")) {
			
			event.setUseItemInHand(Result.DENY);
			
		}
	}
}