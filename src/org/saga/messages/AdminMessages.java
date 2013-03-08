package org.saga.messages;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.saga.Clock.DaytimeTicker.Daytime;
import org.saga.abilities.AbilityDefinition;
import org.saga.attributes.Attribute;
import org.saga.attributes.AttributeParameter;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.config.FactionConfiguration;
import org.saga.config.GeneralConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.PermissionsDependency;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.Colour.CustomColour;
import org.saga.player.GuardianRune;
import org.saga.player.SagaPlayer;
import org.saga.saveload.Directory;
import org.saga.saveload.WriterReader;
import org.saga.settlements.Settlement;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.chat.ChatBook;
import org.saga.utility.chat.ChatTable;
import org.saga.utility.chat.ChatUtil;
import org.saga.utility.chat.RomanNumeral;
import org.sk89q.Command;
import org.sk89q.CommandPermissions;

public class AdminMessages {

	

	// Player levels:
	public static String playerExpSet(Integer exp, SagaPlayer sagaPlayer){
		return Colour.positive + "Player " + sagaPlayer.getName() + " exp set to " + exp + ".";
	}
	
	public static String playerExpSet(Integer exp){
		return Colour.positive + "Exp was set to " + exp + ".";
	}

	public static String playerExpOutOfRange(String exp){
		return Colour.negative + "Exp " + exp + " is out of range. Allowed range: 0 - " + ExperienceConfiguration.config().getMaxExp() + ".";
	}
	
	
	
	// Settlement and faction claims:
	public static String settleClaimsOutOfRange(String claims){
		return Colour.negative + "Claims " + claims + " is out of range. Allowed range: 0 - " + SettlementConfiguration.config().getMaxClaims() + ".";
	}
	
	public static String settleBuildPointsOutOfRange(String bldPts){
		return Colour.negative + "Build points " + bldPts + " is out of range. Allowed range: 0 - " + SettlementConfiguration.config().getMaxBuildPoints() + ".";
	}
	
	public static String factionClaimsOutOfRange(String claims){
		return Colour.negative + "Claims " + claims + " is out of range. Allowed range: 0 - " + FactionConfiguration.config().getMaxClaims() + ".";
	}

	public static String setClaims(Settlement settlement){
		return Colour.positive + "Settlement " + settlement.getName() + " claims set to " + settlement.getTotalClaims() + ".";
	}
	
	public static String setBuildPoints(Settlement settlement){
		return Colour.positive + "Settlement " + settlement.getName() + " build points set to " + settlement.getAvailableBuildPoints() + ".";
	}
	
	
	// Healing:
	public static String healed(){
		return Colour.positive + "You were healed.";
	}
	
	public static String healed(SagaPlayer selPlayer){
		return Colour.positive + "Healed " + selPlayer.getName() + ".";
	}
	
	
	
	// Attributes:
	public static String attributeSet(String attribute, Integer score){
		return Colour.positive + ChatUtil.capitalize(attribute) + " was set to " + score + ".";
	}
	
	public static String attributeSet(String attribute, Integer score, SagaPlayer selPlayer){
		return Colour.positive + "Players " + selPlayer.getName() + " " + attribute + " was set to " + score + ".";
	}
	
	public static String attributeInvalid(String attribute){
		return Colour.negative + ChatUtil.capitalize(attribute) + " isn't a valid attribute.";
	}
	
	public static String attributeScoreOutOfRange(String score){
		return Colour.negative + "Attribute score " + score + " is out of range. Allowed range: 0 - " + AttributeConfiguration.config().getMaxAttributeCap() + ".";
	}
	
	
	
	// Administrator mode:
	public static String adminModeChanged(SagaPlayer sagaPlayer) {
		
		if(sagaPlayer.isAdminMode()){
			return Colour.positive + "Admin mode enabled.";
		}else{
			return Colour.positive + "Admin mode disabled.";
		}
		
	}
	
	public static String adminModeAlreadyEnabled() {
		return Colour.negative + "Admin mode already enabled.";
	}
	
	public static String adminModeAlreadyDisabled() {
		return Colour.negative + "Admin mode already disabled.";
	}

	
	
	// Guardian rune:
	public static String runeRecharged(GuardianRune rune, SagaPlayer sagaPlayer) {
		return Colour.positive + "Recharged players " + sagaPlayer.getName() + " guardian rune recharged.";
	}
	
	public static String runeRecharged(GuardianRune rune) {
		return Colour.positive + "Recharged guardian rune.";
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

	
	
	// Border command:
	public static String borderRepeatAdminModeOnly() {
		return Colour.negative + "Repeat function is only available in admin mode.";
	}
	
	public static String borderWildernessAdminModeOnly() {
		return Colour.negative + "Admin mode required to use border command in the wilderness.";
	}
	
	
	
	// Production:
	public static String forcedWork() {
		return Colour.positive + "Forced work tick.";
	}
	
	public static String forcedCollect() {
		return Colour.positive + "Forced collect tick.";
	}
	
	public static String forcedProduction() {
		return Colour.positive + "Forced production tick.";
	}
	
	
	
	// Wiki:
	private static String getCategoryName(String fullCateg) {

		String[] nodes = fullCateg.split("\\.");
		
		return nodes[nodes.length-1];
		
	}
	
	private static String parameterKey(AttributeParameter parameter) {

		return parameter.toString().toLowerCase().replace("_", " ").replace(" modifier", "").replace(" multiplier", "").replace("penetration", "pen");
				
	}
	
	private static String parameterValue(AttributeParameter parameter, Double value) {

		
		int prec = 1;
		
		if(parameter.toString().toLowerCase().endsWith("chance_modifier")){
			if(value < 0) return "" + ChatUtil.round(value*100, prec) + "%";
			return "+" + ChatUtil.round(value*100, prec) + "%";
		}
		
		if(parameter.toString().toLowerCase().endsWith("multiplier")){
			return ChatUtil.round(100*value, prec) + "%";
		}
		
		if(parameter.toString().toLowerCase().endsWith("modifier")){
			if(value < 0) return "" + ChatUtil.round(value, prec);
			return "+" + ChatUtil.round(value, prec);
		}
		
		if(parameter.toString().toLowerCase().endsWith("penetration")){
			if(value < 0) return "" + ChatUtil.round(value, prec);
			return "+" + ChatUtil.round(value*100, prec-2) + "%";
		}
		
		
		return ChatUtil.round(value, prec);
		
				
	}

	public static String writeDone(Directory dir, String name) {
		return Colour.positive + "Write complete: " + dir.getDirectory() + dir.getFilename().replace(WriterReader.NAME_SUBS, name) + ".";
	}
	
	public static String format(ChatTable table, String start, String left, String middle, String right, String next, String end) {
		
		
		StringBuffer result = new StringBuffer();
		
		String[][] contents = table.getRawContents();
		
		result.append(start);

		for (int row = 0; row < contents.length; row++) {
			
			if(row != 0) result.append(next);
			
			result.append(left);
			
			for (int col = 0; col < contents[row].length; col++) {
				
				if(col != 0) result.append(middle);
				
				result.append(contents[row][col]);
				
				if(col == contents[row].length - 1) result.append(right);
				
			}
			
		}
		
		result.append(end);
		
		return CustomColour.strip(result.toString());
		
		
	}
	
	
	
	// Wiki normal:
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
				add("saga.user.war");
				add("saga.user.economy");
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
			result.append("==" + ChatUtil.capitalize(getCategoryName(category)) + " commands" + "==" + "\n");
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
				add("saga.user.war");
				add("saga.user.economy");
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

	public static String wikiAttributes() {
		
		
		StringBuffer result = new StringBuffer();
		ArrayList<Attribute> attributes = AttributeConfiguration.config().getAttributes();
		int max = AttributeConfiguration.config().getMaxAttributeCap();
		
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
			
			ArrayList<Entry<AttributeParameter, TwoPointFunction>> parameters = attribute.getAllParameterEntries();
			
			result.append("\n");
			result.append("|-");
			result.append("\n");
			result.append("! rowspan=\"" + parameters.size() + "\" | " + ChatUtil.capitalize(attribute.getName()));
			
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
	
	public static String wikiAbilities() {
		
		
		StringBuffer result = new StringBuffer();
		
		ArrayList<AbilityDefinition> definitions = AbilityConfiguration.config().getDefinitions();
		
		// Title:
		result.append("=Abilities=");
		
		result.append("\n");
		
		for (int i = 0; i < definitions.size(); i++) {
			
			AbilityDefinition definition = definitions.get(i);
			
			result.append("\n");
			
			// Name:
			result.append("==" + ChatUtil.capitalize(definition.getName()) + "==");
			
			result.append("\n");
			result.append("\n");

			// Description:
			result.append(ChatUtil.senctence(definition.getFullDescription()));
			
			result.append("\n");
			result.append("\n");

			result.append("{|");
			
			result.append("\n");

			// Scores:
			result.append("|");
			result.append("Score");
			for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {

				result.append("||");
				result.append(RomanNumeral.binaryToRoman(score));

			}
			result.append("\n");
			result.append("|-");
			
			
			result.append("\n");
			
			// Energy:
			result.append("|");
			result.append("Energy");
			for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {

				result.append("||");
				result.append(definition.getUsedEnergy(score));
				
			}
			result.append("\n");
			result.append("|-");
			
			result.append("\n");
			
			// Attributes:
			result.append("|");
			result.append("Requirements");
			for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {
				
				result.append("||");
				result.append(StatsMessages.requirements(definition, score));
				
				
			}
			result.append("\n");
			result.append("|}");
			

			result.append("\n");
			result.append("\n");

			// Usage:
			result.append("Usage: ");
			result.append(definition.getUsage());
			result.append(".");
			
			// Restrictions:
			String restrictions = StatsMessages.restrictions(definition);
			if(restrictions.length() > 0){
				
				result.append("\n");
				result.append("\n");
				
				result.append("Restrictions: ");
				result.append(restrictions);
				result.append(".");
				
			}

			result.append("\n");
			
		}
		
		return result.toString();
		
		
	}

	public static String wikiHelpMessages() {
		
		
		String start = "{|\n";
		String left = "|";
		String middle = "||";
		String right = "";
		String next = "\n|-\n";
		String end = "\n|}";
		
		StringBuffer result = new StringBuffer();
		ChatBook[] books = new ChatBook[]{HelpMessages.pbook(), HelpMessages.sbook(), HelpMessages.fbook(), HelpMessages.ebook()};
		
		result.append("=" + "Help Messages" + "=");
		
		result.append("\n");
		result.append("\n");

		// All books:
		for (int b = 0; b < books.length; b++) {
			
			ChatBook book = books[b];
			
			if(b != 0){
				result.append("\n");
				result.append("\n");
				result.append("\n");
			}

			// Title:
			result.append("==" + book.getTitle() + "==");
			result.append("\n");
			
			for (int i = 0; i < book.sections(); i++) {
				
				String sect = CustomColour.strip(book.getSection(i));
				
				// Next page:
				if(sect.equals(ChatBook.PAGE_BREAK)){
					result.append("\n");
					continue;
				}
				
				// Next paragraph:
				if(i != 0 && !sect.startsWith(" ")) result.append("\n");
				
				// Table:
				if(sect.startsWith(ChatBook.TABLE.toString())){
					
					ChatTable table = book.getTable(sect);
					result.append(format(table, start, left, middle, right, next, end));
					
				}
				
				// Content:
				else result.append(sect);
				
			}
			
		}
		
		return result.toString();
		
		
	}
	
	
	
	// Wiki Creole:
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
				add("saga.user.war");
				add("saga.user.economy");
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
			result.append("==" + ChatUtil.capitalize(getCategoryName(category)) + " commands" + "" + "\n");
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
				add("saga.user.war");
				add("saga.user.economy");
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

	public static String wikiAttributesCreole() {
		
		
		int step = 10;
		int init = 5;
		
		StringBuffer result = new StringBuffer();
		ArrayList<Attribute> attributes = AttributeConfiguration.config().getAttributes();
		int max = AttributeConfiguration.config().getMaxAttributeCap();
		
		// Title:
		result.append("==Attributes");
		
		for (Attribute attribute : attributes) {
			
			result.append("\n");
			
			// Empty line:
			result.append("| |");
			for (int k = 0; k <= max; k+=step) {
				result.append(" ");
				result.append("|");
			}
			
			result.append("\n");
			
			// Attribute name:
			result.append("|="+attribute.getName()+"|");
			
			// Scores:
			for (int j = init; j <= max; j+=step) {
				result.append(" " + j + " ");
				result.append("|");
			}
			
			ArrayList<Entry<AttributeParameter, TwoPointFunction>> parameters = attribute.getAllParameterEntries();
			for (int i = 0; i < parameters.size(); i++) {
			
				Entry<AttributeParameter, TwoPointFunction> parameter = parameters.get(i);
				
				result.append("\n");
				
				result.append("|");
				
				// Parameter:
				result.append(parameterKey(parameter.getKey()));
				
				result.append("|");
				
				// Values:
				for (int j = init; j <= max; j+=step) {
					
					result.append(parameterValue(parameter.getKey(), parameter.getValue().value(j)));
					
					result.append("|");
					
				}
				
			}
			
		}
		
		return result.toString();
		
		
	}

	public static String wikiAbilitiesCreole() {
		
		
		String rowCompensation = " | | | | | | | | | | | | | |";
		String horizontalLine = "----";
		
		StringBuffer result = new StringBuffer();
		
		ArrayList<AbilityDefinition> definitions = AbilityConfiguration.config().getDefinitions();
		
		// Title:
		result.append("=Abilities");
		
		result.append("\n");
		result.append("\n");
		
		result.append(horizontalLine);
		
		for (int i = 0; i < definitions.size(); i++) {
			
			AbilityDefinition definition = definitions.get(i);
			
			result.append("\n");
			
			result.append("\n");
			
			// Name:
			result.append("==" + ChatUtil.capitalize(definition.getName()));
			
			result.append("\n");
			result.append("\n");

			// Description:
			result.append(ChatUtil.senctence(definition.getFullDescription()));
			
			result.append("\n");
			result.append("\n");

			// Scores:
			result.append("|");
			result.append("Score");
			result.append("|");
			for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {
				result.append(RomanNumeral.binaryToRoman(score));
				result.append("|");
			}
			result.append(rowCompensation);
			
			result.append("\n");
			
			// Energy:
			result.append("|");
			result.append("Energy");
			result.append("|");
			for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {
				
				// Energy:
				result.append(definition.getUsedEnergy(score));
				
				result.append("|");
				
			}
			result.append(rowCompensation);
			
			result.append("\n");
			
			// Attributes:
			result.append("|");
			result.append("Requirements");
			result.append("|");
			for (int score = 1; score <= AbilityConfiguration.config().maxAbilityScore; score++) {
				
				result.append(StatsMessages.requirements(definition, score));
				result.append("|");
				
			}
			result.append(rowCompensation);

			result.append("\n");
			result.append("\n");

			// Usage:
			result.append("Usage: ");
			result.append(definition.getUsage());
			result.append(".");
			
			// Restrictions:
			String restrictions = StatsMessages.restrictions(definition);
			if(restrictions.length() > 0){

				result.append("\n");
				result.append("\n");
				
				result.append("Restrictions: ");
				result.append(restrictions);
				result.append(".");
				
			}

			result.append("\n");
			
			result.append(horizontalLine);
			
			
		}
		
		return result.toString();
		
		
	}
	
	public static String wikiHelpMessagesCreole() {
		
		
		String start = "";
		String left = "|";
		String middle = "|";
		String right = "|";
		String next = "\n";
		String end = "";
		
		StringBuffer result = new StringBuffer();
		ChatBook[] books = new ChatBook[]{HelpMessages.pbook(), HelpMessages.sbook(), HelpMessages.fbook(), HelpMessages.ebook()};
		
		result.append("=" + "Help Messages" + "=");
		
		result.append("\n");
		result.append("\n");

		// All books:
		for (int b = 0; b < books.length; b++) {
			
			ChatBook book = books[b];
			
			if(b != 0){
				result.append("\n");
				result.append("\n");
				result.append("\n");
			}

			// Title:
			result.append("==" + book.getTitle() + "==");
			result.append("\n");
			
			for (int i = 0; i < book.sections(); i++) {
				
				String sect = CustomColour.strip(book.getSection(i));
				
				// Next page:
				if(sect.equals(ChatBook.PAGE_BREAK)){
					result.append("\n");
					continue;
				}
				
				// Next paragraph:
				if(i != 0 && !sect.startsWith(" ")) result.append("\n");
				
				// Table:
				if(sect.startsWith(ChatBook.TABLE.toString())){
					
					ChatTable table = book.getTable(sect);
					result.append(format(table, start, left, middle, right, next, end));
					
				}
				
				// Content:
				else result.append(sect);
				
			}
			
		}
		
		return result.toString();
		
		
	}
	
	
	// Saving loading:
	public static String saving() {
		return Colour.veryPositive + "Saving Saga information.";
	}
	
	public static String saved() {
		return Colour.veryPositive + "Saving complete.";
	}

	

	// Additional info:
	public static String statsTargetName(SagaPlayer sagaPlayer) {
		return Colour.positive + "Stats for " + sagaPlayer.getName() + ".";
	}

	
	// Time:
	public static String nextDaytime(World world, Daytime daytime) {

		return Colour.positive + "Daytime set to " + daytime + " for world " + world.getName() + ".";
		
	}


}
