package com.github.boltydawg.horseoverhaul;

import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;

/**
 * @see https://minecraft.gamepedia.com/Attribute
 * @see https://minecraft.gamepedia.com/Horse
 */
public class StatHorse{
	Horse roach;
	
	public StatHorse(Horse horse) {
		roach = horse;
	}
	public StatHorse(LivingEntity horse) {
		roach = (Horse)horse;
	}
	
	public String getJumpHeight() {
		double x = roach.getJumpStrength();
		x = -0.1817584952 * Math.pow(x, 3) + 3.689713992 * Math.pow(x, 2) + 2.128599134 * x - 0.343930367;
		return Main.df.format(x);
	}
	public String getHealth() {
		return (int)(roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()/2) + "";
	}
	public String getSpeed() {
		double x = roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		x = 43.178 * x - 0.02141;
		return Main.df.format(x);
	}
	public String printStats(String name) {
		String msg = name+":\n";
		msg += ChatColor.GREEN + ("S: " + this.getSpeed() + " m/s\n");
		msg += ChatColor.RED + ("H: " + this.getHealth() + " hearts\n");
		msg += ChatColor.BLUE + ("J: " + this.getJumpHeight() + " blocks");
		return msg;
	}
	public void calculateBirth(Horse mother, Horse father) {
		double fJump = father.getJumpStrength();
		double fHealth = father.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		double fSpeed = father.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		double mJump = mother.getJumpStrength();
		double mHealth = mother.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		double mSpeed = mother.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		roach.setJumpStrength((fJump + mJump) / 2);
		roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue((mHealth + fHealth) / 2);
		roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue((mSpeed + fSpeed) / 2);
	}
}
