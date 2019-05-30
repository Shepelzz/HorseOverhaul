package com.github.boltydawg.horseoverhaul;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

public class CommandClearOwners implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!(sender instanceof Player)) return false;
		Player player = (Player)sender;
		for(LivingEntity e : player.getWorld().getLivingEntities()) {
			if(e instanceof Horse) {
				Horse horse = (Horse)e;
				if(horse.getOwner()!=null) {
					horse.setOwner(null);
					horse.setTamed(true);
					player.sendMessage("" + horse.getCustomName());
				}
			}
		}
		return true;
	}

}
