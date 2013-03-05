package org.saga.buildings.signs;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.saga.buildings.Building;
import org.saga.buildings.TradingPost;
import org.saga.buildings.production.SagaItem;
import org.saga.buildings.production.SagaPricedItem;
import org.saga.config.EconomyConfiguration;
import org.saga.config.SettlementConfiguration;
import org.saga.dependencies.EconomyDependency;
import org.saga.messages.EconomyMessages;
import org.saga.messages.GeneralMessages;
import org.saga.player.SagaPlayer;
import org.saga.statistics.StatisticsManager;


public class ExportSign extends BuildingSign {


	/**
	 * Name for the sign.
	 */
	public static String SIGN_NAME = "=EXPORT=";

	/**
	 * Amount division string.
	 */
	public static String DATA_DIV = ":";
	
	/**
	 * Amount division string.
	 */
	public static String AMOUNT_DIV = "\\*";
	
	/**
	 * Displayed amount division string.
	 */
	public static String AMOUNT_DIV_DISPLAY = "*";
	
	/**
	 * Saga item representing the sign.
	 */
	transient private SagaItem item = null;

	
	
	// Initialisation:
	/**
	 * Creates a import sign. 
	 * 
	 * @param sign sign
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param event event that created the sign
	 * @param building building
	 */
	protected ExportSign(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building){
	
		super(sign, SIGN_NAME, secondLine, thirdLine, fourthLine, building);
		
		initialiseFields();
		
	}
	
	/**
	 * Creates the training 
	 * 
	 * @param sign bukkit sign
	 * @param firstLine first line
	 * @param secondLine second line
	 * @param thirdLine third line
	 * @param fourthLine fourth line
	 * @param building building
	 * @return training sign
	 */
	public static ExportSign create(Sign sign, String secondLine, String thirdLine, String fourthLine, Building building) {
		return new ExportSign(sign, secondLine, thirdLine, fourthLine, building);
	}

	@Override
	public boolean complete(Building building) throws SignException {
		
		super.complete(building);
		
		initialiseFields();
		
		return true;
		
	}
	
	/**
	 * Initialises fields.
	 * 
	 */
	private void initialiseFields() {

		
		Material type = null;
		Short data = null;
		Integer amount = null;
		
		// First parameter:
		String[] firstParameter = getFirstParameter().split(AMOUNT_DIV);

		String sMaterial = null;
		String sData = null;
		String sAmount = null;
		
		// Amount, type and data:
		if(firstParameter.length == 2){
			
			String[] sMatData = firstParameter[1].split(DATA_DIV);
			
			// Material:
			sMaterial = sMatData[0];
			type = Material.matchMaterial(sMaterial);
			if(type == null){
				try {
					type = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			
			// Data:
			if(sMatData.length > 1){
				sData = sMatData[1];
				try {
					data = Short.parseShort(sData);
				} catch (NumberFormatException e) { }
			}
			
			// Amount:
			sAmount = firstParameter[0];
			try {
				amount = Integer.parseInt(sAmount);
			} catch (NumberFormatException e) {}
			
		}else{

			String[] sMatData = firstParameter[0].split(DATA_DIV);
			
			// Material:
			sMaterial = sMatData[0];
			type = Material.matchMaterial(sMaterial);
			if(type == null){
				try {
					type = Material.getMaterial(Integer.parseInt(sMaterial));
				} catch (NumberFormatException e) { }
			}
			if(type == null) return;

			// Data:
			if(sMatData.length > 1){
				sData = sMatData[1];
				try {
					data = Short.parseShort(sData);
				} catch (NumberFormatException e) { }
			}
			
			amount = type.getMaxStackSize();
			
		}
		
		// Fix amount:
		if(amount != null && amount < 0) amount = 0;
		
		// Fix data:
		if(data == null) data = 0;
		
		// Item:
		item = new SagaItem(type, amount.doubleValue(), data);
		
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getName()
	 */
	@Override
	public String getName() {
		return SIGN_NAME;
	}

	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getStatus()
	 */
	@Override
	public SignStatus getStatus() {

		if(item == null || item.getType() == null || item.getType() == Material.AIR || item.getData() == null || item.getAmount() == null) return SignStatus.INVALIDATED;
		
		if(EconomyConfiguration.config().getImportItem(item) == null) return SignStatus.INVALIDATED;
		
		// Export limit:
		if(getBuilding() instanceof TradingPost){
			
			if(((TradingPost) getBuilding()).checkOverExportLimit()) return SignStatus.DISABLED;
			
		}
		
		return SignStatus.ENABLED;
		
	}
	
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#getLine(int, org.saga.buildings.signs.BuildingSign.SignStatus)
	 */
	@Override
	public String getLine(int index, SignStatus status) {

		
		SagaPricedItem importItem = null;;
		Double price = null;
		
		switch (status) {
				
			case ENABLED:
				
				importItem = EconomyConfiguration.config().getImportItem(item);
				price = importItem.getPrice();
				
				if(index == 1) return item.getAmount() + AMOUNT_DIV_DISPLAY + GeneralMessages.materialAbrev(item.getType());
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				
				Double perc = 1.0;
				if(getBuilding() instanceof TradingPost){
					
					TradingPost tpost = (TradingPost) getBuilding();
					
					perc = (1 - tpost.getExportCoins() / tpost.getExportLimit()) * 100;
					
				}
				if(index == 3) return perc.intValue() + "%";
				
				break;
				
			case DISABLED:
				
				importItem = EconomyConfiguration.config().getImportItem(item);
				price = importItem.getPrice();
				
				if(index == 1) return item.getAmount() + AMOUNT_DIV_DISPLAY + GeneralMessages.materialAbrev(item.getType());
				if(index == 2) return "price: " + EconomyMessages.coins(price);
				if(index == 3) return "come back later";
				break;
			
			case INVALIDATED:
				
				if(index == 1) return SettlementConfiguration.config().invalidSignColor + "amt" + AMOUNT_DIV_DISPLAY + "item/ID";
			
				break;
				
			default:
				
				return "-";

		}

		return "";
		
		
	}
	
	

	// Interaction:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see org.saga.buildings.signs.BuildingSign#onRightClick(org.saga.player.SagaPlayer)
	 */
	@Override
	protected void onRightClick(SagaPlayer sagaPlayer) {
		
		
		SagaPricedItem exportItem = EconomyConfiguration.config().getExportItem(item);
		Double price = exportItem.getPrice();
		
		SagaItem request = new SagaItem(item);
		
		// Take item:
		SagaItem takenItem = sagaPlayer.takeItem(request);
		if(takenItem.getAmount() == 0){
			sagaPlayer.message(EconomyMessages.insufItems(item.getType()));
			return;
		}
		
		// Add coins:
		Double cost = price*takenItem.getAmount();
		EconomyDependency.addCoins(sagaPlayer, cost);
		
		// Inform:
		sagaPlayer.message(EconomyMessages.sold(item.getType(), takenItem.getAmount().intValue(), price));

		// Notify transaction:
		if(getBuilding() instanceof TradingPost){
			((TradingPost) getBuilding()).notifyExport(price * takenItem.getAmount());
		}
		
		// Statistics:
		StatisticsManager.manager().addExport(sagaPlayer, item.getType(), takenItem.getAmount().intValue(), cost);
		
		// Update sign:
		refresh();

		
	}
	
	
}
