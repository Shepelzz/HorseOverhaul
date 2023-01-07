package club.tesseract.horseoverhaul.listener;

import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

import club.tesseract.horseoverhaul.StatHorse;
import org.bukkit.inventory.ItemStack;

public class BreedingListener implements Listener{
	
	public static boolean BETTER_BREEDING_ENABLED, FOOD_EFFECTS;

	@EventHandler
	public void onBreed(EntityBreedEvent event) {
		
		if(event.getEntity() instanceof AbstractHorse && event.getBreeder() instanceof Player) {
			
			// ensures that the two parents are horses
			if(!( event.getFather() instanceof AbstractHorse && event.getMother() instanceof AbstractHorse )) return;
			
			Player player = (Player)event.getBreeder();
			
			// handle if either of the parents are neutered
			boolean isFatherNeutered = PersistentAttribute.NEUTERED.getData(event.getFather(), (byte) 0) == (byte) 1;
			boolean isMotherNeutered = PersistentAttribute.NEUTERED.getData(event.getMother(), (byte) 0) == (byte) 1;
			if( isFatherNeutered || isMotherNeutered ) {
				AbstractHorse father = (AbstractHorse)event.getFather();
				AbstractHorse mother = (AbstractHorse)event.getMother();

				String r = ChatColor.RESET + ChatColor.RED.toString();
				if ( isFatherNeutered && isMotherNeutered ) {
					player.sendMessage( r + "Both " + father.getName() + r + " and " + mother.getName() + r + " are neutered! The breed attempt fails");
				}
				else if( isFatherNeutered ) {
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
			
			// set the stats based off what breeding food was used
			StatHorse foal;
			ItemStack breedingFood = event.getBredWith();
			if(breedingFood != null && breedingFood.getType().equals(Material.ENCHANTED_GOLDEN_APPLE)) {
				foal = new StatHorse((AbstractHorse)event.getEntity(), (byte)2);
				PersistentAttribute.NEUTERED.setData(event.getEntity(), (byte)1);
			}

			else if(event.getBredWith().getType().equals(Material.GOLDEN_APPLE))
				foal = new StatHorse((AbstractHorse)event.getEntity(), (byte)1);
			else
				foal = new StatHorse((AbstractHorse)event.getEntity());
			
			
			foal.calculateBirth((AbstractHorse)event.getMother(),(AbstractHorse)event.getFather());
			foal.roach.setTamed(true);
			
		}
	}
}