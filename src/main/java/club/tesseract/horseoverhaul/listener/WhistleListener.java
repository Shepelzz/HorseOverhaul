package club.tesseract.horseoverhaul.listener;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import club.tesseract.horseoverhaul.item.Item;
import club.tesseract.horseoverhaul.utils.ComponentUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

import static club.tesseract.horseoverhaul.utils.Utils.toTitleCase;

public class WhistleListener implements Listener {
	
	private static final HashMap<UUID,Integer> WHISTLE_BLOWERS = new HashMap<>();

	public static boolean WHISTLE_ENABLED, WHISTLE_TP;
	public static int WHISTLE_COOLDOWN;
	
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getHand() != EquipmentSlot.HAND) return;

		if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			ItemStack item = event.getItem();
			if(item == null)return;
			
			//player right clicks while holding a whistle
			if(!Item.WHISTLE.isEqual(item)) return;
				
			Player player = event.getPlayer();

			// Check that the horse's uuid is stored in the item, this also validates that the item is a whistle
			UUID horseId = PersistentAttribute.WHISTLE_HORSE_ID.getData(item);
			if(horseId == null) return;

			event.setCancelled(true);
			event.setUseInteractedBlock(Result.ALLOW);

			//checks if the player is on cool-down
			if(WHISTLE_BLOWERS.containsKey(player.getUniqueId()) ) {
				ComponentUtils.sendConfigActionBar(player, "whistle.on-cooldown", Placeholder.parsed("time", WHISTLE_BLOWERS.get(player.getUniqueId()).toString()));
				return;
			}

			//play a whistle sound (or several, to be precise)
			new BukkitRunnable() {

				private int plays = 0;

				@Override
				public void run() {
					player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, SoundCategory.NEUTRAL, 2.0f, 1.15f);
					plays++;
					if(plays > 6)this.cancel();
				}
			}.runTaskTimer(HorseOverhaul.getInstance(), 0L, 2L);


			boolean found = false;	//boolean for whether the horse is detected

			for(Entity entity : player.getNearbyEntities(250, 30, 250)) { //search a 100x30x100 radius
				if(!(entity instanceof AbstractHorse)) continue;

				if(entity.getUniqueId().equals(horseId)) {
					found = true;

					if( WhistleListener.WHISTLE_TP) {
						entity.teleport(player);
					}else {
						((LivingEntity)entity).addPotionEffect( new PotionEffect( PotionEffectType.GLOWING, 200, 1, false, false ) );
					}

					entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_HORSE_ANGRY, 1.0f, 1.0f);
					break;

				}


			}

			if(found) {
				ComponentUtils.sendConfigActionBar(player, "whistle.locate-successful");
			}else {
				ComponentUtils.sendConfigActionBar(player, "whistle.locate-unsuccessful");
			}

			//put the player on cool-down, as to not overload the server
			WHISTLE_BLOWERS.put(player.getUniqueId(), WHISTLE_COOLDOWN);

			new BukkitRunnable() {

				@Override
				public void run() {

					int time = WHISTLE_BLOWERS.get(player.getUniqueId());

					if(time <= 1) {
						WHISTLE_BLOWERS.remove(player.getUniqueId());
						this.cancel();
					}
					else {
						WHISTLE_BLOWERS.put(player.getUniqueId(), time - 1);
					}
				}
			}.runTaskTimer(HorseOverhaul.getInstance(), 20L, 20L);
		}
	}

	/**
	 * Handles when a player right-clicks a horse with a blank whistle.
	 * Runs after the ownership's event handler, that way if the player clicked
	 * 	a horse that they don't own, this will already be canceled
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onLinkWhistle (PlayerInteractEntityEvent event) {
		if(!event.isCancelled() && event.getRightClicked() instanceof AbstractHorse) {
			
			AbstractHorse abHorse = (AbstractHorse)event.getRightClicked();
			if(abHorse.isTamed()) {
				
				Player player = event.getPlayer();
				
				ItemStack item;
				if(event.getHand().equals(EquipmentSlot.HAND)) {
					item = player.getInventory().getItemInMainHand();
				}
				else {
					item = player.getInventory().getItemInOffHand();
				}

				if(!Item.WHISTLE.isEqual(item)) return;
					
				if(! (event.getRightClicked() instanceof Horse) ) {
					ComponentUtils.sendConfigMessage(player, "whistle.not-horse");
					event.setCancelled(true);
					return;
				}

				Horse horse = (Horse)event.getRightClicked();

				ItemMeta meta = item.getItemMeta();
				if(meta == null)return;

				PersistentAttribute.WHISTLE_HORSE_ID.setData(meta, horse.getUniqueId());

				String colour = toTitleCase(horse.getColor().name().replaceAll("_", " "));
				String type = toTitleCase(horse.getStyle().name().replaceAll("_", " "));
				final String whistleName;
				if(horse.getCustomName() == null) {
					whistleName = ComponentUtils.componentFormattedConfigString("whistle.no-name-item",
							Placeholder.parsed("horse_colour", colour),
							Placeholder.parsed("horse_color", colour),
							Placeholder.parsed("horse_type", type)
							);
				}else {
					whistleName = ComponentUtils.componentFormattedConfigString("whistle.name-item",
							Placeholder.parsed("horse_colour", colour),
							Placeholder.parsed("horse_color", colour),
							Placeholder.parsed("horse_type", type),
							Placeholder.parsed("horse", horse.getCustomName())
					);
				}
				meta.setDisplayName(whistleName);


				if(item.getAmount() == 1) {
					item.setItemMeta(meta);
				}else {
					item.setAmount(item.getAmount() - 1);
					ItemStack t = new ItemStack(Material.IRON_NUGGET);
					t.setItemMeta(meta);
					player.getInventory().addItem(t);
				}

				ComponentUtils.sendConfigMessage(player, "whistle.linked");
				player.getWorld().playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0f, 2.0f);
				event.setCancelled(true);
			}
			
		}
	}
}