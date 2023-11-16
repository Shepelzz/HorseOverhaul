package club.tesseract.horseoverhaul.command;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.config.ConfigManager;
import club.tesseract.horseoverhaul.utils.ComponentUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the execution of the /horse command
 * @author Jason
 * @author TropicalShadow
 */
public class CommandHorseo implements TabExecutor {
	/**
	 * This method fires everytime someone uses the /horseoverhaul command.
	 * Expects only 1 argument: either what they'd like more information about, or an admin using "reload"
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		
		//checks if the player didn't enter any arguments following /horseoverhaul
		if(args == null || args.length < 1)return false;

		String param = args[0];

		switch(param.toLowerCase()){
			case "breeding":
				ComponentUtils.sendConfigMessage(sender, "command.help.breed");
				break;
			case "whistle":
				ComponentUtils.sendConfigMessage(sender, "command.help.whistle");
				break;
			case "stats":
				ComponentUtils.sendConfigMessage(sender, "command.help.stats");
				break;
			case "ownership":
				ComponentUtils.sendConfigMessage(sender, "command.help.ownership");
				break;
			case "reload":
				if(sender.hasPermission("horseo.reload")){
					reloadCommand(sender);
				}else{
					ComponentUtils.sendConfigMessage(sender, "command.no-permission");
				}
				break;
			default:
				return false;
		}
		return true;
	}


	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		ArrayList<String> result = new ArrayList<>();
		if(args == null || args.length < 1)return result;
		if(args.length == 1){
			if("breeding".startsWith(args[0].toLowerCase()))result.add("breeding");
			if("whistle".startsWith(args[0].toLowerCase()))result.add("whistle");
			if("stats".startsWith(args[0].toLowerCase()))result.add("stats");
			if("ownership".startsWith(args[0].toLowerCase()))result.add("ownership");
			if("reload".startsWith(args[0].toLowerCase()) && sender.hasPermission("horseo.reload"))result.add("reload");
		}
		return result;
	}


	private void reloadCommand(CommandSender sender){
		sender.sendMessage("Reloading HorseOverhaul...");

		//un-initialize any existing listeners
		HorseOverhaul.getInstance().removeListeners();

		//reload the config file
		ConfigManager.get().loadConfigs();

		sender.sendMessage("Done.");
	}

}