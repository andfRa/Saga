package org.saga.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.config.AbilityConfiguration;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.messages.PlayerMessages.ColorCircle;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.MathUtil;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;




public class StatisticsMessages {
	

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
	
	
	

	public static String general() {
	
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(color);

		// Add headings:
		table.addLine(new String[]{"STATISTIC","VALUE"});
		
		// Guardian stone:
		table.addLine(new String[]{"guardian stone restores", StatisticsManager.manager().getGuardStoneBreaks().toString()});
		table.addLine(new String[]{"guardian stone recharges", StatisticsManager.manager().getGuardStoneFixes().toString()});
		
		// Blocks:
		table.addLine(new String[]{"block data change", StatisticsManager.manager().getBlockDataChanges().toString()});
		
		table.collapse();
		
		StringBook book = new StringBook("general statistics", color, 10);
		
		book.addTable(table);
		
		return book.framed(0, table.calcTotalWidth());
		
		
	}

	public static String abilities() {
	
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		ArrayList<String> abilities = AbilityConfiguration.config().getAbilityNames();
		StringTable table = new StringTable(color);

		// Add headings:
		table.addLine(new String[]{"ABILITY","USES", "EXP AWARDED"});
		
		// Add data:
		StatisticsManager manager = StatisticsManager.manager();
		for (int i = 0; i < abilities.size(); i++) {
			
			String name = abilities.get(i);
			
			String[] row = new String[]{name, manager.getAbilityUses(name).toString(), manager.getAbilityExp(name).toString()};
			
			table.addLine(row);
			
		}
		
		table.collapse();
		
		StringBook book = new StringBook("abilities", color, 18);
		
		book.addTable(table);
		
		return book.framed(0, table.calcTotalWidth());
		
		
	}
	
	public static String statisticsAge(long milliseconds) {

		
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
		int days    = (int) ((milliseconds / (1000*60*60*24)));
		
		return positive + "Statistics age " + days + "d " + hours + "h " + minutes + "m " + seconds + "s.";
		
		
	}
	
	public static String xrayIndication(Integer totalPlayers, ArrayList<String> indications) {

		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBuffer result = new StringBuffer();
		
		// Totals:
		result.append(color.nextColor() + "Indications: " + indications.size() + "/" + totalPlayers);
		
		result.append("\n");
		
		// Players:
		result.append(color.nextColor() + "Indications: ");
		if(indications.size() > 0){
			
			result.append(TextUtil.flatten(indications));
			
		}else{
			
			result.append("none");
			
		}
		
		return TextUtil.frame("x-ray mod indications", color.nextColor(), result.toString(), 60);
		
		
	}

	public static String xray(SagaPlayer sagaPlayer, Integer page) {

//		
//		String name = sagaPlayer.getName();
//		
//		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
//		StringBook book = new StringBook(name + " x-ray statistics", color, 10);
//
//		// Blocks:
//		StringTable table = new StringTable(color);
//		String[] line = new String[2];
//		HashSet<Material> xrayMaterials = XrayIndicator.getXrayBlocks();
//		
//		line = new String[]{"MATERIAL", "AMOUNT", "RATIO"};
//		table.addLine(line);
//		
//		for (Material xrMaterial : xrayMaterials) {
//			
//			Integer amount = StatisticsManager.manager().getOreMined(name, xrMaterial);
//			String amountValue = amount.toString();
//			
//			Double ratio = StatisticsManager.manager().getOreRatio(name, xrMaterial);
//			boolean indication = BalanceConfiguration.config().checkXrayIndication(xrMaterial, ratio);
//			
//			String ratioValue = String.format("%.2g", ratio*100) + " %";
//			if(indication) ratioValue = ChatColor.DARK_RED + ratioValue;
//			
//			if(xrMaterial == Material.STONE){
//				
//				line = new String[]{EconomyMessages.material(xrMaterial), amountValue, ""};
//				
//			}else{
//				
//				line = new String[]{EconomyMessages.material(xrMaterial), amountValue, ratioValue};
//			
//			}
//			
//			table.addLine(line);
//			
//		}
//		
//		table.collapse();
//		book.addTable(table);
//		
//		return book.framed(page);
//	
		return "";
		
	}

	public static String levels(int page) {
	
		
		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook result = new StringBook("Level statistics", colour, 10);
		
		// Histogram data:
		Double[] data = StatisticsManager.manager().getLevelHistogram(ExperienceConfiguration.config().maximumLevel, false);
		Double maxVal = MathUtil.max(data);
		
		// No data:
		if(maxVal <= 0.0){
			
			maxVal = 1.0;
			
			for (int i = 0; i < data.length; i++) {
				data[i] = 1.0;
			}
			
		}
		
		int height = 10;
		MathUtil.multiply(data, height / maxVal);
		
		// Histogram:
		ChatColor frameColor = ChatColor.DARK_GRAY;
		ColorCircle histColours = new ColorCircle().addColor(ChatColor.LIGHT_PURPLE).addColor(ChatColor.DARK_AQUA);
		result.addLine("");
		result.addLine("LEVEL DISTRIBUTION");
		result.addLine("  " + frameColor + TextUtil.repeat("..", ExperienceConfiguration.config().maximumLevel));
		result.addLine(frameColor + "  |" + TextUtil.histogram(data, histColours).replaceAll("\n", frameColor + "|\n  |") + frameColor + "|");
		result.addLine("  " + frameColor + TextUtil.repeat("..", ExperienceConfiguration.config().maximumLevel));
		result.addLine("");
		
		return result.framed(page);
		
		
	}
	
	public static String attributes() {
		
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(color);

		// Add headings:
		table.addLine(new String[]{GeneralMessages.columnTitle("attribute"), GeneralMessages.columnTitle("total score")});
		
		ArrayList<String> attributes = AttributeConfiguration.config().getAttributeNames();
		for (String attribute : attributes) {
			
			table.addLine(attribute, StatisticsManager.manager().getAttributeScoreTotal(attribute).toString(), 0);
			
		}
		
		table.collapse();
		
		StringBook book = new StringBook("attribute statistics", color, 10);
		
		book.addTable(table);
		
		return book.framed(0, table.calcTotalWidth());
		
		
	}
		
	
	public static String exp(int page) {
	
		
		ColorCircle color = new ColorCircle().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(color);

		// Add headings:
		table.addLine(new String[]{GeneralMessages.columnTitle("category"), GeneralMessages.columnTitle("exp")});
		
		Collection<String> categs = StatisticsManager.manager().getExpCategories();
		Collection<String> subcategs = StatisticsManager.manager().getExpSubcategories();
		
		if(subcategs.size() != 0){
			
			for (String categ : categs) {

				Double exp = StatisticsManager.manager().getExpGained(categ);
				table.addLine(categ, exp.intValue() + "", 0);
				
				for (String subcateg : subcategs) {
					
					exp = StatisticsManager.manager().getExpGained(categ, subcateg);
					if(exp <= 0) continue;
					
					table.addLine(TextUtil.TAB + subcateg, exp.intValue() + "", 0);
					
				}
				
			}
		
		}else{
			table.addLine(new String[]{"-", "-"});
		}
		
		table.collapse();
		
		StringBook book = new StringBook("experience statistics", color, 10);
		
		book.addTable(table);
		
		return book.framed(page, table.calcTotalWidth());
		
		
	}

	public static String xrayDistrib(int page) {
	
		
		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
		StringBook result = new StringBook("X-ray distributions", colour, 10);
		
		// Histogram data:
		Double[] data = StatisticsManager.manager().getVeinHistogram(Material.DIAMOND_ORE, 50, false);
		Double maxVal = MathUtil.max(data);
		
		// No data:
		if(maxVal <= 0.0){
			
			maxVal = 1.0;
			
			for (int i = 0; i < data.length; i++) {
				data[i] = 1.0;
			}
			
		}
		
		int height = 10;
		MathUtil.multiply(data, height / maxVal);
		
		// Histogram:
		ChatColor frameColor = ChatColor.DARK_GRAY;
		ColorCircle histColours = new ColorCircle().addColor(ChatColor.LIGHT_PURPLE).addColor(ChatColor.DARK_AQUA);
		result.addLine("");
		result.addLine("DIAMOND RATIO DISTRIBUTION");
		result.addLine("  " + frameColor + TextUtil.repeat("..", ExperienceConfiguration.config().maximumLevel));
		result.addLine(frameColor + "  |" + TextUtil.histogram(data, histColours).replaceAll("\n", frameColor + "|\n  |") + frameColor + "|");
		result.addLine("  " + frameColor + TextUtil.repeat("..", ExperienceConfiguration.config().maximumLevel));
		result.addLine("");
		
		if(data.length > 0) result.addLine("Maximum: " + maxVal);
		
		return result.framed(page);
		
		
	}
	
	public static String balance(int page) {

		
		StringBook book = new StringBook("economy statistics", new ColorCircle().addColor(normal1).addColor(normal2), 10);
		StringTable table = new StringTable(new ColorCircle().addColor(normal1).addColor(normal2));
		final StatisticsManager manager = StatisticsManager.manager();
		
		ArrayList<Material> materials = StatisticsManager.manager().getAllEcoMaterials();
		
		// Sort:
		Comparator<Material> comp = new Comparator<Material>() {
			@Override
			public int compare(Material o1, Material o2) {
			
				return (int) ( (manager.getBuyCoins(o2) + manager.getSellCoins(o2)) - (manager.getBuyCoins(o1) + manager.getSellCoins(o1)) );
			
			}
		};
		Collections.sort(materials, comp);
		
		// Names:
		table.addLine(new String[]{"ITEM","BUY/PL","AMOUNT","SELL/PL","AMOUNT"});
		
		// Materials:
		if(materials.size() > 0){
			
			for (Material material : materials) {

				String buyTotal = "-";
				if(manager.getBuyCoins(material) != null) buyTotal = EconomyMessages.coins(manager.getBuyCoins(material) / manager.countBuyPlayers(material).doubleValue());

				String buyAmount = "-";
				if(manager.getBuyAmount(material) != null) buyAmount = manager.getBuyAmount(material).toString();

				String sellTotal = "-";
				if(manager.getSellCoins(material) != null) sellTotal = EconomyMessages.coins(manager.getSellCoins(material) / manager.countSellPlayers(material).doubleValue());

				String sellAmount = "-";
				if(manager.getSellAmount(material) != null) sellAmount = manager.getSellAmount(material).toString();
				
				table.addLine(new String[]{EconomyMessages.materialShort(material),buyTotal,buyAmount,sellTotal,sellAmount});
				
			}
			
		}else{
			table.addLine(new String[]{"-","-","-","-","-"});
		}
			
		table.collapse();
		book.addTable(table);
		
		return book.framed(page);
		
		
	}
	
	
	
	
	// Resetting:
	public static String reset() {
	
		return positive + "Statistics reset.";
		
	}

	
}
