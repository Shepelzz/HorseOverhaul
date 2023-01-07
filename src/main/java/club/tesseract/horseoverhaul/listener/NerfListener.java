package club.tesseract.horseoverhaul.listener;

import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkLoadEvent;

public class NerfListener implements Listener{
	
	
	public static double NERF_DIVISOR;
	public static boolean NERF_OVERRIDE;
	
	
	@EventHandler
	public void onSpawn(CreatureSpawnEvent event){
		
		if(event.getEntity() instanceof AbstractHorse) {
			
			if(event.getSpawnReason().equals(SpawnReason.NATURAL)) {
					
				nerf((AbstractHorse)event.getEntity());
			} else if(NERF_OVERRIDE) {
				PersistentAttribute.NERFED.setData(event.getEntity(), (byte)1);
			}
		}
	}
	
	
	@EventHandler
	public void onNewChunk(ChunkLoadEvent event) {
		
		if(event.isNewChunk()) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof AbstractHorse) {
					
					nerf((AbstractHorse)e);
				}
			}
		}
		else if(NERF_OVERRIDE) {
			
			for(Entity e : event.getChunk().getEntities()) {
				
				if(e instanceof AbstractHorse) {
					Byte data = PersistentAttribute.NERFED.getData(e);
					if(data == null || data == (byte) 0)
						nerf((AbstractHorse)e);
					
				}
			}
		}
	}
	
	public static void nerf(AbstractHorse horse) {
		
		if(NERF_OVERRIDE) {
			PersistentAttribute.NERFED.setData(horse, (byte)1);
		}
		
		horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue( horse.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() / NERF_DIVISOR);
		horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue( horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue() / NERF_DIVISOR);
		
		horse.setJumpStrength( horse.getJumpStrength() / NERF_DIVISOR);
		
	}
}
