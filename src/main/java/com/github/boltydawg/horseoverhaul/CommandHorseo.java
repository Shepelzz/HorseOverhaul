package com.github.boltydawg.horseoverhaul;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

/**
 * Handles the execution of the /horse command
 * @author Jason
 *
 */
public class CommandHorseo implements CommandExecutor {
	/**
	 * This method fires everytime someone uses the /horseo command.
	 * Expects only 1 argument: what they'd like more information about
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(!(sender instanceof Player)) return false;
		
		//checks if the player didn't enter any arguments following /horseo
		if(args == null || args.length != 1){
			return false;
		}
		
		//checks if the player entered a valid string after /horseo, and sends them the proper message
		else {
			
			Player player = ((Player) sender);
			String param = args[0];
			
			if(param.equalsIgnoreCase("breed")) {
				player.sendMessage(helpBreed());
			}
			else if(param.equalsIgnoreCase("own")) {
				player.sendMessage(helpOwn());
			}
			else if(param.equalsIgnoreCase("stats")) {
				player.sendMessage(helpStats());
			}
			else if(param.equalsIgnoreCase("pizza"))
				player.sendMessage("Your gamemode has been updated to Creative Mode");
			else {
				return false;
			}
				
			return true;
		}
			
	}
	
	private String helpBreed() {
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Breeding" + ChatColor.RESET + "\n\n";
		
		
		msg += ChatColor.GREEN + "The breeding algorithm has been reworked in a way such that there's more of a focus on generational improvements rather than random luck.\n" + ChatColor.RESET;
		msg += ChatColor.YELLOW + "You can get better results from your breeding by using better foods: golden apples will prevent the foal from having stats less than the lesser "
				+ "of the two parents; using enchanted golden apples will give you a foal with max stats, however, it will be sterile and unable to breed in the future.";
		
		return msg;
	}
	private String helpOwn() {
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Ownership" + ChatColor.RESET + '\n';
		
		msg += org.bukkit.ChatColor.GRAY;
		msg += ChatColor.GREEN + "Claim a horse as yours by right clicking it with a deed!\n" + ChatColor.RESET;
		msg += ChatColor.YELLOW + "Craft a deed by combining a golden carrot with a book and quill.\n" + ChatColor.RESET;
		msg += ChatColor.GREEN + "Other players cannot interact with horses that you own, and equipping your owned horses with armor prevents you from accidentally damaging them :)\n" + ChatColor.RESET;
		msg += ChatColor.YELLOW + "If you're in the business of selling horses, you can right click a foal that you own with shears to neuter it and prevent it from ever breeding.\n" + ChatColor.RESET;
		msg += ChatColor.GREEN + "If trying to use a nametag on one of your horses, you must be holding its deed in your off hand.\n" + ChatColor.RESET;
		if(Main.coloredNames)
			msg += ChatColor.YELLOW + "You can use Color Codes when naming your horses! See "+ ChatColor.UNDERLINE + "https://www.spigotmc.org/attachments/example2-png.188806/";
		
		return msg;
	}
	private String helpStats() {
		String msg = ChatColor.LIGHT_PURPLE + ChatColor.UNDERLINE.toString() + "HORSE OVERHAUL HELP: Checking Stats" + ChatColor.RESET + '\n';
		
		msg += org.bukkit.ChatColor.GRAY;
		msg += ChatColor.GREEN + "Right click a horse while holding a carrot on a stick to see its stats!\n" + ChatColor.RESET;
		msg += ChatColor.YELLOW + "After viewing a horse's stats, you can display them on a sign by right clicking the sign with your carrot on a stick.\n";
		
		return msg;
	}
}

