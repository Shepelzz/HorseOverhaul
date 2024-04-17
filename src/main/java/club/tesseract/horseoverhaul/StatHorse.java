package club.tesseract.horseoverhaul;

import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Horse;

import club.tesseract.horseoverhaul.listener.BreedingListener;


/**
 * @see <a href="https://minecraft.gamepedia.com/Attribute">Gamepedia Attributes</a>
 * @see <a href="https://minecraft.gamepedia.com/Horse">Gamepedia Horse</a>
 */
public class StatHorse{


	private static final String HEART = "❤";
	private static final String SPEED = "➤";
	private static final String JUMP = "⬛";

	/**
	 * fields
	 */
	
	public AbstractHorse roach;
	byte food;
	

	public StatHorse(AbstractHorse horse) {
		roach = horse;
		food = 0;
	}
	
	public StatHorse(AbstractHorse horse, byte f) {
		roach = horse;
		food = BreedingListener.FOOD_EFFECTS ? f : 0;
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


	public void calculateBirth(AbstractHorse mother, AbstractHorse father) {
		if(food == (byte)2) {
			roach.setJumpStrength(1.0);
			roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30);
			roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3375);
			return;
		}
		double fatherJump, fatherHealth, fatherSpeed, motherJump, motherHealth, motherSpeed;
		
		fatherJump = father.getJumpStrength();
		fatherHealth = father.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		fatherSpeed = father.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		motherJump = mother.getJumpStrength();
		motherHealth = mother.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		motherSpeed = mother.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue();
		
		roach.setJumpStrength(calcJump(fatherJump, motherJump));
		roach.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(calcHealth(fatherHealth, motherHealth));
		roach.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(calcSpeed(fatherSpeed, motherSpeed));
	}


	// Breeding calculations //

	/**
	 * @param fatherStat: mother's stat, on scale of 0-1.0
	 * @param motherStat: father's stat, on scale of 0-1.0
	 * @return double on scale of 0-1.0 that will determine the child's attribute
	 */
	private double randomizer(double fatherStat, double motherStat) {
		double min = Math.min(fatherStat, motherStat);
		double max = Math.max(fatherStat, motherStat);
		double nmax, nmin;
		
		double offset = Math.pow(max-min, 3) * 3.5;
		nmax = max + ( (1-max-offset) / 4 );
		nmin = min - ( (1-min) /5.5);
		
		double foal = Math.random() * (nmax-nmin) + nmin;
		
		if(food!=0 && foal<min) foal = min;
		
		return Math.min(foal, 1.0);
		
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
	public String printStats() {
		String msg = "";
		if(roach == null)return msg;

		String horseName = "кобылятина";
		if(roach.getCustomName()!=null) {
			horseName = roach.getCustomName();
		} else if (roach instanceof Horse) {
			horseName = "лошади";
		} else {
			horseName = roach.getType().name();
		}
			
		StringBuilder line = new StringBuilder(ChatColor.GRAY.toString());
		line.append("-".repeat(Math.max(0, 30)));

		msg+= line + "\n";
		msg += ChatColor.GOLD + "Характеристики: \"" + horseName + ChatColor.GOLD + "\"" + ChatColor.RESET + "\n";

		msg += ChatColor.YELLOW + "Здоровье: " + printHearts(getHealth()) + " " + ChatColor.WHITE + HorseOverhaul.statNumberFormat.format(getHealth()) + "хп\n";
		msg += ChatColor.YELLOW + "Скорость: " + printSpeed(getSpeed()) + " " + ChatColor.WHITE + HorseOverhaul.statNumberFormat.format(getSpeed()) + "м/с\n";
		msg += ChatColor.YELLOW + "Прыжок: " + printJump(getJumpHeight()) + " " + ChatColor.WHITE + HorseOverhaul.statNumberFormat.format(getJumpHeight()) + "м\n";

		msg += ChatColor.YELLOW + "Может размножаться: " + ChatColor.WHITE + (PersistentAttribute.NEUTERED.getData(roach, (byte)0) == (byte)1 ? "Нет" : "Да") + "\n";

		msg += ChatColor.YELLOW + "Владелец: " + ChatColor.WHITE +
			   (roach.getOwner() != null ? (roach.getOwner().getName() + " (" + PersistentAttribute.OWNER_COUNT.getData(roach, 1) + "-й)") : "Нет") + "\n";
		msg += ChatColor.YELLOW + "Документы: " + ChatColor.WHITE + (roach.getCustomName()!=null ? "Есть" : "Нет") + "\n";
		msg += line;
		return msg;
	}

	private String printJump(double jh) {
		String msg = "";
		double b = 0;
		StringBuilder blocks = new StringBuilder();

		// Display yellow squares up to a maximum of 5, only if jh is greater than or equal to 5
		if (jh >= 5) {
			while (b < 5) {
				blocks.append(JUMP);
				b += 0.525;
			}
			msg += ChatColor.GOLD + blocks.toString();
			blocks = new StringBuilder();
		} else {
			while (jh - b >= 0.2625) {
				blocks.append(JUMP);
				b += 0.525;
			}
			msg += ChatColor.WHITE + blocks.toString();
			blocks = new StringBuilder();
		}

		// Display gray squares for any additional squares beyond the first 5
		while (b < 5.25) {
			blocks.append(JUMP);
			b += 0.525;
		}
		msg += ChatColor.GRAY + blocks.toString();
		return msg;
	}

	private String printSpeed(double sp) {
		String msg = "";
		int b = 0;
		StringBuilder rate = new StringBuilder();
		// Display yellow circles up to a maximum of 14, only if sp is greater than or equal to 14
		if (sp >= 14) {
			while (b < 14) {
				rate.append(SPEED);
				b++;
			}
			msg += ChatColor.GOLD + rate.toString();
			rate = new StringBuilder();
		} else {
			while (sp - b >= 0.5) {
				rate.append(SPEED);
				b++;
			}
			msg += ChatColor.WHITE + rate.toString();
			rate = new StringBuilder();
		}
		// Display gray circles for any additional circles beyond the first 14
		while (b < 14) {
			rate.append(SPEED);
			b++;
		}
		msg += ChatColor.GRAY + rate.toString();
		return msg;
	}
	
	private String printHearts(int hp) {
		String msg = "";
		int s = 0;
		StringBuilder hearts = new StringBuilder();

		// Display white hearts up to a maximum of 15, only if hp is greater than 15
		if (hp > 15) {
			while (s < 15) {
				hearts.append(HEART);
				s++;
			}
			msg += ChatColor.GOLD + hearts.toString();
			hearts = new StringBuilder();
		} else {
			while (s < hp) {
				hearts.append(HEART);
				s++;
			}
			msg += ChatColor.WHITE + hearts.toString();
			hearts = new StringBuilder();
		}

		// Display gray hearts for any additional hearts beyond the first 15
		while (s < 15) {
			hearts.append(HEART);
			s++;
		}
		msg += ChatColor.GRAY + hearts.toString();
		return msg;
	}
}
