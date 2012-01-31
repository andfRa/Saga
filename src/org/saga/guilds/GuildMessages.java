package org.saga.guilds;

import org.bukkit.ChatColor;
import org.saga.economy.EconomyMessages;
import org.saga.utility.TextUtil;



public class GuildMessages {

public static ChatColor positiveHighlightColor = ChatColor.GREEN;
	


	// Colors:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.

	public static ChatColor positive = ChatColor.GREEN;

	public static ChatColor negative = ChatColor.RED;

	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.

	public static ChatColor unavailable = ChatColor.DARK_GRAY;

	public static ChatColor anouncment = ChatColor.AQUA;

	public static ChatColor normal1 = ChatColor.GOLD;

	public static ChatColor normal2 = ChatColor.YELLOW;

	public static ChatColor frame = ChatColor.DARK_GREEN;


	public static String visitCollect(String playerName, SagaGuild guild) {
		
		return positive + "Visit " + TextUtil.capitalize(guild.getName()) + " guildhall to collect your payment.";
		
	}
	
	public static String guildPayed(SagaGuild guild, Double payed, Integer playerCount) {
		
		return positive + TextUtil.capitalize(guild.getName()) + " guild payed a total of " + EconomyMessages.coins(payed) + " to " + playerCount + " members.";
		
	}
	
	
}
