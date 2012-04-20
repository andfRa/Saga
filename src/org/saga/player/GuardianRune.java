package org.saga.player;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.saga.Saga;
import org.saga.config.ExperienceConfiguration;
import org.saga.economy.InventoryUtil;
import org.saga.messages.PlayerMessages;
import org.saga.statistics.StatisticsManager;


public class GuardianRune {
	

	/**
	 * Guardian stone status.
	 */
	private GuardianRuneStatus status;

	/**
	 * True if the rune is charged.
	 */
	private Boolean charged;
	
	/**
	 * Items contained.
	 */
	private Hashtable<Integer, ItemStack> items;
	
	/**
	 * Items contained.
	 */
	private Hashtable<Integer, ItemStack> armor;
	
	/**
	 * Experience contained.
	 */
	private Integer exp;
	
	/**
	 * True if enabled.
	 */
	private Boolean enabled;
	
	
	// Initialization:
	/**
	 * Creates a guardian stone.
	 * 
	 * @param items items stored
	 * @param exp experience stored
	 * @param status guardian stone status
	 */
	private GuardianRune(Hashtable<Integer, ItemStack> items, Hashtable<Integer, ItemStack>  armor, Integer exp, GuardianRuneStatus status) {

		this.armor = armor;
		this.items = items;
		this.exp = exp;
		this.status = status;
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
			exp = 0;
			Saga.severe(getClass(), "exp field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(charged == null){
			charged = false;
			Saga.severe(getClass(), "charged field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(items == null){
			items = new Hashtable<Integer, ItemStack>();
			Saga.severe(getClass(), "items field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(armor == null){
			armor = new Hashtable<Integer, ItemStack>();
			Saga.severe(getClass(), "armor field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(status == null){
			status = GuardianRuneStatus.FULL;
			Saga.severe(getClass(), "status field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(enabled == null){
			enabled = true;
			Saga.severe(getClass(), "enabled field failed to initialize", "setting default");
			integrity = false;
		}
		
		if(status.equals(GuardianRuneStatus.FULL)){
			status = GuardianRuneStatus.DISCHARGED;
		}else{
			status = GuardianRuneStatus.CHARGED;
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

		
		this.exp = ExperienceConfiguration.config().getTotalExperience(player);
		this.items = new Hashtable<Integer, ItemStack>();
		
		// Absorb items:
		ItemStack[] contents = player.getInventory().getContents();
		for (int i = 0; i < contents.length; i++) {
				
			putItem(i, contents[i]);
			
		}

		// Absorb armor:
		ItemStack[] armorContents = player.getInventory().getArmorContents();
		for (int i = 0; i < armorContents.length; i++) {
				
			putArmor(i, armorContents[i]);
			
		}
//		
//		// Use the stone:
//		if(exp.intValue() > 0 || items.size() > 0){
//			
//			return true;
//			
//		}else{
//			
//			return false;
//			
//		}
//		
		
	}
	
	/**
	 * Restores items from the guardian rune.
	 * 
	 * @param player player
	 */
	@SuppressWarnings("deprecation")
	public void restore(Player player) {

		
		ArrayList<ItemStack> remainingItems = new ArrayList<ItemStack>();
		
		// Add experience:
		for (int i = 0; i < exp; i++) {
			player.giveExp(1);
		}
		exp = 0;
		
		PlayerInventory inventory = player.getInventory();
		
		// Add items:
		for (int i = 0; i < inventory.getSize(); i++) {
			
			// Absorbed item:
			ItemStack absorbedItem = takeItem(i);
			if(absorbedItem == null) continue;
			
			// Only put in empty slots:
			if(!(inventory.getItem(i) == null || inventory.getItem(i).getType().equals(Material.AIR))){
				
				remainingItems.add(absorbedItem);
				continue;
				
			}
			
			// Put item:
			inventory.setItem(i, absorbedItem);
			
		}
		
		// Add armor:
		ItemStack[] armorContents = inventory.getArmorContents();
		for (int i = 0; i < armorContents.length; i++) {

			// Absorbed item:
			ItemStack absorbedItem = takeArmor(i);
			if(absorbedItem == null || absorbedItem.getType().equals(Material.AIR)) continue;

			// Only put in empty slots:
			if(!(armorContents[i] == null || armorContents[i].getType().equals(Material.AIR))){
				
				remainingItems.add(absorbedItem);
				continue;
				
			}
			
			// Put item:
			armorContents[i] = absorbedItem;
			
		}
		inventory.setArmorContents(armorContents);
		
		
		// Add left items:
		for (ItemStack remainingItem : remainingItems) {
			
			InventoryUtil.addItem(remainingItem, inventory, player.getLocation());
		
		}
		
//		// Discharge the stone:
//		status = GuardianRuneStatus.DISCHARGED;

		// Update inventory:
		player.updateInventory();
		
		
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
	 * Creates a new guardian stone.
	 * 
	 * @return guardian stone
	 */
	public static GuardianRune newStone() {

		return new GuardianRune(new Hashtable<Integer, ItemStack>(), new Hashtable<Integer, ItemStack>(), 0, GuardianRuneStatus.EMPTY);
		
	}
	
	
	// Interaction:
	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	public GuardianRuneStatus getStatus() {
		return status;
	}
	
	/**
	 * Checks if the stone is empty.
	 * 
	 * @return true if empty
	 */
	public boolean isEmpty() {
		return exp <= 0 && items.size() <= 0 && armor.size() <= 0;
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
	 * Gets the items.
	 * 
	 * @return the items
	 */
	public Hashtable<Integer, ItemStack> getItems() {
		return items;
	}

	/**
	 * Gets the armor.
	 * 
	 * @return the armor
	 */
	public Hashtable<Integer, ItemStack> getArmor() {
		return armor;
	}

	
	/**
	 * Gets the exp.
	 * 
	 * @return the exp
	 */
	public Integer getExp() {
		return exp;
	}
	
	/**
	 * Puts the item.
	 * 
	 * @param index index
	 * @param item item
	 */
	private void putItem(Integer index, ItemStack item) {

		
		if(item == null || item.getType().equals(Material.AIR)) return;
		
		items.put(index, item);
		
		
	}
	
	/**
	 * Puts the armor.
	 * 
	 * @param index index
	 * @param item armor
	 */
	private void putArmor(Integer index, ItemStack item) {

		
		if(item == null || item.getType().equals(Material.AIR)) return;
		
		armor.put(index, item);
		
		
	}
	
	/**
	 * Takes the item.
	 * 
	 * @param index index
	 * @return item taken, null if air or none
	 */
	private ItemStack takeItem(Integer index) {

		
		ItemStack item = items.remove(index);
		
		if(item == null || item.getType().equals(Material.AIR)) return null;
		
		return item;
		
		
	}
	
	/**
	 * Takes the armor.
	 * 
	 * @param index index
	 * @return armor taken, null if air or none
	 */
	private ItemStack takeArmor(Integer index) {


		ItemStack item = armor.remove(index);
		
		if(item == null || item.getType().equals(Material.AIR)) return null;
		
		return item;
		
		
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
	 * Guardian rune status.
	 * 
	 * @author andf
	 *
	 */
	public static enum GuardianRuneStatus{
		
		EMPTY("empty"),
		FULL("full"),
		BROKEN("broken"),
		
		CHARGED("charged"),
		DISCHARGED("discharged");
		
		/**
		 * Status name.
		 */
		private final String name;
		
		/**
		 * Sets status name.
		 * 
		 * @param name status name
		 */
		private GuardianRuneStatus(String name) {
			this.name = name;
		}
		
		/**
		 * Gets status name.
		 * 
		 * @return status name
		 */
		public String getStatusName() {
			return name;
		}
		
	}
	
	
	// Events:
	/**
	 * Handles rune restoration after death.
	 * 
	 * @param sagaPlayer player
	 * @param event event
	 */
	public static void handleRestore(SagaPlayer sagaPlayer){
		

		GuardianRune rune = sagaPlayer.getGuardianRune();
		if(sagaPlayer.getGuardianRune().isEmpty()) return;

		// Inform:
		sagaPlayer.message(PlayerMessages.restored(rune));
		
		// Restore:
		sagaPlayer.guardianRuneRestore();
		
		// Statistics:
		StatisticsManager.manager().onGuardanRuneRestore();

		
	}
	
	/**
	 * Handles rune absorption on death.
	 * 
	 * @param sagaDead player
	 * @param event event
	 * @return true if absorbed
	 */
	public static boolean handleAbsorb(SagaPlayer sagaDead, PlayerDeathEvent event){
		
		
		GuardianRune rune = sagaDead.getGuardianRune();
		
		// Nothing to absorb:
		if(!sagaDead.checkGuardRuneAbsorb()){
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
		sagaDead.guardianRuneAbsorb();
		
		// Remove drops:
		List<ItemStack> drops = event.getDrops();
		drops.clear();
		event.setDroppedExp(0);
		
		// Discharge:
		rune.discharge();
	
		// Indicate:
		sagaDead.onGuardRuneAbsorption();
		
		return true;
		
		
	}
	
	
}
