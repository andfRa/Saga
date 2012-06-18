package org.saga.economy;

import org.bukkit.Material;
import org.saga.SagaLogger;
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
			SagaLogger.nullField(this, "type");
			integrity = false;
		}
		
		if(material == null){
			material = Material.AIR;
			SagaLogger.nullField(this, "material");
			integrity = false;
		}

		if(value == null){
			value = 0.0;
			SagaLogger.nullField(this, "value");
			integrity = false;
		}
		
		if(amount == null){
			amount = 1;
			SagaLogger.nullField(this, "amount");
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
