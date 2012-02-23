package org.saga.player;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.saga.Saga;
import org.saga.utility.TwoPointFunction;

public class Skill {

	
	/**
	 * Random generator.
	 */
	private static Random random = new Random();

	/**
	 * Name.
	 */
	private String name;
	
	/**
	 * Attribute type.
	 */
	private SkillType type;
	
	/**
	 * Attack items that trigger the attribute use.
	 */
	private ArrayList<Material> attackerWeapons;

	/**
	 * Defense items that trigger the attribute use.
	 */
	private ArrayList<Material> defenderWeapons;
	
	/**
	 * Armor that triggers the attribute use.
	 */
	private ArmourType attackerArmor;

	/**
	 * Armor that triggers the attribute use.
	 */
	private ArmourType defenderArmor;
	
	
	/**
	 * Attacked type.
	 */
	private EntityType attacker;

	/**
	 * Attacked type.
	 */
	private EntityType defender;

	/**
	 * Attack type.
	 */
	private AttackType attack;
	
	/**
	 * Level function.
	 */
	private TwoPointFunction modifier;

	
	/**
	 * Forwards the name and sets use materials.
	 * 
	 * @param isAttack sets attack if true defense otherwise
	 * @param name name
	 * @param triggerItems materials that are required for use.
	 * @param attackerType attacker type
	 * @param attackedType attacked type
	 * @param attackType attack type
	 * @param displayType display type
	 */
	public Skill(Boolean isAttack, String name, ArrayList<Material> triggerItems, EntityType attackerType, EntityType attackedType, AttackType attackType) {
		
		
		this.attackerWeapons = triggerItems;
		this.attacker = attackerType;
		this.defender = attackerType;
		this.attack = attackType;
		
		
	}
	
	/**
	 * Completes the attribute.
	 * 
	 * @return integrity
	 */
	public boolean complete() {

		
		boolean integrity = true;

		if(name == null){
			name = "none";
			Saga.severe(this, "failed to intialize name field", "setting default");
			integrity = false;
		}
		
		if(type == null){
			type = SkillType.INVALID;
			Saga.severe(this, "failed to intialize type field", "setting default");
			integrity = false;
		}
		
		if(attackerWeapons == null){
			attackerWeapons = new ArrayList<Material>();
			Saga.severe(this, "failed to intialize attackerWeapons field", "setting default");
			integrity = false;
		}
		
		if(defenderWeapons == null){
			defenderWeapons = new ArrayList<Material>();
			Saga.severe(this, "failed to intialize defenderWeapons field", "setting default");
			integrity = false;
		}
		
		if(attackerArmor == null){
			attackerArmor = ArmourType.NONE;
			Saga.severe(this, "failed to intialize attackerArmor field", "setting default");
			integrity = false;
		}

		if(defenderArmor == null){
			defenderArmor = ArmourType.NONE;
			Saga.severe(this, "failed to intialize defenderArmor field", "setting default");
			integrity = false;
		}
		
		if(defender == null){
			defender = EntityType.ALL;
			Saga.severe(this, "failed to intialize attackedType field", "setting default");
			integrity = false;
		}
		
		if(attacker == null){
			attacker = EntityType.ALL;
			Saga.severe(this, "failed to intialize attackerType field", "setting default");
			integrity = false;
		}
		
		if(attack == null){
			attack = AttackType.NONE;
			Saga.severe(this, "failed to intialize attack field", "setting default");
			integrity = false;
		}
		
		if(modifier == null){
			modifier = new TwoPointFunction(0.0);
			Saga.severe(this, "failed to intialize modifier field", "setting default");
			integrity = false;
		}
		integrity = modifier.complete() && integrity;
		
		return integrity;
		
		
	}
	
	
	// Attribute usage:
	/**
	 * Modifies the attack.
	 * 
	 * @param multiplier attribute level
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	private void modifyAttack(Integer multiplier, SagaPlayer sagaPlayer, EntityDamageByEntityEvent event) {

		
		// Level:
		if(multiplier == 0){
			return;
		}
		
		// Calculate damage:
		int damageMod = randomRound(modifier.value(multiplier.shortValue()));
		Integer damage = event.getDamage() + damageMod;
		
		// Only positive damage:
		if(damage < 1){
			damage = 1;
		}

		if(damageMod > 0){
			sagaPlayer.message(ChatColor.DARK_GREEN + getName() + ": +" + new Double(0.5 * damageMod) + "dmg");
		}
		else if(damageMod < 0){
			sagaPlayer.message(ChatColor.DARK_GREEN + getName() + ": " + new Double(0.5 * damageMod) + "dmg");
		}
		
		// Set damage:
		event.setDamage(damage);

		
	}
	
	/**
	 * Modifies the defense.
	 * 
	 * @param multiplier attribute level
	 * @param sagaPlayer saga player
	 * @param event event
	 */
	private void modifyDefense(Integer multiplier, SagaPlayer sagaPlayer, double armor, EntityDamageByEntityEvent event) {

		
		// Level:
		if(multiplier == 0){
			return;
		}
		
		// Calculate damage:
		int damageMod = randomRound(modifier.value(multiplier.shortValue()) * armor);
		Integer damage = event.getDamage() + damageMod;
		
		// Only positive damage:
		if(damage < 1){
			damage = 1;
		}

		if(damageMod > 0){
			sagaPlayer.message(ChatColor.GREEN + getName() + ": +" + new Double(0.5 * damageMod) + "dmg");
		}
		else if(damageMod < 0){
			sagaPlayer.message(ChatColor.GREEN + getName() + ": " + new Double(0.5 * damageMod) + "dmg");
		}
		
		// Set damage:
		event.setDamage(damage);

		
	}
	
	
	// Triggers:
	/**
	 * Called when the player gets hit by a creature.
	 * 
	 * @param event event
	 */
	public void triggerHitByCreature(SagaPlayer defenderPlayer, Integer multiplier, EntityDamageByEntityEvent event, Creature creature) {

		
		// Proceed if type is correct:
		if(!type.equals(SkillType.DEFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!attacker.equals(EntityType.CREATURE) && !attacker.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MELEE) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyDefense(multiplier, defenderPlayer,defenderArmour, event);
		
		
	}
	
	/**
	 * Called when the player damages a creature.
	 * 
	 * @param event event
	 */
	public void triggerHitCreature(SagaPlayer attackerPlayer,  Integer multiplier, EntityDamageByEntityEvent event, Creature creature) {


		// Proceed if type is correct:
		if(!type.equals(SkillType.OFFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!defender.equals(EntityType.CREATURE) && !defender.equals(EntityType.ALL)){
			return;
		}

		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MELEE) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Modify:
		modifyAttack(multiplier, attackerPlayer, event);
		
		
	}

	/**
	 * Called when the player gets damaged by a creature.
	 * 
	 * @param event event
	 */
	public void triggerHitByPlayer(SagaPlayer defenderPlayer,  Integer multiplier, EntityDamageByEntityEvent event, SagaPlayer attackerPlayer) {
		
		
		// Proceed if type is correct:
		if(!type.equals(SkillType.DEFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!attacker.equals(EntityType.PLAYER) && !attacker.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MELEE) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyDefense(multiplier, defenderPlayer,defenderArmour, event);
		
		
	}
	
	/**
	 * Called when the player damages a creature.
	 * 
	 * @param event event
	 */
	public void triggerHitPlayer(SagaPlayer attackerPlayer,  Integer multiplier, EntityDamageByEntityEvent event, SagaPlayer defenderPlayer) {


		// Proceed if type is correct:
		if(!type.equals(SkillType.OFFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!defender.equals(EntityType.PLAYER) && !defender.equals(EntityType.ALL)){
			return;
		}

		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MELEE) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyAttack(multiplier, attackerPlayer, event);
		
		
	}

	
	/**
	 * Called when the player gets shot by a creature.
	 * 
	 * @param event event
	 */
	public void triggerShotByCreature(SagaPlayer defenderPlayer, Integer multiplier, EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {

		
		// Proceed if type is correct:
		if(!type.equals(SkillType.DEFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!attacker.equals(EntityType.CREATURE) && !attack.equals(AttackType.PHYSICAL) && !attacker.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.RANGED) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyDefense(multiplier, defenderPlayer,defenderArmour, event);
		
		
	}
	
	/**
	 * Called when the player shoots a creature.
	 * 
	 * @param event event
	 */
	public void triggerShotCreature(SagaPlayer attackerPlayer,  Integer multiplier, EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {


		// Proceed if type is correct:
		if(!type.equals(SkillType.OFFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!defender.equals(EntityType.CREATURE) && !defender.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.RANGED) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Modify:
		modifyAttack(multiplier, attackerPlayer, event);
		
		
	}

	/**
	 * Called when the player gets shot by a creature.
	 * 
	 * @param event event
	 */
	public void triggerShotByPlayer(SagaPlayer defenderPlayer,  Integer multiplier, EntityDamageByEntityEvent event, SagaPlayer attackerPlayer, Projectile projectile) {

		
		// Proceed if type is correct:
		if(!type.equals(SkillType.DEFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!attacker.equals(EntityType.PLAYER) && !attack.equals(AttackType.PHYSICAL) && !attacker.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.RANGED) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyDefense(multiplier, defenderPlayer,defenderArmour, event);
		
		
	}
	
	/**
	 * Called when the player shoots a creature.
	 * 
	 * @param event event
	 */
	public void triggerShotPlayer(SagaPlayer attackerPlayer,  Integer multiplier, EntityDamageByEntityEvent event, SagaPlayer defenderPlayer, Projectile projectile) {


		// Proceed if type is correct:
		if(!type.equals(SkillType.OFFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!defender.equals(EntityType.PLAYER) && !defender.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.RANGED) && !attack.equals(AttackType.PHYSICAL) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyAttack(multiplier, attackerPlayer, event);
		
		
	}


	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public void triggerSpelledCreature(SagaPlayer attackerPlayer,  Integer multiplier, EntityDamageByEntityEvent event, Creature creature, Projectile projectile) {


		// Proceed if type is correct:
		if(!type.equals(SkillType.OFFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!defender.equals(EntityType.PLAYER) && !defender.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MAGIC) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Modify:
		modifyAttack(multiplier, attackerPlayer, event);
		
		
	}
	
	/**
	 * Called when magic is casted on the player.
	 * 
	 * @param event event
	 */
	public void triggerSpelledByPlayer(SagaPlayer defenderPlayer,  Integer multiplier, EntityDamageByEntityEvent event, SagaPlayer attackerPlayer, Projectile projectile) {

		
		// Proceed if type is correct:
		if(!type.equals(SkillType.DEFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!attacker.equals(EntityType.PLAYER) && !attacker.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MAGIC) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyDefense(multiplier, defenderPlayer, defenderArmour, event);
		
		
	}
	
	/**
	 * Called when magic is casted by the player.
	 * 
	 * @param event event
	 */
	public void triggerSpelledPlayer(SagaPlayer attackerPlayer,  Integer multiplier, EntityDamageByEntityEvent event, SagaPlayer defenderPlayer, Projectile projectile) {


		// Proceed if type is correct:
		if(!type.equals(SkillType.OFFENSE)){
			return;
		}
		
		// Proceed if the attribute is correct:
		if(!defender.equals(EntityType.PLAYER) && !defender.equals(EntityType.ALL)){
			return;
		}
		
		// Proceed if the attack is correct:
		if(!attack.equals(AttackType.MAGIC) && !attack.equals(AttackType.ALL)){
			return;
		}
		
		// Check attacker weapon:
		if(!checkAttackerWeapons(attackerPlayer)){
			return;
		}

		// Check defender weapon:
		if(!checkDefenderWeapons(defenderPlayer)){
			return;
		}

		// Check attacker armor:
		double attackerArmour = getAttackerArmor(attackerPlayer);
		if(attackerArmour <= 0){
			return;
		}
		
		// Check defender armor:
		double defenderArmour = getDefenderArmor(defenderPlayer);
		if(defenderArmour <= 0){
			return;
		}
		
		// Modify:
		modifyAttack(multiplier, attackerPlayer, event);
		
		
	}
	

	/**
	 * Called when a player breaks a block.
	 * 
	 * @param sagaPlayer saga player
	 * @param multiplier multiplier
	 * @param event event
	 */
	public void triggerBlockBrake(SagaPlayer sagaPlayer, Integer multiplier, BlockBreakEvent event) {
		

//		// Experience:
//		Integer exp = calcExperience(event.getBlock(), multiplier);
//		
//		if(exp > 0){
//			
//			// Award:
//			sagaPlayer.giveExperience(exp);
//			
//			// Statistics:
//			StatisticsManager.manager().onSkillBlockExp(getName(), exp, sagaPlayer);
//			
//		}
		
		
	}
	
	
	
	// Checks:
	/**
	 * Checks attacker weapons.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if matches
	 */
	private boolean checkAttackerWeapons(SagaPlayer sagaPlayer) {

		
		ItemStack itemInHand = sagaPlayer.getItemInHand();
		
		if(attackerWeapons.size() == 0) return true;
		
		for (Material material : attackerWeapons) {
			if(itemInHand.getType().equals(material)) return true;
		}
		
		return false;
		
	}

	/**
	 * Checks defender weapons.
	 * 
	 * @param sagaPlayer saga player
	 * @return true if matches
	 */
	private boolean checkDefenderWeapons(SagaPlayer sagaPlayer) {

		
		ItemStack itemInHand = sagaPlayer.getItemInHand();
		
		if(defenderWeapons.size() == 0) return true;
		
		for (Material material : defenderWeapons) {
			if(itemInHand.getType().equals(material)) return true;
		}
		
		return false;
		
	}
	
	/**
	 * Gets attacker armor.
	 * 
	 * @param sagaPlayer saga player
	 * @return attacker armor
	 */
	private double getAttackerArmor(SagaPlayer sagaPlayer) {

		return sagaPlayer.getArmor(attackerArmor);
		
	}
	
	/**
	 * Gets defender armor.
	 * 
	 * @param sagaPlayer saga player
	 * @return defender armor
	 */
	private double getDefenderArmor(SagaPlayer sagaPlayer) {

		return sagaPlayer.getArmor(defenderArmor);
		
	}

	
	// Getters:
	/**
	 * Gets attribute name.
	 * 
	 * @return attribute name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets attribute type.
	 * 
	 * @return attribute type
	 */
	public SkillType getType() {
		return type;
	}
	
	
	// Util:
	private static int randomRound(Double value) {

		
		
		if (value >= 0){
		
			double decimal = value - Math.floor(value);
			
			if(random.nextDouble() < decimal){
				return value.intValue() + 1;
			}else{
				return value.intValue();
			}
			
		}else{
			
			double decimal = -value + Math.ceil(value);
			
			if(random.nextDouble() < decimal){
				return value.intValue() - 1;
			}else{
				return value.intValue();
			}
			
		}
			
			
			
		
	}
	
	// Other:
	@Override
	public String toString() {
		return getName();
	}
	
	
	// Types:
	public enum EntityType{
		
		PLAYER,
		CREATURE,
		ALL;
		
	}
	
	public enum AttackType{
		
		
		NONE,
		MELEE,
		RANGED,
		PHYSICAL,
		MAGIC,
		ALL;
		
		
	}
	
	public enum ArmourType{
		
		NONE,
		LIGHT,
		HEAVY,
		EXOTIC,
		UNARMOURED,
		ALL,
		
	}
	
	public static void main(String[] args) {
		
		for (int i = 0; i < 100; i++) {
			
		}
		
	}


}
