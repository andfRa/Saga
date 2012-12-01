package org.saga.messages;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.attributes.Attribute;
import org.saga.attributes.AttributeParameter;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.PermissionsDependency;
import org.saga.factions.Faction;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.text.TextUtil;
import org.sk89q.Command;
import org.sk89q.CommandPermissions;

public class AdminMessages {

	
public static ChatColor veryPositive = ChatColor.DARK_GREEN;
	

	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED;
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;

	

	// Player levels:
	public static String playerExpSet(Integer exp, SagaPlayer sagaPlayer){
		return positive + "Player " + sagaPlayer.getName() + " exp set to " + exp + ".";
	}
	
	public static String playerExpSet(Integer exp){
		return positive + "Exp was set to " + exp + ".";
	}

	public static String playerExpOutOfRange(String exp){
		return negative + "Exp " + exp + " is out of range. Allowed range: 0 - " + ExperienceConfiguration.config().getMaxExp() + ".";
	}
	
	
	// Settlement and faction levels:
	public static String settleClaimsOutOfRange(String level){
		return negative + "Claims " + level + " is out of range. Allowed range: 0 - " + SettlementConfiguration.config().getMaxClaims() + ".";
	}
	
	public static String factionClaimsOutOfRange(String claims){
		return negative + "Claims " + claims + " is out of range. Allowed range: 0 - " + FactionConfiguration.config().getMaxClaims() + ".";
	}

	public static String setClaims(Settlement settlement){
		
		return positive + "Settlement " + settlement.getName() + " claims set to " + settlement.getTotalClaims() + ".";
		
	}
	
	public static String setClaims(Faction faction){
		
		return positive + "Faction " + faction.getName() + " claims set to " +faction.getTotalClaims() + ".";
		
	}
	
	
	// Healing:
	public static String healed(){
		return positive + "You got healed" + ".";
	}
	
	public static String healed(SagaPlayer selPlayer){
		return positive + "Healed " + selPlayer.getName() + ".";
	}
	
	
	
	// Attributes:
	public static String attributeSet(String attribute, Integer score){
		return positive + TextUtil.capitalize(attribute) + " was set to " + score + ".";
	}
	
	public static String attributeSet(String attribute, Integer score, SagaPlayer sagaPlayer){
		return positive + "Players " + sagaPlayer.getName() + " " + attribute + " was set to " + score + ".";
	}
	
	public static String attributeInvalid(String attribute, SagaPlayer sagaPlayer){
		return negative + TextUtil.capitalize(attribute) + " isn't a valid attribute.";
	}
	
	public static String attributeOutOfRange(String score){
		return negative + "Ability score " + score + " is out of range. Allowed range: 0 - " + AttributeConfiguration.config().maxAttributeScore + ".";
	}
	
	
	
	// Administrator mode:
	public static String adminMode(SagaPlayer sagaPlayer) {
		
		
		if(sagaPlayer.isAdminMode()){
			return positive + "Admin mode enabled.";
		}else{
			return positive + "Admin mode disabled.";
		}
		
		
	}
	
	public static String adminModeAlreadyEnabled() {
		
		return negative + "Admin mode already enabled.";
		
	}
	
	public static String adminModeAlreadyDisabled() {
		
		return negative + "Admin mode already disabled.";
		
	}

	
	
	// Additional info:
	public static String statsTargetName(SagaPlayer sagaPlayer) {
		return positive + "Stats for " + sagaPlayer.getName() + ".";
	}
	
	
	
	// Guardian rune:
	public static String recharged(GuardianRune rune, SagaPlayer sagaPlayer) {
		
		return positive + "Recharged players " + sagaPlayer.getName() + " guardian rune recharged.";
		
	}
	
	public static String recharged(GuardianRune rune) {
		
		return positive + "Recharged guardian rune.";
		
	}
	
	

	// Saving loading:
	public static String saving() {
		return veryPositive + "Saving Saga information.";
	}
	
	public static String saved() {
		return veryPositive + "Saving complete.";
	}
	
	
	
	// Messages:
	public static String chatMessage(String name, String message) {

		ChatColor nameColor = GeneralConfiguration.config().adminChatNameColor;
		ChatColor messageColor = GeneralConfiguration.config().adminChatMessageColor;
		String namedMessage = messageColor + "{" + nameColor + name + messageColor + "} " + message;
		
		return namedMessage;
		
	}
	
	public static void chatWarning(String message) {

		chatMessage("WARNING", message);
		
	}

	
	
	// Time:
	public static String nextDaytime(World world, Daytime daytime) {

		return positive + "Daytime set to " + daytime + " for world " + world.getName() + ".";
		
	}

	
	
	// Wiki:
	public static String wikiCommands(ArrayList<Method> commandMethods) {
		
		
		StringBuffer result = new StringBuffer();
		ArrayList<String> categories = new ArrayList<String>(){
			
			private static final long serialVersionUID = 1L;

			{
				add("saga.user.help");
				add("saga.user.player");
				add("saga.user.settlement");
				add("saga.user.building");
				add("saga.user.faction");
				add("saga.admin");
				add("saga.special");
				add("saga.statistics");
			}
			
		};
		
		// Sort commands:
		Comparator<Method> comparator = new Comparator<Method>() {
			
			@Override
			public int compare(Method o1, Method o2) {

				CommandPermissions perm1 = o1.getAnnotation(CommandPermissions.class);
				CommandPermissions perm2 = o2.getAnnotation(CommandPermissions.class);
				if(perm1 == null || perm2 == null || perm1.value().length == 0 || perm2.value().length == 0) return o1.getName().compareToIgnoreCase(o2.getName());
				
				return perm1.value()[0].compareToIgnoreCase(perm2.value()[0]);
				
			}
			
		};
		
		Collections.sort(commandMethods, comparator);
		
		// Categories:
		for (String category : categories) {
			
			if(result.length() > 0) result.append("\n\n");
			
			// Begin:
			result.append("==" + TextUtil.capitalize(getCategoryName(category)) + " commands" + "==" + "\n");
			result.append("{| width=\"90%\" class=\"wikitable\"\n");
			result.append("|-");
			result.append("\n");
			result.append("!Command");
			result.append("\n");
			result.append("!Parameters");
			result.append("\n");
			result.append("!Description");
//			result.append("\n");
//			result.append("!Permission");		
			
			// Commands:
			for (Method method : commandMethods) {
				
				Command command = method.getAnnotation(Command.class);
				if(command == null) continue;
				
				CommandPermissions permissions = method.getAnnotation(CommandPermissions.class);
				String permission = "";
				if(permissions != null && permissions.value().length != 0) permission = permissions.value()[0];
				
				if(!permission.startsWith(category)) continue;
				
				String flags = "";
				if(command.flags().length() > 0) flags = "[-" + command.flags().replace(" ", "] [-") + "] ";
				
				result.append("\n");
				result.append("|-");
				result.append("\n");
				result.append("|" + command.aliases()[0]);
				result.append("\n");
				result.append("|" + "<nowiki>" + flags + command.usage() + "</nowiki>");
				result.append("\n");
				result.append("|" + command.desc());
				
//				if(permission.length() > 0){
//					result.append("\n");
//					result.append("|" + permission);
//				}
				
				
			}
			
			result.append("\n");
			
			// End:
			result.append("|}");
			
		}
		
		return result.toString();
		
		
	}
	
	public static String wikiPermissions(ArrayList<Method> commandMethods) {
		
		
		StringBuffer result = new StringBuffer();
		ArrayList<String> categories = new ArrayList<String>(){
			
			private static final long serialVersionUID = 1L;

			{
				add("saga.user.help");
				add("saga.user.player");
				add("saga.user.settlement");
				add("saga.user.building");
				add("saga.user.faction");
				add("saga.admin");
				add("saga.special");
				add("saga.statistics");
			}
			
		};
		
		// Sort commands:
		Comparator<Method> comparator = new Comparator<Method>() {
			
			@Override
			public int compare(Method o1, Method o2) {

				CommandPermissions perm1 = o1.getAnnotation(CommandPermissions.class);
				CommandPermissions perm2 = o2.getAnnotation(CommandPermissions.class);
				if(perm1 == null || perm2 == null || perm1.value().length == 0 || perm2.value().length == 0) return o1.getName().compareToIgnoreCase(o2.getName());
				
				return perm1.value()[0].compareToIgnoreCase(perm2.value()[0]);
				
			}
			
		};
		
		Collections.sort(commandMethods, comparator);
		

		// Begin:
		result.append("==" + "Command permissions" + "==" + "\n");
		result.append("{| width=\"70%\" class=\"wikitable\"\n");
		result.append("|-");
		result.append("\n");
		result.append("!Permission");
		result.append("\n");
		result.append("!Command");		
		
		// Commands:
		for (Method method : commandMethods) {
			
			Command command = method.getAnnotation(Command.class);
			if(command == null) continue;
			
			CommandPermissions permissions = method.getAnnotation(CommandPermissions.class);
			String permission = "";
			if(permissions != null && permissions.value().length != 0) permission = permissions.value()[0];
			
			boolean stop = true;
			for (String category : categories) {
				
				if(permission.startsWith(category)){
					stop = false;
					break;
				}
				
			}
			if(stop) continue;
			
			result.append("\n");
			result.append("|-");
			result.append("\n");
			result.append("|" + permission);			
			result.append("\n");
			result.append("|" + command.aliases()[0]);
			
		}
		result.append("\n");
		
		// End:
		result.append("|}");
		
		result.append("\n");
		
		// Other permissions:
		result.append("==" + "Other permissions" + "==" + "\n");
		result.append("{| width=\"70%\" class=\"wikitable\"\n");
		result.append("|-");
		result.append("\n");
		result.append("!Permission");
		result.append("\n");
		result.append("!Effect");		
		
		ArrayList<Entry<String, String>> descriptions = new ArrayList<Entry<String,String>>(PermissionsDependency.PERMISSION_DESCRIPTIONS.entrySet());
		
		// Sort:
		Comparator<Entry<String, String>> descComparator = new Comparator<Entry<String, String>>() {
			
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {

				return o1.getKey().compareToIgnoreCase(o2.getKey());
				
			}
			
		};
		Collections.sort(descriptions, descComparator);
		
		for (Entry<String, String> entry : descriptions) {
			
			result.append("\n");
			result.append("|-");
			result.append("\n");
			result.append("|" + entry.getKey());			
			result.append("\n");
			result.append("|" + entry.getValue());
			
		}
		result.append("\n");
		
		// End:
		result.append("|}");
		
		return result.toString();
		
		
	}
	
	private static String getCategoryName(String fullCateg) {

		String[] nodes = fullCateg.split("\\.");
		
		return nodes[nodes.length-1];
		
	}

	public static String wikiAttributes(ArrayList<Method> commandMethods) {
		
		
		StringBuffer result = new StringBuffer();
		ArrayList<Attribute> attributes = AttributeConfiguration.config().getAttributes();
		int max = AttributeConfiguration.config().maxAttributeScore;
		
		// Begin:
		result.append("==" + "Attributes" + "==" + "\n");
		result.append("{| width=\"100%\" class=\"wikitable\"");
//		result.append("|-");
//		result.append("\n");
//		result.append("!Attribute");
		
		// Column names:
		result.append("\n");
		result.append("|-");
		result.append("\n");
		
		result.append("! rowspan=\"" + 2 + "\" | Attribute ");
		result.append("\n");
		result.append("! rowspan=\"" + 2 + "\" | Parameter ");
		result.append("\n");
		result.append("! colspan=\"" + (max/5) + "\" | Score ");
		
		result.append("\n");
		result.append("|-");
		result.append("\n");
		
		result.append("| || ");
		for (int i = 5; i <= max; i+=5) {
			result.append(" || ");
			result.append("'''" + i + "'''");
		}
		
		for (Attribute attribute : attributes) {
			
			ArrayList<Entry<AttributeParameter, TwoPointFunction>> parameters = attribute.getAllEntries();
			
			result.append("\n");
			result.append("|-");
			result.append("\n");
			result.append("! rowspan=\"" + parameters.size() + "\" | " + TextUtil.capitalize(attribute.getName()));
			
			boolean first = true;
			for (Entry<AttributeParameter, TwoPointFunction> entry : parameters) {
				
				result.append("\n");
				
				// Next row:
				if(!first){
					result.append("|-");
					result.append("\n");
				}
				first = false;
				
				// Parameter:
				AttributeParameter parameter = entry.getKey();
				TwoPointFunction function = entry.getValue();
				
				// Key:
				result.append("| " + parameterKey(parameter));
				
				// Values:
				for (int i = 5; i <= max; i+=5) {
					result.append(" || ");
					result.append(parameterValue(parameter, function.value(i)));
				}
				
			}
			
		}
		result.append("\n");
		
		// End:
		result.append("|}");
		
		
		return result.toString();
		
	}
	
	private static String parameterKey(AttributeParameter parameter) {

		
		String key = parameter.name().toLowerCase().replace("_", " ");
		
		// Damage modifiers:
		if(parameter == AttributeParameter.MELEE_MODIFIER || parameter == AttributeParameter.RANGED_MODIFIER || parameter == AttributeParameter.MAGIC_MODIFIER){
			
			return key.replace(" modifier", " damage");
				
		}
		
		// Damage multipliers:
		if(parameter == AttributeParameter.MELEE_MULTIPLIER || parameter == AttributeParameter.RANGED_MULTIPLIER || parameter == AttributeParameter.MAGIC_MULTIPLIER){
			
			return key.replace(" multiplier", " damage");
				
		}
		
		// Hit chance:
		if(parameter == AttributeParameter.MELEE_HIT_CHANCE || parameter == AttributeParameter.RANGED_HIT_CHANCE || parameter == AttributeParameter.MAGIC_HIT_CHANCE){
			
			return key.replace(" hit chance", " dodge");
				
		}

		// Amour penetration:
		if(parameter == AttributeParameter.MELEE_ARMOUR_PENETRATION || parameter == AttributeParameter.RANGED_ARMOUR_PENETRATION || parameter == AttributeParameter.MAGIC_ARMOUR_PENETRATION){
			
			return key.replace(" armour penetration", " penetration");
				
		}
		
		// Burn resist:
		if(parameter == AttributeParameter.BURN_RESIST){
			
			return key.replace(" resist chance", " resist");
			
		}

		// Drops:
		if(parameter == AttributeParameter.DROP_MODIFIER){
			
			return key.replace("drop modifier", "bonus drop chance");
			
		}
		
		return key;
		
				
	}
	
	private static String parameterValue(AttributeParameter parameter, Double value) {

		
		// Damage modifiers:
		if(parameter == AttributeParameter.MELEE_MODIFIER || parameter == AttributeParameter.RANGED_MODIFIER || parameter == AttributeParameter.MAGIC_MODIFIER){
			
			if(value < 0) return "" + TextUtil.round(value, 1);
			return "+" + TextUtil.round(value, 1);
				
		}
		
		// Damage multipliers:
		if(parameter == AttributeParameter.MELEE_MULTIPLIER || parameter == AttributeParameter.RANGED_MULTIPLIER || parameter == AttributeParameter.MAGIC_MULTIPLIER){
			
			if(value < 1) return "-" + TextUtil.round((1-value)*100, 0) + "%";
			return "+" + TextUtil.round((value-1)*100, 0) + "%";
				
		}
		
		// Hit chance:
		if(parameter == AttributeParameter.MELEE_HIT_CHANCE || parameter == AttributeParameter.RANGED_HIT_CHANCE || parameter == AttributeParameter.MAGIC_HIT_CHANCE){
			
			value*= -1;
			
			if(value < 0) return "" + TextUtil.round(value*100, 0) + "%";
			return "" + TextUtil.round((value)*100, 0) + "%";
				
		}

		// Amour penetration:
		if(parameter == AttributeParameter.MELEE_ARMOUR_PENETRATION || parameter == AttributeParameter.RANGED_ARMOUR_PENETRATION || parameter == AttributeParameter.MAGIC_ARMOUR_PENETRATION){
			
			if(value < 0) return TextUtil.round(value*100, 0) + "%";
			return TextUtil.round(value*100, 0) + "%";
				
		}
		
		// Burn resist:
		if(parameter == AttributeParameter.BURN_RESIST){
			
			return TextUtil.round(value*100, 0) + "%";
			
		}
		
		// Drops:
		if(parameter == AttributeParameter.DROP_MODIFIER){
			
			return TextUtil.round(value*100, 0) + "%";
			
		}
		
		return "";
		
				
	}
	
	

	// Wiki:
	public static String wikiCommandsCreole(ArrayList<Method> commandMethods) {
		
		
		StringBuffer result = new StringBuffer();
		ArrayList<String> categories = new ArrayList<String>(){
			
			private static final long serialVersionUID = 1L;

			{
				add("saga.user.help");
				add("saga.user.player");
				add("saga.user.settlement");
				add("saga.user.building");
				add("saga.user.faction");
				add("saga.admin");
				add("saga.special");
				add("saga.statistics");
			}
			
		};
		
		// Sort commands:
		Comparator<Method> comparator = new Comparator<Method>() {
			
			@Override
			public int compare(Method o1, Method o2) {

				CommandPermissions perm1 = o1.getAnnotation(CommandPermissions.class);
				CommandPermissions perm2 = o2.getAnnotation(CommandPermissions.class);
				if(perm1 == null || perm2 == null || perm1.value().length == 0 || perm2.value().length == 0) return o1.getName().compareToIgnoreCase(o2.getName());
				
				return perm1.value()[0].compareToIgnoreCase(perm2.value()[0]);
				
			}
			
		};
		
		Collections.sort(commandMethods, comparator);
		
		// Categories:
		for (String category : categories) {
			
			if(result.length() > 0) result.append("\n\n");
			
			// Begin:
			result.append("==" + TextUtil.capitalize(getCategoryName(category)) + " commands" + "" + "\n");
			result.append("|=Command|=Parameters|=Description");
			
			// Commands:
			for (Method method : commandMethods) {
				
				Command command = method.getAnnotation(Command.class);
				if(command == null) continue;
				
				CommandPermissions permissions = method.getAnnotation(CommandPermissions.class);
				String permission = "";
				if(permissions != null && permissions.value().length != 0) permission = permissions.value()[0];
				
				if(!permission.startsWith(category)) continue;
				
				String flags = "";
				if(command.flags().length() > 0) flags = "[-" + command.flags().replace(" ", "] [-") + "] ";
				
				result.append("\n");
				result.append("|" + command.aliases()[0]);
				result.append("|" + flags + command.usage());
				result.append("|" + command.desc() + "|");
				
//				if(permission.length() > 0){
//					result.append("\n");
//					result.append("|" + permission);
//				}
				
				
			}
			
		}
		
		return result.toString();
		
		
	}
	
	public static String wikiPermissionsCreole(ArrayList<Method> commandMethods) {
		
		
		StringBuffer result = new StringBuffer();
		ArrayList<String> categories = new ArrayList<String>(){
			
			private static final long serialVersionUID = 1L;

			{
				add("saga.user.help");
				add("saga.user.player");
				add("saga.user.settlement");
				add("saga.user.building");
				add("saga.user.faction");
				add("saga.admin");
				add("saga.special");
				add("saga.statistics");
			}
			
		};
		
		// Sort commands:
		Comparator<Method> comparator = new Comparator<Method>() {
			
			@Override
			public int compare(Method o1, Method o2) {

				CommandPermissions perm1 = o1.getAnnotation(CommandPermissions.class);
				CommandPermissions perm2 = o2.getAnnotation(CommandPermissions.class);
				if(perm1 == null || perm2 == null || perm1.value().length == 0 || perm2.value().length == 0) return o1.getName().compareToIgnoreCase(o2.getName());
				
				return perm1.value()[0].compareToIgnoreCase(perm2.value()[0]);
				
			}
			
		};
		
		Collections.sort(commandMethods, comparator);
		

		// Begin:
		result.append("==" + "Command permissions" + "" + "\n");
		result.append("|=Permission|=Command|");		
		
		// Commands:
		for (Method method : commandMethods) {
			
			Command command = method.getAnnotation(Command.class);
			if(command == null) continue;
			
			CommandPermissions permissions = method.getAnnotation(CommandPermissions.class);
			String permission = "";
			if(permissions != null && permissions.value().length != 0) permission = permissions.value()[0];
			
			boolean stop = true;
			for (String category : categories) {
				
				if(permission.startsWith(category)){
					stop = false;
					break;
				}
				
			}
			if(stop) continue;
			
			result.append("\n");
			result.append("|" + permission);			
			result.append("|" + command.aliases()[0] + "|");
			
		}
		
		result.append("\n");
		result.append("\n");
		
		// Other permissions:
		result.append("==" + "Other permissions" + "" + "\n");
		result.append("|=Permission|=Effect|");		
		
		ArrayList<Entry<String, String>> descriptions = new ArrayList<Entry<String,String>>(PermissionsDependency.PERMISSION_DESCRIPTIONS.entrySet());
		
		// Sort:
		Comparator<Entry<String, String>> descComparator = new Comparator<Entry<String, String>>() {
			
			@Override
			public int compare(Entry<String, String> o1, Entry<String, String> o2) {

				return o1.getKey().compareToIgnoreCase(o2.getKey());
				
			}
			
		};
		Collections.sort(descriptions, descComparator);
		
		for (Entry<String, String> entry : descriptions) {
			
			result.append("\n");
			result.append("|" + entry.getKey());			
			result.append("|" + entry.getValue() + "|");
			
		}
		
		return result.toString();
		
		
	}
	
	public static String writeDone(Directory dir, String name) {
		return positive + "Write complete: " + dir.getDirectory() + dir.getFilename().replace(WriterReader.NAME_SUBS, name) + ".";
	}
	
	
}
