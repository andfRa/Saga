package org.saga.player;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.SagaLogger;
import org.saga.config.GeneralConfiguration;
import org.saga.messages.PlayerMessages;
import org.saga.statistics.StatisticsManager;


public class GuardianRune {
	

	/**
	 * True if the rune is charged.
	 */
	private Boolean charged;
	
	/**
	 * Items contained.
	 */
	private ItemStack[] items;
	
	/**
	 * Armour contained.
	 */
	private ItemStack[] armour;

	/**
	 * Experience contained.
	 */
	private Float exp;

	/**
	 * Levels contained.
	 */
	private Integer levels;
	
	/**
	 * True if enabled.
	 */
	private Boolean enabled;
	

	
	
	// Initialisation:
	/**
	 * Creates a guardian rune.
	 * 
	 * @param sagaPlayer saga player
	 */
	public GuardianRune(SagaPlayer sagaPlayer) {

		this.armour =  new ItemStack[0];
		this.items =  new ItemStack[0];
		this.exp = (float) 0;
		this.levels = 0;
		this.levels = 0;
		this.enabled = true;
		this.charged = true;
		
	}

	/**
	 * Goes trough all the fields and makes sure everything has been set after gson load.
	 * If not, it fills the field with defaults.
	 * 
	 * @return true if everything was correct.
	 */
	public boolean complete(){
		
		
		boolean integrity = true;
		
		// Fields:
		if(exp == null){
			exp = (float) 0;
			SagaLogger.nullField(getClass(), "exp");
			integrity = false;
		}
		
		if(levels == null){
			levels = 0;
			SagaLogger.nullField(getClass(), "levels");
			integrity = false;
		}
		
		if(charged == null){
			charged = false;
			SagaLogger.nullField(getClass(), "charged");
			integrity = false;
		}
		
		if(items == null){
			items = new ItemStack[0];
			SagaLogger.nullField(getClass(), "items");
			integrity = false;
		}
		
		if(armour == null){
			armour = new ItemStack[0];
			SagaLogger.nullField(getClass(), "armor");
			integrity = false;
		}
		
		if(enabled == null){
			enabled = true;
			SagaLogger.nullField(getClass(), "enabled");
			integrity = false;
		}
		
		return integrity;
		

	}
	
	
	
	
	// Usage:
	/**
	 * Uses the guardian stone.
	 * 
	 * @param player player
	 */
	public void absorb(Player player) {

		
		this.charged = false;
		this.items =  player.getInventory().getContents();
		this.armour =  player.getInventory().getArmorContents();
		this.exp = player.getExp();
		this.levels = player.getLevel();
		
		
	}
	
	/**
	 * Restores items from the guardian rune.
	 * 
	 * @param player player
	 */
	@SuppressWarnings("deprecation")
	public void restore(Player player) {

		
		try {

			// Items and armour:
			player.getInventory().setContents(items);
			player.getInventory().setArmorContents(armour);
			
			// Levels and exp:
			player.setLevel(levels);
			player.setExp(exp);
			
		}
		catch (Exception e) {
			SagaLogger.severe(this, "guardian rune failure: " + e.getClass().getSimpleName() + ":" + e.getMessage());
			e.printStackTrace();
		}
		
		// Update inventory:
		player.updateInventory();
		
		
	}

	/**
	 * Checks if the stone is empty.
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return levels <= 0 && exp <= 0 && items.length <= 0 && armour.length <= 0;
	}
	
	/**
	 * Clears the rune.
	 * 
	 */
	private void clear() {

		items = new ItemStack[0];
		armour = new ItemStack[0];
		levels = 0;
		exp = (float) 0;

	}
	
	/**
	 * Enables or disables the stone.
	 * 
	 * @param enabled true if enabled
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	/**
	 * Gets if enabled.
	 * 
	 * @return true if enabled
	 */
	public Boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Gets the enchant exp.
	 * 
	 * @return the exp
	 */
	public Float getExp() {
		return exp;
	}
	
	/**
	 * Gets the enchant level points.
	 * 
	 * @return enchant level points
	 */
	public Integer getLevel() {
		return levels;
	}
	
	
	
	
	// Getters:
	/**
	 * Gets the items.
	 * 
	 * @return the items
	 */
	public ItemStack[] getItems() {
	
	
		return items;
	}

	/**
	 * Gets the armor.
	 * 
	 * @return the armor
	 */
	public ItemStack[] getArmour() {
	
	
		return armour;
	}	

	
	
	
	// Charge:
	/**
	 * Checks if the rune is charged.
	 * 
	 * @return true if charged
	 */
	public Boolean isCharged() {
		return charged;
	}
	

	/**
	 * Recharges the rune.
	 * 
	 */
	public void recharge() {
		charged = true;
	}
	
	/**
	 * Charges the rune.
	 * 
	 */
	public void discharge() {
		charged = false;
	}
	
	
	
	
	// Other:
	@Override
	public String toString() {
		return items.toString();
	}

	/**
	 * Counts items.
	 * 
	 * @param items items
	 * @return item count
	 */
	public static int countItems(ItemStack[] items) {


		int count = 0;
		for (int i = 0; i < items.length; i++) {
			if(items[i] != null && items[i].getType() != Material.AIR)
				count += items[i].getAmount();
		}
		return count;

	}
	
	
	// Absorb and restore:
	/**
	 * Handles rune restoration after death.
	 * 
	 * @param sagaPlayer player
	 * @param event event
	 */
	public static void handleRestore(SagaPlayer sagaPlayer){
		
		
		try {
			
			Player player = sagaPlayer.getPlayer();
			GuardianRune rune = sagaPlayer.getGuardRune();
			
			// Rune disabled:
			if(!GeneralConfiguration.config().isRuneEnabled()) return;
			
			// Rune disabled in the world:
			if(!GeneralConfiguration.config().isRuneEnabled(sagaPlayer.getLocation().getWorld())) return;
			
			if(rune.isEmpty()) return;

			// Restore:
			rune.restore(player);
			
			// Inform:
			sagaPlayer.message(PlayerMessages.restored(rune));
			
			// Clear:
			rune.clear();
			
			// Statistics:
			StatisticsManager.manager().addGuardRuneRestore(sagaPlayer);

		}
		catch (Throwable e) {
			StatisticsManager.manager().addGuardRuneRestoreException(sagaPlayer);
		}
		
		
	}
	
	/**
	 * Handles rune absorption on death.
	 * 
	 * @param sagaDead player
	 * @param event event
	 * @return true if absorbed
	 */
	public static boolean handleAbsorb(SagaPlayer sagaDead, EntityDeathEvent event){
		
		
		GuardianRune rune = sagaDead.getGuardRune();
		Player player = sagaDead.getPlayer();

		// Rune disabled:
		if(!GeneralConfiguration.config().isRuneEnabled()) return false;
		
		// Rune disabled in the world:
		if(!GeneralConfiguration.config().isRuneEnabled(player.getLocation().getWorld())) return false;
		
		// Nothing to absorb:
		if(countItems(player.getInventory().getContents()) <= 0 && countItems(player.getInventory().getArmorContents()) <= 0 && player.getLevel() <= 0 && player.getExp() <= 0){
			return false;
		}
		
		// Not empty:
		if(!rune.isEmpty()){
			sagaDead.message(PlayerMessages.notEmpty(rune));
			return false;
		}

		// Not charged:
		if(!rune.isCharged()){
			sagaDead.message(PlayerMessages.notCharged(rune));
			sagaDead.message(PlayerMessages.notChargedInfo(rune));
			return false;
		}
		
		// Absorb:
		rune.absorb(player);
		
		// Remove drops:
		List<ItemStack> drops = event.getDrops();
		drops.clear();
		event.setDroppedExp(0);
		
		// Discharge:
		rune.discharge();
	
		return true;
		
		
	}
	
	
}
