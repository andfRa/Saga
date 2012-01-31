package org.saga.economy;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.config.EconomyConfiguration;

public class TradeDeal implements Comparable<TradeDeal>{

	
	/**
	 * ID.
	 */
	private Integer id;

	/**
	 * Trade deal type.
	 */
	private TradeDealType type;
	
	/**
	 * Material.
	 */
	private Material material;

	/**
	 * Amount per transaction.
	 */
	private Integer amount;
	
	/**
	 * Total transactions left before expiration.
	 */
	private Integer trnscLeft;
	
	/**
	 * Value.
	 */
	private Double value;
	
	/**
	 * Time left until expiration in days.
	 */
	private Integer daysLeft;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	@SuppressWarnings("unused")
	private TradeDeal() {
	}
	
	/**
	 * Initializes.
	 * 
	 * @param type type
	 * @param material material
	 * @param amount amount
	 * @param transactionsLeft total transactions before expiration
	 * @param totalValue value
	 * @param daysLeft days before expiration
	 */
	public TradeDeal(TradeDealType type, Material material, Integer amount, Integer transactionsLeft, Double totalValue, Integer daysLeft) {
		
		
		super();
		
		this.id = -1;
		this.type = type;
		this.material = material;
		this.amount = Math.abs(amount);
		this.trnscLeft = Math.abs(transactionsLeft);
		this.daysLeft = Math.abs(daysLeft);
		this.value = Math.abs(totalValue);
		
		
	}

	/**
	 * Completes.
	 * 
	 * @throws TradeDealException when type is null
	 */
	public boolean complete() throws TradeDealException{
		
		
		boolean integrity = true;
		
		if(id == null){
			Saga.severe(TradeDeal.class, "failed to initialize id field", "setting default");
			id = -1;
		}
		
		if(type == null){
			Saga.severe(TradeDeal.class, "failed to initialize type field", "stopping complete");
			throw new TradeDealException("type null");
		}
	
		if(material == null){
			material = Material.AIR;
			Saga.severe(TradeDeal.class, "failed to initialize material field", "setting default");
			integrity = false;
		}
		
		if(amount == null){
			amount = Integer.MAX_VALUE;
			Saga.severe(TradeDeal.class, "failed to initialize amount field", "setting default");
			integrity = false;
		}
		
		if(trnscLeft == null){
			trnscLeft = 0;
			Saga.severe(TradeDeal.class, "failed to initialize trnscLeft field", "setting default");
			integrity = false;
		}
		
		if(value == null){
			value = 0.0;
			Saga.severe(TradeDeal.class, "failed to initialize value field", "setting default");
			integrity = false;
		}
		
		if(daysLeft == null){
			daysLeft = 0;
			Saga.severe(TradeDeal.class, "failed to initialize daysLeft field", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
	}
	
	/**
	 * Returns a random normal distributed trade deal based on the previous one.
	 * 
	 * @return random trade deal
	 */
	public TradeDeal createRandomTradeDeal() {

		
		return new TradeDeal(this.type, this.material,
				EconomyConfiguration.nextGaussian(this.amount, EconomyConfiguration.config().dealAmountSpread),
				EconomyConfiguration.nextGaussian(this.trnscLeft, EconomyConfiguration.config().dealTransactionsSpread),
				EconomyConfiguration.nextGaussian(this.value, EconomyConfiguration.config().dealValueSpread),
				EconomyConfiguration.nextGaussian(this.daysLeft, EconomyConfiguration.config().dealDaysLeftSpread));
		
		
	}
	
	
	// Interaction:
	/**
	 * Decreases days left.
	 * 
	 */
	public void nextDay() {
		daysLeft --;
	}
	
	/**
	 * Decreases left transactions.
	 * 
	 */
	public void doTransaction() {
		trnscLeft --;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public TradeDealType getType() {
		return type;
	}

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public Material getMaterial() {
		return material;
	}

	/**
	 * Gets the amount.
	 * 
	 * @return the amount
	 */
	public Integer getAmount() {
		return amount;
	}

	/**
	 * Gets the transactionsLeft.
	 * 
	 * @return the transactionsLeft
	 */
	public Integer getTransactionsLeft() {
		return trnscLeft;
	}

	/**
	 * Gets the value of the transaction.
	 * 
	 * @return the total value
	 */
	public Double getValue() {
		return value;
	}
	
	/**
	 * Gets the total value of the transaction.
	 * 
	 * @return the total value
	 */
	public Double getTotalValue() {
		return value * amount;
	}

	/**
	 * Gets the daysLeft.
	 * 
	 * @return the daysLeft
	 */
	public Integer getDaysLeft() {
		return daysLeft;
	}

	/**
	 * Gets the ID.
	 * 
	 * @return the ID
	 */
	public Integer getId() {
		return id;
	}
	

	/**
	 * Sets the ID.
	 * 
	 * @param id the ID to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	

	// Other:
	/* 
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(TradeDeal o) {
		return new Double(getTotalValue() - o.getTotalValue()).intValue();
	}

	@Override
	public String toString() {
		return id + ": " + type + " " + amount + " " + material + " for " + value;
	}

	/**
	 * Trade deal type.
	 * 
	 * @author andf
	 *
	 */
	public static enum TradeDealType{
		
		EXPORT,
		IMPORT;
		
		/**
		 * Gets the type name.
		 * 
		 * @return type name
		 */
		public String getName() {
			return name().replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2").toLowerCase();
		}
		
	}
	
	public static class TradeDealException extends Exception{
		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		
		
		public TradeDealException(String message) {
			super(message);
		}
		
	}
	
}
