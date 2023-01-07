package club.tesseract.horseoverhaul;

import club.tesseract.horseoverhaul.listener.OwnershipListener;
import club.tesseract.horseoverhaul.utils.ComponentUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NamePrompt extends StringPrompt {
	
	private final Player player;
	private final AbstractHorse abHorse;

	public NamePrompt(Player player, AbstractHorse horse) {
		this.player = player;
		this.abHorse = horse;
	}

	@Override
	public @NotNull String getPromptText(ConversationContext context) {

		Conversable recipient = context.getForWhom();
		if(OwnershipListener.COLOURED_NAMES) {
			if(recipient instanceof Player){
				ComponentUtils.sendConfigMessage((Player) recipient, "horse.name.question");
			}else{
				recipient.sendRawMessage(ComponentUtils.componentFormattedConfigString("horse.name.question"));
			}
		}else{
			if(recipient instanceof Player){
				ComponentUtils.sendConfigMessage((Player) recipient, "horse.name.question-no-colour");
			}else{
				recipient.sendRawMessage(ComponentUtils.componentFormattedConfigString("horse.name.question-no-colour"));
			}
		}
		
		return ComponentUtils.componentFormattedConfigString("horse.name.prompt");
	}

	@Override
	public Prompt acceptInput(@NotNull ConversationContext context, String input) {
		
		final String name;
		
		if(OwnershipListener.COLOURED_NAMES) name = ChatColor.translateAlternateColorCodes('&', input);
		else name = input;

		
		if(ChatColor.stripColor(name).length() > 16) {
			Conversable recipient = context.getForWhom();
			if(recipient instanceof Player)ComponentUtils.sendConfigMessage(((Player) recipient), "horse.name.too-long");
			else context.getForWhom().sendRawMessage(ComponentUtils.componentFormattedConfigString("horse.name.too-long"));
			return new NamePrompt(player,abHorse);
			
		}
		
		OwnershipListener.claimHorse(abHorse, player, name);
		
		return StringPrompt.END_OF_CONVERSATION;
		
	}
}