package org.saga.economy;

import org.bukkit.Material;
import org.saga.Saga;
import org.saga.economy.EconomyManager.TransactionType;


public class Transaction {

	
	/**
	 * Type.
	 */
	private TransactionType type;
	
	/**
	 * Material.
	 */
	private Material material;

	/**
	 * Value.
	 */
	private Double value;
	
	/**
	 * Amount.
	 */
	private Integer amount;
	

	// Initialization:
	/**
	 * Initializes.
	 * 
	 * @param type transaction type
	 * @param material material offered
	 * @param amount amount
	 * @param value offered amount
	 */
	public Transaction(TransactionType type, Material material, Integer amount, Double value) {
		super();
		this.type = type;
		this.amount = amount;
		this.material = material;
		this.value = value;
	}
	
	/**
	 * Checks all fields and fill ins the default values.
	 * 
	 * @return integrity
	 */
	public boolean complete(){
		
		
		boolean integrity = true;
		
		// Fields:
		if(type == null){
			type = TransactionType.INVALID;
			Saga.severe(this, "failed to initialize type field", "setting default");
			integrity = false;
		}
		
		if(material == null){
			material = Material.AIR;
			Saga.severe(this, "failed to initialize material field", "setting default");
			integrity = false;
		}

		if(value == null){
			value = 0.0;
			Saga.severe(this, "failed to initialize value field", "setting default");
			integrity = false;
		}
		
		if(amount == null){
			amount = 1;
			Saga.severe(this, "failed to initialize amount field", "setting default");
			integrity = false;
		}
		
		return integrity;
		
		
	}


	// Interaction:
	/**
	 * Gets transaction type.
	 * 
	 * @return transaction type
	 */
	public TransactionType getType() {
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
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public Double getValue() {
		return value;
	}
	
	/**
	 * Gets the total value.
	 * 
	 * @return the total value
	 */
	public Double getTotalValue() {
		return value*amount;
	}

	/**
	 * Gets the value.
	 * 
	 * @param newAmount new amount
	 * @return the value
	 */
	public Double getValue2(Integer newAmount) {
		return value * newAmount.doubleValue() / amount.doubleValue();
	}

	/**
	 * Gets amount.
	 * 
	 * @return amount
	 */
	public Integer getAmount() {
		return amount;
	}
	
	/**
	 * Gets amount.
	 * 
	 * @return amount
	 */
	public Integer getAmount(Double newValue) {
		return new Double(amount.doubleValue() * newValue / value).intValue();
	}
	
	/**
	 * Duplicates the transaction.
	 * 
	 * @return duplicated version
	 */
	public Transaction duplicate() {

		return new Transaction(type, material, amount, value);
		
	}
	
	// Other:
	@Override
	public String toString() {
		return type + " " + amount + " " + material + " for " + value;
	}
	

}
