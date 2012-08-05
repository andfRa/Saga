package org.saga.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.saga.messages.PlayerMessages.ColourLoop;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.Histogram;
import org.saga.utility.text.StringBook;
import org.saga.utility.text.StringTable;
import org.saga.utility.text.TextUtil;




public class StatisticsMessages {
	

	// Colours:
	public static ChatColor veryPositive = ChatColor.DARK_GREEN; // DO NOT OVERUSE.
	
	public static ChatColor positive = ChatColor.GREEN;
	
	public static ChatColor negative = ChatColor.RED;
	
	public static ChatColor veryNegative = ChatColor.DARK_RED; // DO NOT OVERUSE.
	
	public static ChatColor unavailable = ChatColor.DARK_GRAY;
	
	public static ChatColor announce = ChatColor.AQUA;
	
	public static ChatColor normal1 = ChatColor.GOLD;
	
	public static ChatColor normal2 = ChatColor.YELLOW;
	
	public static ChatColor frame = ChatColor.DARK_GREEN;
	
	

	// Statistics:
	public static String general() {
	
		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);

		// Add headings:
		table.addLine(new String[]{"STATISTIC","VALUE"});

		// Blocks:
		table.addLine(new String[]{"block data change", StatisticsManager.manager().getBlockDataChanges().toString()});
		
		table.collapse();
		
		StringBook book = new StringBook("general statistics", colours);
		
		book.addTable(table);
		
		return book.framedPage(0);
		
		
	}

	public static String statisticsAge(long milliseconds) {

		
		int seconds = (int) (milliseconds / 1000) % 60 ;
		int minutes = (int) ((milliseconds / (1000*60)) % 60);
		int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
		int days    = (int) ((milliseconds / (1000*60*60*24)));
		
		return positive + "Statistics age " + days + "d " + hours + "h " + minutes + "m " + seconds + "s.";
		
		
	}
	
	public static String xrayIndication(Integer totalPlayers, ArrayList<String> indications) {

		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBuffer result = new StringBuffer();
		
		// Totals:
		result.append(colours.nextColour() + "Indications: " + indications.size() + "/" + totalPlayers);
		
		result.append("\n");
		
		// Players:
		result.append(colours.nextColour() + "Indications: ");
		if(indications.size() > 0){
			
			result.append(TextUtil.flatten(indications));
			
		}else{
			
			result.append("none");
			
		}
		
		return TextUtil.frame("x-ray mod indications", result.toString(), colours.nextColour());
		
		
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

	public static String exp(int page) {
	
		
		int maxLines = 20;
		int lines = 0;
		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringTable table = new StringTable(colours);

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
					
					lines++;
					
					if(lines > maxLines){
						lines = 0;
						table.nextPage();
					}
					
				}
				
			}
		
		}else{
			table.addLine(new String[]{"-", "-"});
		}
		
		table.collapse();
		
		StringBook book = new StringBook("experience statistics", colours);
		
		book.addTable(table);
		
		return book.framedPage(page);
		
		
	}

	public static String balance(int page) {

		
		StringBook book = new StringBook("economy statistics", new ColourLoop().addColor(normal1).addColor(normal2));
		StringTable table = new StringTable(new ColourLoop().addColor(normal1).addColor(normal2));
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
		
		return book.framedPage(page);
		
		
	}
	
	
	
	public static String values(String title, String category, String column1, String column2, boolean ignoreBottom, int decimals, int page) {

		
		ColourLoop colours = new ColourLoop().addColor(normal1).addColor(normal2);
		StringBook book = new StringBook(title, colours);
		StringTable table = new StringTable(colours);
		
		Collection<String> subCategs = StatisticsManager.manager().getSubCategs(category, ignoreBottom);
		
		// Column names:
		table.addLine(GeneralMessages.columnTitle(column1), GeneralMessages.columnTitle(column2), 0);
		
		if(subCategs.size() != 0){
			
			for (String subCateg : subCategs) {
				
				String name = StatisticsManager.formatCategName(subCateg);
				String value = TextUtil.round(StatisticsManager.manager().getSumValue(category + "." + subCateg, ignoreBottom), decimals);
				
				int depth = StatisticsManager.calcCategDepth(subCateg);
				value = TextUtil.repeat(GeneralMessages.TAB, depth) + value; 
				
				table.addLine(name, value, 0);
				
			}
			
		}else{
			
			table.addLine("-", "-", 0);
			
		}
		
		table.collapse();
		book.addTable(table);
		
		return book.framedPage(page);
		
		
	}
	
	
	
	// Histogram:
	public static String histogram(String title, Double[] data, Integer width, Integer decimals) {

		
		ColourLoop colours = new ColourLoop().addColor(normal2).addColor(normal1);
		
		ColourLoop hcolours = new ColourLoop()
			.addColor(ChatColor.LIGHT_PURPLE)
			.addColor(ChatColor.DARK_AQUA).addColor(ChatColor.DARK_AQUA).addColor(ChatColor.DARK_AQUA)
			.addColor(ChatColor.DARK_AQUA).addColor(ChatColor.DARK_AQUA)
			.addColor(ChatColor.LIGHT_PURPLE).addColor(ChatColor.LIGHT_PURPLE)
			.addColor(ChatColor.LIGHT_PURPLE).addColor(ChatColor.LIGHT_PURPLE);
		
		ChatColor axisColour = ChatColor.DARK_GRAY;
		ChatColor valsColour = ChatColor.YELLOW;
		
		StringTable table = new StringTable(colours);
		
		Histogram histogram = new Histogram(data);
		
		Integer[] ocurrances = histogram.createHistogram(width);
		Integer[] bars = histogram.createHistogram(width, 99);
		String[] values = histogram.createValues(width, decimals);
		
		// Title and first value:
		table.addLine(new String[]{"", TextUtil.repeat(" ", 50), ""});
		
		for (int i = 0; i < ocurrances.length; i++) {
			
			table.addLine(new String[]{
				valsColour + values[i],
				axisColour + "_/ " + hcolours.nextColour() +
				TextUtil.repeat("|", bars[i] + 1),
				axisColour + "- " + valsColour + ocurrances[i].toString()
			});
			
		}
		
		table.addLine(new String[]{valsColour + values[ocurrances.length], axisColour + "_/ " + hcolours.nextColour() + TextUtil.repeat(" ", 50), ""});
		
		table.collapse();
		
		return TextUtil.frame(title, table.createTable(), colours.nextColour());
		
		
	}
	
	public static String histogram(String title, Integer[] data, Integer width, Integer decimals) {
		
		
		Double[] dblData = new Double[data.length];
		
		for (int i = 0; i < dblData.length; i++) {
			dblData[i] = data[i].doubleValue();
		}
		
		return histogram(title, dblData, width, decimals);
		
		
	}
	
	
	
	// Resetting:
	public static String reset() {
	
		return positive + "Statistics reset.";
		
	}

	
}
