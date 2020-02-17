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
	
	/**
	 * fields
	 */
	
	Horse roach;
	byte food;
	
	
	/**
	 * constructors
	 */
	
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
		if(Main.foodEffects)
			food = f;
		else
			food = 0;
	}
	
	
	/**
	 * get methods
	 */
	
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
	
	
	public void calculateBirth(Horse mother, Horse father) {
		if(food == (byte)2) {
			roach.setJumpStrength(1.0);
			roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
			roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3375);
		}
		double fj, fh, fs, mj, mh, ms;
		double boost = 1.0;
		
		if(food == (byte)1)
			boost = 1.10;
		
		fj = father.getJumpStrength();
		fh = father.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		fs = father.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		mj = mother.getJumpStrength();
		mh = mother.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		ms = mother.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		roach.setJumpStrength(boost * calcJump(fj, mj));
		roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(boost*calcHealth(fh, mh));
		roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(boost*calcSpeed(fs, ms));
	}
	
	
	/**
	 * Breeding calculations
	 */
	
	/**
	 * @param f: mother's stat, on scale of 0-1.0
	 * @param m: father's stat, on scale of 0-1.0
	 * @return double on scale of 0-1.0 that will determine the child's attribute
	 */
	private double randomizer(double f, double m) {
		if(food == 2)
			return 1.0;
		double min = f < m ? f : m;
		double max = f > m ? f : m;
		
		if(food == 0)
			min = min * (Math.random() * 0.3 + 0.9);
		
		if(max > .8)
			max += (1-max)/1.5;
		else
			max = max * (Math.random() * 0.25 + 0.9);
		
		return Math.random() * (max-min) + min;
	}
	
	/**
	 * 	Horse Jump height can range from 0.4-1.0, average is 0.7
	 */
	private double calcJump(double f, double m) {
		
		double mc = (m - .4)  / 0.6;
		double fc = (f - .4) / 0.6;
		
		double child = randomizer(mc, fc);
		return child * 0.6 + 0.4;
	}
	
	/**
	 * Health can range from 15-30, average is 22-23
	 */
	private double calcHealth(double f, double m) {
		 
		double mc = (m - 15) / 15;
		double fc = (f - 15) / 15;
		
		double child = randomizer(mc, fc);
		return child * 15 + 15;
	}
	
	/**
	 * Speed ranges from 0.1125 - 0.3375, average is 0.225
	 */
	private double calcSpeed(double f, double m) {
		
		double mc = (m - 0.1125) / 0.225;
		double fc = (f - 0.1125) / 0.225;
		
		double child = randomizer(mc, fc);
		return child * 0.225 + 0.1125;
	}
	
	
	/**
	 * Methods for printing/display a horse's stats
	 */
	
	public String printStats(boolean border) {
		String msg = "";
		
		if(roach.getCustomName()!=null)
			msg += (ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + roach.getCustomName() + "'s Stats") + ChatColor.RESET + "\n \n";
		else {
			String color = this.roach.getColor().name();
			color = color.toCharArray()[0] + color.substring(1).toLowerCase();
			msg += (ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + color + " Horse's Stats") + ChatColor.RESET + "\n \n";
		}
			
		
		msg += ChatColor.RED + "Health:\n" + printHearts(getHealth()) + " " + ChatColor.RED + Main.df.format(getHealth()) + "\n";
		msg += ChatColor.GREEN + "Speed:\n" + printSpeed(getSpeed()) + " " + ChatColor.GREEN + Main.df.format(getSpeed()) + "\n";
		msg += ChatColor.BLUE + "Jump Height:\n" + printJump(getJumpHeight()) + " " + ChatColor.BLUE + Main.df.format(getJumpHeight()) + "\n";
		
		
		if(border) {
			String bord = ChatColor.LIGHT_PURPLE + "-----------------------------------------------------";
			return bord + "\n" + msg + bord;
		}
		else
			return msg + ChatColor.DARK_GRAY + "Can Breed:\n" + (roach.getScoreboardTags().contains("ho.isNeutered") ? ChatColor.GRAY + "False" : ChatColor.GRAY + "True") + "\n";
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
	
}