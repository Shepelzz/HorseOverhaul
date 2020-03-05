package com.github.boltydawg.horseoverhaul;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;

import com.github.boltydawg.horseoverhaul.Listeners.OwnershipListener;

public class NamePrompt extends StringPrompt {
	
	private Player player;
	private Horse horse;

	public NamePrompt(Player player, Horse horse) {
		this.player = player;
		this.horse = horse;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		
		return ChatColor.BLUE + "What would you like to name your new horse?" + ChatColor.RESET + '\n' + ChatColor.GRAY + "(type your answer in chat)";
		
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {
		
		String name = input;
		
		if(OwnershipListener.coloredNames) {
			
			while(name.contains("&")) {
				
				int ind = name.indexOf('&');
				
				if(ChatColor.getByChar( name.charAt(ind+1) ) != null ) {
					
					name = name.substring(0, ind) + ChatColor.getByChar( name.charAt(ind+1) ) + name.substring(ind+2);

				}
			}
		}
		
		if(ChatColor.stripColor(name).length() > 16) {
			
			context.getForWhom().sendRawMessage(ChatColor.RED + "Name too long! Must be at most 16 characters");
			return new NamePrompt(player,horse);
			
		}
		
		OwnershipListener.claimHorse(horse, player, name);
		
		return StringPrompt.END_OF_CONVERSATION;
		
	}
}