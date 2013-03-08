package org.saga.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.config.GeneralConfiguration;
import org.saga.messages.colours.Colour;
import org.saga.messages.colours.ColourLoop;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.ArrayUtil;
import org.saga.utility.Histogram;
import org.saga.utility.MetricPrefix;
import org.saga.utility.chat.ChatBook;
import org.saga.utility.chat.ChatFramer;
import org.saga.utility.chat.ChatTable;
import org.saga.utility.chat.ChatUtil;




public class StatisticsMessages {
	

	// Statistics:
	public static String general() {
	
		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatTable table = new ChatTable(colours);

		// Add headings:
		table.addLine(new String[]{"STATISTIC","VALUE"});

		// Blocks:
		table.addLine(new String[]{"block data change", StatisticsManager.manager().getBlockDataChanges().toString()});
		
		table.collapse();
		
		ChatBook book = new ChatBook("general statistics", colours);
		
		book.add(table);
		
		return book.framedPage(0);
		
		
	}

	public static String statisticsAge(long milliseconds) {

		
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
		int days    = (int) ((milliseconds / (1000*60*60*24)));
		
		return Colour.positive + "Statistics age " + days + "d " + hours + "h " + minutes + "m " + seconds + "s.";
		
		
	}

	public static String exp(int page) {
	
		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatTable table = new ChatTable(colours);

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
					
					table.addLine(GeneralMessages.TAB + subcateg, exp.intValue() + "", 0);

				}
				
			}
		
		}else{
			table.addLine(new String[]{"-", "-"});
		}
		
		table.collapse();
		
		ChatBook book = new ChatBook("experience statistics", colours);
		
		book.add(table);
		
		return book.framedPage(page);
		
		
	}

	public static String balance(int page) {

		
		ChatBook book = new ChatBook("economy statistics", new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
		ChatTable table = new ChatTable(new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2));
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
				
				table.addLine(new String[]{GeneralMessages.materialAbrev(material),buyTotal,buyAmount,sellTotal,sellAmount});
				
			}
			
		}else{
			table.addLine(new String[]{"-","-","-","-","-"});
		}
			
		table.collapse();
		book.add(table);
		
		return book.framedPage(page);
		
		
	}
	
	public static String values(String title, String category, String column1, String column2, boolean ignoreBottom, int decimals, int page) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook(title, colours);
		ChatTable table = new ChatTable(colours);
		
		Collection<String> subCategs = StatisticsManager.manager().getSubCategs(category, ignoreBottom);
		
		// Column names:
		table.addLine(GeneralMessages.columnTitle(column1), GeneralMessages.columnTitle(column2), 0);
		
		if(subCategs.size() != 0){
			
			for (String subCateg : subCategs) {
				
				String name = StatisticsManager.formatCategName(subCateg);
				String value = ChatUtil.round(StatisticsManager.manager().getSumValue(category + "." + subCateg, ignoreBottom), decimals);
				
				table.addLine(name, value, 0);
				
			}
			
		}else{
			
			table.addLine("-", "-", 0);
			
		}
		
		table.collapse();
		book.add(table);
		
		return book.framedPage(page);
		
		
	}
	
	public static String values(String title, String category, String varCategMain, String varCateg1, String column1, String column2, String column3, boolean ignoreBottom, int decimals, int page) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatBook book = new ChatBook(title, colours);
		ChatTable table = new ChatTable(colours);
		
		Collection<String> subCategs = StatisticsManager.manager().getSubCategs(category, ignoreBottom);
		
		// Column names:
		table.addLine(new String[]{GeneralMessages.columnTitle(column1), GeneralMessages.columnTitle(column2), GeneralMessages.columnTitle(column3)});
		
		if(subCategs.size() != 0){
			
			for (String subCateg : subCategs) {
				
				String name = StatisticsManager.formatCategName(subCateg);
				String sumCategory = category + "." + subCateg;
				String sumCategory2 = sumCategory.replace(varCategMain, varCateg1);
				String value1 = ChatUtil.round(StatisticsManager.manager().getSumValue(sumCategory, ignoreBottom), decimals);
				String value2 = ChatUtil.round(StatisticsManager.manager().getSumValue(sumCategory2, ignoreBottom), decimals);
				
				table.addLine(new String[]{name, value1, value2});
				
			}
			
		}else{
			
			table.addLine(new String[]{"-", "-", "-"});
			
		}
		
		table.collapse();
		book.add(table);
		
		return book.framedPage(page);
		
		
	}
	
	
	
	// Histogram:
	public static String histogram(String title, Double[] data, Integer width, Integer decimals, MetricPrefix metPref) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal2).addColor(Colour.normal1);
		
		ColourLoop hcolours = new ColourLoop()
			.addColor(ChatColor.LIGHT_PURPLE)
			.addColor(ChatColor.DARK_AQUA).addColor(ChatColor.DARK_AQUA).addColor(ChatColor.DARK_AQUA)
			.addColor(ChatColor.DARK_AQUA).addColor(ChatColor.DARK_AQUA)
			.addColor(ChatColor.LIGHT_PURPLE).addColor(ChatColor.LIGHT_PURPLE)
			.addColor(ChatColor.LIGHT_PURPLE).addColor(ChatColor.LIGHT_PURPLE);
		
		ChatColor axisColour = ChatColor.DARK_GRAY;
		ChatColor valsColour = ChatColor.YELLOW;
		
		ChatTable table = new ChatTable(colours);
		
		// Units:
		ArrayUtil.multiply(data, (1/metPref.getValue()));
		
		// Histogram:
		Histogram histogram = new Histogram(data);
		
		Integer[] ocurrances = histogram.createHistogram(width);
		Integer[] bars = histogram.createHistogram(width, 99);
		String[] values = histogram.createValues(width, decimals);
		
		// Title and first value:
		table.addLine(new String[]{"", ChatUtil.repeat(" ", 50), ""});
		
		for (int i = 0; i < ocurrances.length; i++) {
			
			table.addLine(new String[]{
				valsColour + values[i] + metPref.getName(),
				axisColour + "_/ " + hcolours.nextColour() +
				ChatUtil.repeat("|", bars[i] + 1),
				axisColour + "- " + valsColour + ocurrances[i].toString()
			});
			
		}
		
		table.addLine(new String[]{valsColour + values[ocurrances.length] + metPref.getName(), axisColour + "_/ " + hcolours.nextColour() + ChatUtil.repeat(" ", 50), ""});
		
		table.collapse();
		
		return ChatFramer.frame(title, table.createTable(), colours.nextColour());
		
		
	}
	
	public static String histogram(String title, Double[] data, Integer width, Integer decimals) {
		return histogram(title, data, width, decimals, MetricPrefix.NONE);
	}

	
	public static String histogram(String title, Integer[] data, Integer width, Integer decimals, MetricPrefix metPref) {
		
		
		Double[] dblData = new Double[data.length];
		
		for (int i = 0; i < dblData.length; i++) {
			dblData[i] = data[i].doubleValue();
		}
		
		return histogram(title, dblData, width, decimals, metPref);
		
		
	}
	
	public static String histogram(String title, Integer[] data, Integer width, Integer decimals) {
		return histogram(title, data, width, decimals, MetricPrefix.NONE);
	}
		
	
	
	// X-ray:
	public static String xrayIndication(String[] suspects, Double[] ratios, Integer[] veins) {

		
		ColourLoop colours = new ColourLoop().addColor(Colour.normal1).addColor(Colour.normal2);
		ChatTable table = new ChatTable(colours);
		ChatBook book = new ChatBook("x-ray indications", colours);
		
		// Notes:
		book.add("NOTE: xray indication still needs calibrating. Don't ban without investigating!");
		book.add("Use " + GeneralMessages.command("/xrayconfirm") + " when you confirmed x-ray usage.");
		
		book.add("");
		
		// Columns:
		table.addLine(new String[]{GeneralMessages.columnTitle("name"), GeneralMessages.columnTitle("ratio/ind"), GeneralMessages.columnTitle("veins")});
		
		
		StringBuffer result = new StringBuffer();
		
		result.append("\n");
		
		// Players:
		result.append(colours.nextColour() + "Indications: ");
		if(suspects.length > 0){
			
			for (int i = 0; i < suspects.length; i++) {
				
				String name = suspects[i];
				String ratioComb = ChatUtil.round((ratios[i] / GeneralConfiguration.config().getXrayDiamondRatio()), 4);
				String vein = "" + veins[i];
				
				// Confirmed:
				if(StatisticsManager.manager().isXrayConfirmed(name, Material.DIAMOND_ORE)){
					name = Colour.unavailable.toString() + ChatColor.STRIKETHROUGH + name;
					ratioComb = Colour.unavailable.toString() + ChatColor.STRIKETHROUGH + ratioComb;
					vein = Colour.unavailable.toString() + ChatColor.STRIKETHROUGH + vein;
				}
				
				table.addLine(new String[]{name, ratioComb, vein.toString()});
				
			}
			
		}else{
			
			table.addLine(new String[]{"-", "-", "-"});
			
		}
		
		table.collapse();
		book.add(table);
		
		return book.framedPage(0);
		
		
	}

	public static String xray(SagaPlayer sagaPlayer, Integer page) {

//		
//		String name = sagaPlayer.getName();
//		
//		ColorCircle colour = new ColorCircle().addColor(normal1).addColor(normal2);
//		StringBook book = new StringBook(name + " x-ray statistics", colour, 10);
//
//		// Blocks:
//		StringTable table = new StringTable(colour);
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
//		return book.framedPage(page);
//	
		return "";
		
	}


	public static String xrayConfirmed(String name) {

		return Colour.positive + "X-ray mod confirmed for " + name + ".";
		
	}

	
	
	// Updating:
	public static String updating() {

		return Colour.positive + "Updating statistics.";
		
	}
	
	public static String updated() {

		return Colour.positive + "Statistics updated.";
		
	}
	
	
	
	// Resetting:
	public static String reset() {
	
		return Colour.positive + "Statistics reset.";
		
	}

	
}
