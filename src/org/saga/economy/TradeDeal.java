package org.saga.economy;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;

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
	 * Price.
	 */
	private Double price;
	
	/**
	 * Time left until expiration in days.
	 */
	private Integer days;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	@SuppressWarnings("unused")
	private TradeDeal() {
	}
	
	/**
	 * Creates a trade deal.
	 * 
	 * @param type deal type
	 * @param material material
	 * @param value value
	 * @param amount amount
	 * @param days days
	 */
	public TradeDeal(TradeDealType type, Material material, Double value, Integer amount, Integer days) {
		
		
		super();
		
		this.id = -1;
		this.type = type;
		this.material = material;
		this.price = Math.abs(value);
		this.amount = Math.abs(amount);
		this.days = Math.abs(days);
		
		
	}

	/**
	 * Completes.
	 */
	public boolean complete(){
		
		
		boolean integrity = true;
		
		if(id == null){
			id = -1;
			SagaLogger.nullField(this, "id");
			integrity = false;
		}
		
		if(type == null){
			type = TradeDealType.EXPORT;
			SagaLogger.nullField(this, "type");
			integrity = false;
		}
	
		if(material == null){
			material = Material.AIR;
			SagaLogger.nullField(this, "material");
			integrity = false;
		}
		
		if(amount == null){
			amount = Integer.MAX_VALUE;
			SagaLogger.nullField(this, "amount");
			integrity = false;
		}
		
		if(price == null){
			price = 0.0;
			SagaLogger.nullField(this, "price");
			integrity = false;
		}
		
		if(days == null){
			days = 0;
			SagaLogger.nullField(this, "daysLeft");
			integrity = false;
		}
		
		return integrity;
		
		
	}
	
	
	// Interaction:
	/**
	 * Decreases days left.
	 * 
	 */
	public void nextDay() {
		days --;
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
	 * Gets the cost of the deal.
	 * 
	 * @return the total value
	 */
	public Double getPrice() {
		return price;
	}
	
	/**
	 * Gets the total cost of the deal. Uses decreased amount if it is too big.
	 * 
	 * @return total cost
	 */
	public Double getTotalCost() {
		
		return price*amount;
		
	}

	/**
	 * Gets the total cost of the deal. Uses decreased amount if it is too big.
	 * 
	 * @param amount amount of items
	 * @return total cost
	 */
	public Double getTotalCost(Integer amount) {
		
		if(amount > this.amount) amount = this.amount;
		return price*amount;
		
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
	 * Gets the total value of the transaction.
	 * 
	 * @return the total value
	 */
	public Double getTotalValue() {
		return price * amount;
	}

	/**
	 * Gets the daysLeft.
	 * 
	 * @return the daysLeft
	 */
	public Integer getDaysLeft() {
		return days;
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
	
	
	// Deal:
	/**
	 * Does a trade deal.
	 * 
	 * @param trader trader
	 * @return price of items exchanged
	 */
	public Double doDeal(Trader trader) {

		
		if(amount < 1) return 0.0;
		
		ItemStack item = null;
		Integer usedAmount = null;
		
		switch (type) {
			case IMPORT:
				
				usedAmount = amount;
				
				while(usedAmount * price > trader.getCoins()){
					usedAmount --;
				}
				
				if(usedAmount < 1) return 0.0;
				
				item = new ItemStack(material, usedAmount);
				
				// Do deal:
				trader.removeCoins(usedAmount * price);
				trader.addItem(item);
				amount -= usedAmount;
				
				return usedAmount * price;
				
			case EXPORT:

				usedAmount = amount;
				
				if(usedAmount > trader.getAmount(material)) usedAmount = trader.getAmount(material);
				
				if(usedAmount < 1) return 0.0;
				
				item = new ItemStack(material, usedAmount);
				
				// Do deal:
				trader.addCoins(usedAmount * price);
				trader.removeItem(item);
				amount -= usedAmount;
				
				return usedAmount*price;
				
			default:

				return 0.0;
		
		}
		
		
	}
	
	/**
	 * Checks if the deal is completed.
	 * 
	 * @return true if completed
	 */
	public boolean isCompleted() {

		return amount < 1;

	}
	
	/**
	 * Checks if the deal is expired.
	 * 
	 * @return true if expired
	 */
	public boolean isExpired() {
		
		return days < 1;

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

	
}
