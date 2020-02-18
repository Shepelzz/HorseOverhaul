package com.github.boltydawg.horseoverhaul.Listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.github.boltydawg.horseoverhaul.Main;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class OwnershipListener implements Listener {
	
	private ItemStack getDeed(UUID horseID, String horsey, UUID pID, String pname) {
		
		ItemStack deed = new ItemStack(Material.WRITTEN_BOOK);
		BookMeta met = (BookMeta)deed.getItemMeta();
		
		met.setDisplayName(ChatColor.GREEN.toString() + ChatColor.ITALIC + "Deed to " + horsey);
		met.setAuthor(null);
		
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
			
			if(horse.getOwner() != null && horse.getInventory().getArmor() != null) {
				
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
	public void onClickHorse(PlayerInteractEntityEvent event) {
		
		if(event.getRightClicked() instanceof Horse) {
			
			Player player = event.getPlayer();
			Horse horse = (Horse)event.getRightClicked();
			ItemStack main = player.getInventory().getItemInMainHand();
			ItemStack off = player.getInventory().getItemInOffHand();
			
			if(main.getType().equals(Material.NAME_TAG) || off.getType().equals(Material.NAME_TAG)) {
				
				
				if(horse.getOwner() != null && horse.getScoreboardTags().contains("ho.isOwned") && horse.getOwner().getUniqueId() == player.getUniqueId() ) {
					
					if(off.equals(getDeed(horse.getUniqueId(), horse.getCustomName(), player.getUniqueId(), player.getName())) 
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
					
					if( horse.getOwner().getUniqueId() != player.getUniqueId() ) {
						
						if( main.getItemMeta().getDisplayName().contains(ChatColor.GREEN.toString() + ChatColor.ITALIC + "Deed to ") && main.getType().equals(Material.WRITTEN_BOOK)) {
							
							BookMeta met = (BookMeta)main.getItemMeta();
							
							UUID horseId = UUID.fromString(met.getPage(1));
						
							if(horseId == horse.getUniqueId()) {
								
								if(met.getGeneration().equals(BookMeta.Generation.ORIGINAL)) {
									
									player.getInventory().setItemInMainHand(null);
									claimHorse(horse, player, met.getDisplayName());

								}
								else {
							
									TextComponent msg = new TextComponent();
									msg.setText("You need the original copy!");
									msg.setColor(ChatColor.RED);
									player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
									
									horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0F, 1.0F);
							
								}
								
								event.setCancelled(true);
								return;
							}		
						}
						
						horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0F, 1.0F);
						event.setCancelled(true);
				
						TextComponent msg = new TextComponent();
						msg.setText(player.getServer().getPlayer(horse.getOwner().getUniqueId()).getDisplayName() + " owns this horse!");
						msg.setColor(ChatColor.RED);
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR,msg);
						
					}
					
					else if( !horse.isAdult() && main.getType().equals(Material.SHEARS)) {
						
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
				else if(main != null) {
					if(main.isSimilar(Main.blankDeed)){
					
						event.setCancelled(true);
						player.sendMessage("You have to name this horse before you can claim it!\n"
								+ "Use an anvil to change the name of this blank deed to the name of your new horse, not a nametag!.");
						
					}
					
					else if(main.getType().equals(Material.PAPER)) {
						
						ItemMeta met = main.getItemMeta();
						List<String> lore = met.getLore();
						if (lore != null && lore.size() == 2) {
							
							if(lore.get(0).equals(ChatColor.GRAY + "Right click an unclaimed")
									&& lore.get(1).equals(ChatColor.GRAY + "horse to make it yours")) {
								
								event.setCancelled(true);
								player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
								
								claimHorse(horse, player, met.getDisplayName());
								
							}
						}
					}
					
					else if(!horse.isAdult() && main.getType().equals(Material.SHEARS)) {
					
						player.sendMessage(ChatColor.DARK_RED + "You can only neuter foals that you own the deed to!");
					
					}
				}
			}
		}
	}
	
	private void claimHorse(Horse horse, Player player, String horseName) {
		
		horse.setOwner(player);
		horse.getScoreboardTags().add("ho.isOwned");
		horse.setCustomName(horseName);
		
		horse.getWorld().playSound(horse.getLocation(), Sound.ENTITY_HORSE_ARMOR, 0.9f, 2.0f);
		player.sendMessage(ChatColor.GREEN + ("You are now the proud owner of " + horseName + "!"));
		
		player.getWorld().playSound(player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1.0f, 0.8f);
		
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
				&& event.getItem().getItemMeta().getDisplayName().contains(ChatColor.GREEN.toString() + ChatColor.ITALIC + "Deed to ")) {
			
			event.setUseItemInHand(Result.DENY);
			
		}
	}
}