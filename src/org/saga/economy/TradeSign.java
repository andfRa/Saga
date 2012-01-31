package org.saga.economy;


import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;
import org.saga.economy.EconomyManager.TransactionType;
import org.saga.player.SagaPlayer;



public class TradeSign {

	
	/**
	 * Type.
	 */
	private TransactionType type;
	
	/**
	 * Material.
	 */
	private Material material;

	/**
	 * Trader.
	 */
	transient private Trader trader;
	
	/**
	 * Trading sign status.
	 */
	private TradeSignSatus status;
	
	/**
	 * World.
	 */
	private String world;
	
	/**
	 * X coordinate.
	 */
	private Integer x;
	
	/**
	 * Y coordinate.
	 */
	private Integer y;
	
	/**
	 * Z coordinate.
	 */
	private Integer z;
	
	/**
	 * Location.
	 */
	transient private Location location;

	/**
	 * Sign.
	 */
	transient private Sign sign;
	
	/**
	 * Transaction.
	 */
	transient private Transaction transaction = null;
	
	
	// Initialization:
	/**
	 * Creates a economy sign.
	 * 
	 * @param type transaction type
	 * @param sign sign
	 * @param trader trader
	 * @param event event that created the sign
	 */
	private TradeSign(TransactionType type, Sign sign, Trader trader, SignChangeEvent event){

		this.type = type;
		this.location = sign.getBlock().getLocation();
		this.status = TradeSignSatus.OPEN;
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.sign = sign;
		this.trader = trader;
		
		// Match material:
		material = Material.matchMaterial(event.getLine(1));
		
		if(material == null){
			
			try {
				Integer id = Integer.parseInt(event.getLine(1));
				material = Material.getMaterial(id);
			} catch (NumberFormatException e) {
			}
			
		}
		
		if(material == null || material.equals(Material.AIR)){
			this.status = TradeSignSatus.CLOSED;
			this.type = TransactionType.INVALID;
			this.material = Material.AIR;
		}
		
		
	}

	public boolean complete(Trader trader) throws TradeSignException{
		
		
		boolean integrity = true;
		
		if(type == null){
			type = TransactionType.INVALID;
			Saga.severe(this, "failed to initialize type field", "setting default");
			integrity = false;
		}
		if(material == null){
			material = Material.AIR;
			Saga.severe(this, "failed to initialize requestMaterial field", "setting default");
			integrity = false;
		}
		if(status== null){
			status = TradeSignSatus.CLOSED;
			Saga.severe(this, "failed to initialize status field", "setting default");
		}
		if(world== null){
			Saga.severe(this, "failed to initialize world field", "stopping complete");
			throw new TradeSignException("world null");
		}
		if(x== null){
			Saga.severe(this, "failed to initialize x field", "stopping complete");
			throw new TradeSignException("x null");
		}
		if(y== null){
			Saga.severe(this, "failed to initialize y field", "stopping complete");
			throw new TradeSignException("y null");
		}
		if(z== null){
			Saga.severe(this, "failed to initialize z field", "stopping complete");
			throw new TradeSignException("z null");
		}
		World serverWorld = Saga.plugin().getServer().getWorld(this.world);
		if(serverWorld == null){
			Saga.severe(this, "failed to retrieve world", "stopping complete");
			throw new TradeSignException("invalid world");
		}
		location = new Location(serverWorld, x, y, z);
		try {
			sign = (Sign) location.getBlock().getState();
		} catch (Exception e) {
			throw new TradeSignException(e.getClass().getSimpleName() + ":" + e.getMessage() + " for sign retrieve");
		}
		
		this.trader = trader;
		
		return integrity;
		
		
	}	
	
	/**
	 * Refreshes the sign and transaction data.
	 * 
	 */
	public void refresh() {
		
		
		transaction = findTransaction();

		// Close open sign if no transaction is found:
		if(transaction == null && status.equals(TradeSignSatus.OPEN)){
			status = TradeSignSatus.CLOSED;
		}

		// Open sign if transaction is found:
		if(transaction != null && status.equals(TradeSignSatus.CLOSED)){
			status = TradeSignSatus.OPEN;
		}
		
		
		// Opened:
		if(status.equals(TradeSignSatus.OPEN)){
			
			// Sell:
			if(type.equals(TransactionType.SELL)){
				
				// Title:
				sign.setLine(0, EconomyConfiguration.config().openSellSignTitle);
				
				// Sell:
				sign.setLine(1, "" + transaction.getAmount() + " " + EconomyMessages.materialShort(material));
				
				// For:
				sign.setLine(2, "" + EconomyMessages.coins(transaction.getValue()) + " each");
				
				// Stock:
				sign.setLine(3, "" + EconomyMessages.coins(trader.getCoins()) + " banked");
				
			}else

			// Buy:
			if(type.equals(TransactionType.BUY)){
				
				// Title:
				sign.setLine(0, EconomyConfiguration.config().openBuySignTitle);
				
				// Buy:
				sign.setLine(1, "" + transaction.getAmount() + " " + EconomyMessages.materialShort(material));
				
				// For:
				sign.setLine(2, "for " + EconomyMessages.coins(transaction.getValue()) + " each");
				
				// Stock:
				sign.setLine(3, "" + trader.getItemCount(transaction.getMaterial()) + " stored");
				
			}
			
			// Invalid:
			else{

				sign.setLine(0, EconomyConfiguration.config().invalidSignTitle);
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				
			}
			
		}else
		// Closed:	
		if(status.equals(TradeSignSatus.CLOSED)){
			

			// Sell:
			if(type.equals(TransactionType.SELL)){
				
				// Title:
				sign.setLine(0, EconomyConfiguration.config().closedSellSignTitle);
				
				// Sell:
				sign.setLine(1, "sell "+ EconomyMessages.materialShort(material));
				
				// For:
				sign.setLine(2, "for " + "-");
				
				// Stock:
				sign.setLine(3, "stock: " + "-");
				
			}else

			// Buy:
			if(type.equals(TransactionType.BUY)){
				
				// Title:
				sign.setLine(0, EconomyConfiguration.config().closedBuySignTitle);
				
				// Buy:
				sign.setLine(1, "buy " + EconomyMessages.materialShort(material));
				
				// For:
				sign.setLine(2, "for " + "-");
				
				// Stock:
				sign.setLine(3, "stock: " + "-");
				
			}
			
			// Invalid:
			else{
				
				sign.setLine(0, EconomyConfiguration.config().invalidSignTitle);
				sign.setLine(1, "");
				sign.setLine(2, "");
				sign.setLine(3, "");
				
			}
			
		}
		// Invalid:
		else{
			
			sign.setLine(0, EconomyConfiguration.config().invalidSignTitle);
			sign.setLine(1, "");
			sign.setLine(2, "");
			sign.setLine(3, "");
			
		}

		// TODO: Hackish workaround for signs not updating. Fix when Bukkit fixes
		// Author TheDgtl
		CraftWorld cw = (CraftWorld)sign.getWorld();
		cw.getHandle().notify(sign.getX(), sign.getY(), sign.getZ());
		
//		
//		// Update:
//		sign.update();
//		

	}
	
	/**
	 * Creates a economy sign.
	 * 
	 * @param type transaction type
	 * @param sign sign
	 * @param trader trader
	 * @param event event that created the sign
	 */
	public static TradeSign create(TransactionType type, Sign sign, Trader trader, SignChangeEvent event){

		TradeSign ecoSign = new TradeSign(type, sign, trader, event);
		ecoSign.refresh();
		
		return ecoSign;
		
	}
	
	/**
	 * Deletes the sign.
	 * 
	 */
	public void remove(){


		if(!location.getBlock().getType().equals(Material.SIGN_POST)){
			Saga.severe(this, "tried to remove an non existant sign from the world", "ignoring remove from world request");
			return;
		}

		
	}
	
	
	// Interaction:
	/**
	 * Checks if the sign title is for a sell sign.
	 * 
	 * @param signTitle sign title
	 * @return true if sell sign
	 */
	public static boolean isSellSign(String signTile) {
		
		return signTile.equalsIgnoreCase("=[SELL]=");
		
	}
	
	/**
	 * Checks if the sign title is for a buy sign.
	 * 
	 * @param signTitle sign title
	 * @return true if buy sign
	 */
	public static boolean isBuySign(String signTile) {
		
		return signTile.equalsIgnoreCase("=[BUY]=");
		
	}

	/**
	 * Gets the transaction for the given sign.
	 * 
	 * @return transaction, null if not found
	 */
	private Transaction findTransaction() {

		
		ArrayList<Transaction> transactions = trader.getTransactions();
		for (int i = 0; i < transactions.size(); i++) {
			if( transactions.get(i).getMaterial().equals(material) && transactions.get(i).getType().equals(type)){
				return transactions.get(i);
			}
		}
		return null;
		
		
	}

	/**
	 * Gets transaction.
	 * 
	 * @return transaction, null if none
	 */
	public Transaction getTransaction() {
		return transaction;
	}
	
	/**
	 * Gets the sign location.
	 * 
	 * @return sign location
	 */
	public Location getLocation() {
		return location;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public TransactionType getType() {
		return type;
	}
	
	/**
	 * Gets trading post status:
	 * 
	 * @return
	 */
	public TradeSignSatus getStatus() {
		return status;
	}

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}
	
	
	// Events:
	/**
	 * Called when the player interacts with the sign.
	 * 
	 * @param sagaPlayer player saga player
	 * @param event event event
	 * 
	 */
	public void onPlayerInteract(SagaPlayer sagaPlayer, PlayerInteractEvent event) {
		

		// Right click:
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
			return;
		}
		
		// Take control:
		event.setUseInteractedBlock(Result.DENY);
		event.setUseItemInHand(Result.DENY);

		// Transaction:
		EconomyManager.transaction(sagaPlayer, trader, getTransaction());
    	
		// Refresh:
		refresh();
		
		
	}
	

	// Other:
	/**
	 * Checks if the sign is a duplicate.
	 * 
	 * @param sign sign
	 * @return true if duplicate
	 */
	public boolean isDuplicateSign(TradeSign sign) {
		return this.sign.equals(sign);
	}
	
	
	public enum TradeSignSatus{
		
		
		OPEN,
		CLOSED;
		
		
	}
	
	public class TradeSignException extends Exception{

		public TradeSignException(String message) {
			super(message);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		
	}
	
}
