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
	byte food;
	
	public StatHorse(Horse horse) {
		roach = horse;
		food = 0;
	}
	public StatHorse(LivingEntity horse) {
		roach = (Horse)horse;
		food = 0;
	}
	public StatHorse(LivingEntity horse, byte f) {
		roach = (Horse)horse;
		food = f;
	}
	
	public double getJumpHeight() {
		double x = roach.getJumpStrength();
		
		return -0.1817584952 * Math.pow(x, 3) + 3.689713992 * Math.pow(x, 2) + 2.128599134 * x - 0.343930367;
	}
	public int getHealth() {
		return (int)(roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()/2);
	}
	public double getSpeed() {
		double x = roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		return 43.178 * x - 0.02141;
	}
	public String printStats(boolean border) {
		String msg = "";
		
		if(roach.getCustomName()!=null)
			msg += (ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + roach.getCustomName() + "'s Stats") + ChatColor.RESET + "\n \n";
		else
			msg += (ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + "Horse Stats") + ChatColor.RESET + "\n \n";
		
		msg += ChatColor.RED + "Health:\n" + printHearts(getHealth()) + " " + ChatColor.RED + Main.df.format(getHealth()) + "\n";
		msg += ChatColor.GREEN + "Speed:\n" + printSpeed(getSpeed()) + " " + ChatColor.GREEN + Main.df.format(getSpeed()) + "\n";
		msg += ChatColor.BLUE + "Jump Height:\n" + printJump(getJumpHeight()) + " " + ChatColor.BLUE + Main.df.format(getJumpHeight()) + "\n";
		
		
		
		if(border) {
			String bord = ChatColor.LIGHT_PURPLE + "-----------------------------------------------------";
			return bord + "\n" + msg + bord;
		}
		else
			return msg;
	}
	private String printJump(double jh) {
		String msg = "";
		double b = 0;
		String blocks = "";
		while(jh - b >= 0.2625) {
			blocks += "⬛";
			b += 0.525;
		}
		msg += ChatColor.DARK_BLUE + blocks;
		
		blocks = "";
		
		while(b < 5.25) {
			blocks += "⬛";
			b += 0.525;
		}
		msg += ChatColor.GRAY + blocks;
		return msg;
	}
	private String printSpeed(double sp) {
		String msg = "";
		int b = 0;
		String rate = "";
		while(sp - b >= 0.5) {
			rate += "⬤";
			b++;
		}
		msg += ChatColor.DARK_GREEN + rate;
		
		rate = "";
		
		while(b < 14.5125) {
			rate += "⬤";
			b++;
		}
		msg += ChatColor.GRAY + rate;
		return msg;
	}
	private String printHearts(int hp) {
		String msg = "";
		int s = 0;
		String hearts = "";
		while(s<hp) {
			hearts += "❤";
			s++;
		}
		msg += ChatColor.DARK_RED + hearts;
		
		hearts = "";
		
		while(s < 15) {
			hearts += "❤";
			s++;
		}	
		msg += ChatColor.GRAY + hearts;
		return msg;
	}
	public void calculateBirth(Horse mother, Horse father) {
		double fj, fh, fs, mj, mh, ms;
		
		fj = father.getJumpStrength();
		fh = father.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		fs = father.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		mj = mother.getJumpStrength();
		mh = mother.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		ms = mother.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		roach.setJumpStrength(calcJump(fj, mj));
		roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(calcHealth(fh, mh));
		roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(calcSpeed(fs, ms));
	}
	/**
	 * Horse Jump height can range from 0.4-1.0, average is 0.7
	 * TODO deal with golden apples
	 */
	private double calcJump(double f, double m) {
		if(food == 2) {
			return 1;
		}
		double x, bound, origin;
		
		if(f > m) {
			//bound = 1 - ((1 - f) / 3);
			if(f >= .8)
				bound = f + 0.075;
			else
				bound = f + 0.1;
			origin = m;
		}
		else {
			//bound = 1 - ((1 - m) / 3);
			if(m >= .8)
				bound = m + 0.075;
			else
				bound = m + 0.1;
			origin = f;
		}
		if(bound > 1)
			bound = 1;
		
		x = Math.random() * (bound - origin) + origin;

		return x;
	}
	
	/**
	 * Health can range from 15-30, average is 22-23
	 */
	private double calcHealth(double f, double m) {
		if(food == 2) {
			return 30;
		}
		 
		double x = (f + m) / 2;
		
		return x;
	}
	
	/**
	 * ranges from 0.1125 - 0.3375, average is 0.225
	 */
	private double calcSpeed(double f, double m) {
		if(food == 2) {
			return 0.3375;
		}
		
		double x = (f + m) / 2;
		
		return x;
	}
	
}
