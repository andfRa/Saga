package org.saga.messages;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.saga.buildings.Building;
import org.saga.config.SettlementConfiguration;
import org.saga.factions.Faction;
import org.saga.factions.SiegeManager;
import org.saga.listeners.events.SagaBuildEvent.BuildOverride;
import org.saga.messages.colours.Colour;
import org.saga.player.Proficiency;
import org.saga.player.SagaPlayer;
import org.saga.settlements.Bundle;
import org.saga.settlements.BundleToggleable;
import org.saga.settlements.SagaChunk;
import org.saga.settlements.SagaMap;
import org.saga.settlements.Settlement;
import org.saga.utility.chat.ChatFramer;
import org.saga.utility.chat.ChatUtil;


public class SettlementMessages {
	
	
	
	// Create/delete:
	public static String settled(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal1 + "Founded " + settlement.getName() + " settlement.";
	}

	public static String dissolved(Bundle settlement) {
		return Colour.normal1 + "Dissolved " + settlement.getName() + " settlement.";
	}

	public static String cantDissolve() {
		return Colour.negative + "Can't dissolve the settlement.";
	}
	
	
	
	// Naming:
	public static String invalidName() {
		return Colour.negative + "Settlement name must be  " + SettlementConfiguration.config().getMinNameLength() + "-" + SettlementConfiguration.config().getMaxNameLength() + ". Letters and numbers only.";
	}

	public static String inUse(String name) {
		return Colour.negative + "Settlement name " + name + " is already in use.";
	}
	
	public static String renamed(Bundle bundle) {

		return Colour.normal2 + "Settlement was renamed to " + bundle.getName() + ".";
		
	}
	
	
	
	// Claiming:
	public static String claimed(SagaChunk sagaChunk) {
		return Colour.normal1 +  "Claimed chunk.";
	}
	
	public static String claimed(SagaChunk sagaChunk, Bundle bundle) {
		return Colour.normal1 +  "Claimed chunk for " + bundle.getName() + " settlement.";
	}
	
	public static String notClaimed() {
		return Colour.negative +  "The chunk is no claimed.";
	}
	
	public static String abandoned(SagaChunk sagaChunk) {
		return Colour.normal1 +  "Abandoned chunk.";
	}
	
	public static String abandoned(SagaChunk sagaChunk, Bundle bundle) {
		return Colour.normal1 +  "Abandoned chunk from " + bundle.getName() + " settlement.";
	}
	
	public static String notEnoughClaims() {
		return Colour.negative + "Settlement doesn't have any claim points.";
	}

	public static String chunkAlreadyClaimed(){
		return Colour.negative + "This chunk of land is already claimed.";
	}
	
	public static String chunkNotClaimed(){
		return Colour.negative + "This chunk of land isn't claimed.";
	}

	public static String chunkMustBeAdjacent(){
		return Colour.negative + "You can only claim chunks adjacent to an existing settlement.";
	}
	
	public static String claimAdjacentDeny() {
		return Colour.negative + "Can't claim land adjacent to other settlements.";
	}

	
	
	// Buildings:
	public static String setBuilding(Building building) {
		return Colour.normal1 +  "Set " +  building.getName() + " building.";
	}
	
	public static String setBuilding(Building building, Bundle bundle) {
		return Colour.normal1 +  "Set " +  building.getName() + " building for " + bundle.getName() + " settlement.";
	}
	
	public static String removedBuilding(Building building) {
		return Colour.normal1 +  "Removed " +  building.getName() + " building.";
	}
	
	public static String removedBuilding(Building building, Bundle bundle) {
		return Colour.normal1 +  "Removed " +  building.getName() + " building from " + bundle.getName() + " settlement.";
	}

	
	
	// Building points:
	public static String notEnoughBuildingPoints(Building building) {
		return Colour.negative + "Not enough build points.";
	}

	public static String noBuilding() {
		return Colour.negative + "There is no building on this chunk of land.";
	}

	
	
	// Members:
	public static String notMember(){
		return Colour.negative + "You aren't a settlement member.";
	}
	
	public static String notMember(Bundle bundle){
		return Colour.negative + "You aren't a member of " + bundle.getName() + " settlement.";
	}
	
	public static String notMember(Bundle bundle, String name){
		return Colour.negative + "Player " + name + " isn't a member of the settlement.";
	}

	public static String alreadyInSettlement() {
		return Colour.negative + "You are already in a settlement.";
	}
	
	public static String alreadyInSettlement(SagaPlayer selPlayer) {
		return Colour.negative + "Player " + selPlayer.getName() + " is already in a settlement.";
	}

	
	
	// Invite join leave:
	public static String noInvites() {
		return Colour.negative + "You don't have any settlement invitations.";
	}
	
	public static String noInvites(String factionName) {
		return Colour.negative + "You don't have an invitation to " + factionName + " settlement.";
	}
	
	public static String cantKickYourself(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.negative + "You can't kick yourself from the settlement.";
	}

	public static String alreadyInvited(SagaPlayer sagaPlayer, Bundle group) {
		return Colour.negative + sagaPlayer.getName() + " is already invited to the settlement.";
	}
	
	public static String notMember(SagaPlayer sagaPlayer, Bundle bundle) {
		return Colour.negative + "Player " + sagaPlayer.getName() + " isn't a member of the settlement.";
	}
	
	public static String notMember(SagaPlayer sagaPlayer) {
		return Colour.negative + "Player " + sagaPlayer.getName() + " isn't a member of the settlement.";
	}

	public static String declinedInvite(Bundle bundle) {
		return Colour.normal1 + "Declined a join invitation from " + bundle.getName() + " settlement.";
	}
	
	public static String declinedAllInvites() {
		return Colour.normal1 + "Declined all settlement join invitations.";
	}

	public static String informAccept() {
		return Colour.normal1 + "Use /saccept to accept the settlement join invitation.";
	}

	
	
	// Invite join leave broadcasts:
	public static String wasInvited(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal1 + "You were invited to " + settlement.getName() + " settlement.";
	}
	
	public static String invited(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal2 + sagaPlayer.getName() + " was invited to the settlement.";
	}
	
	
	public static String haveJoined(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal1 + "Joined " +settlement.getName() + " settlement.";
	}
	
	public static String joined(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal2 + sagaPlayer.getName() + " joined the settlement.";
	}
	
	
	public static String haveQuit(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal1 + "Quit from " + settlement.getName() + " settlement.";
	}
	
	public static String quit(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal2 + sagaPlayer.getName() + " quit the settlement.";
	}

	
	public static String wasKicked(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal1 + "You were kicked out of " + settlement.getName() + " settlement.";
	}
	
	public static String kicked(SagaPlayer sagaPlayer, Bundle settlement) {
		return Colour.normal2 + sagaPlayer.getName() + " was kicked from the settlement.";
	}

	
	
	// Owner:
	public static String newOwner(String name) {
		return Colour.normal2 + "Player " + name + " is the new owner of the settlement.";
	}
	
	public static String alreadyOwner() {
		return Colour.negative + "You are already the settlement owner.";
	}
	
	public static String alreadyOwner(String name) {
		return Colour.negative + "Player " + name + " is already the settlement owner.";
	}
	
	public static String ownerCantQuit() {
		return Colour.negative + "Settlement owner can't quit the settlement.";
	}
	
	public static String ownerCantQuitInfo() {
		return Colour.normal1 + "Use /sresign to declare someone else as the owner.";
	}

	public static String cantKickOwner() {
		return Colour.negative + "Can't kick settlement owner.";
	}
	
	
	
	// Roles:
	public static String invalidRole(String roleName){
		return Colour.negative + "Role " + roleName + " is invalid.";
	}
	
	public static String newRole(SagaPlayer sagaPlayer, Bundle settlement, String roleName) {
		
		return Colour.normal2 + sagaPlayer.getName() + " is now a " + roleName + ".";
		
	}

	public static String roleNotAvailable(String roleName) {

		return Colour.negative + "No " + roleName + " roles are available.";
		
	}
	
	public static String roledPlayer(Settlement settlement, SagaPlayer sagaPlayer) {

		Proficiency role = settlement.getRole(sagaPlayer.getName());
		
		if(role == null){
			return sagaPlayer.getName();
		}else{
			return role.getName() + " " + sagaPlayer.getName();
		}
		
	}
	
	
	
	// Options:
	public static String optionToggle(Bundle bundle, BundleToggleable option) {
		
		if(bundle.isOptionEnabled(option)){
			return Colour.positive + "Enabled " + option + " for " + bundle.getName() + ".";
		}else{
			return Colour.positive + "Disabled " + option + " for " + bundle.getName() + ".";
		}

	}
	
	public static String optionAlreadyEnabled(Bundle bundle, BundleToggleable option){
		
		return Colour.negative + "Option " + option.toString() + " is already enabled for " + bundle.getName() + " settlement.";
		
	}
	
	public static String optionAlreadyDisabled(Bundle bundle, BundleToggleable option){
		
		return Colour.negative + "Option " + option.toString() + " is already disabled for " + bundle.getName() + " settlement.";
		
	}
	
	public static String optionInvalid(String option){
		
		return Colour.negative + "Option " + option + " is not valid.";
		
	}
	
	public static String optionInvalidInfo(){
		
		
		BundleToggleable[] options = BundleToggleable.values();
		ArrayList<String> validOptions = new ArrayList<String>();
		for (int i = 0; i < options.length; i++) {
			
			validOptions.add(options[i].toString().replace(" ", GeneralMessages.SPACE_SYMBOL));
			
		}
		
		return Colour.normal1 + "Valid options: " + ChatUtil.flatten(validOptions) + ".";

		
	}
	
	
	
	// Movement:
	public static String entered(Bundle bundle) {
		
		// Claimed:
		Faction faction = SiegeManager.manager().getOwningFaction(bundle.getId());
		if(faction != null){
			return Colour.normal1 + "[" + FactionMessages.faction(faction) + "]" + ChatColor.ITALIC + " Entered " + bundle.getName() + " settlement.";
		}
		
		return Colour.normal1 + "" + ChatColor.ITALIC + "Entered " + bundle.getName() + " settlement.";
		
	}
	
	public static String left(Bundle bundle) {

		// Claimed:
		Faction faction = SiegeManager.manager().getOwningFaction(bundle.getId());
		if(faction != null){
			return Colour.normal1 + "[" + FactionMessages.faction(faction) + "]" + ChatColor.ITALIC + " Left " + bundle.getName() + " settlement.";
		}
		
		return Colour.normal1 + "" + ChatColor.ITALIC + "Left " + bundle.getName() + " settlement.";
		
	}
	
	
	
	// Info:
	public static String wrongQuit() {
		
		return Colour.negative + "Because /squit and /fquit are similar, this command isn't used. Please use /settlementquit instead.";
		
	}

	
	// Map:
	public static String map(SagaPlayer sagaPlayer, Location location){
		
		
		ArrayList<String> map = SagaMap.getMap(sagaPlayer, sagaPlayer.getLocation());
		StringBuffer result = new StringBuffer();
		
		// Add borders:
		for (int i = 0; i < map.size(); i++) {
			if(i != 0) result.append("\n");
			result.append(map.get(i));
		}
		
		Chunk locationChunk = location.getWorld().getChunkAt(location);
		String title = locationChunk.getWorld().getName() + " map " + "(" + locationChunk.getX() + ", " + locationChunk.getZ() + ")";
		
		return ChatFramer.frame(title, result.toString(), ChatColor.GOLD, 0.75);
		//TODO: Remove frame size limitation for special map characters when chat handles them correctly.
		
	}
	
	
	// Build restriction:
	public static String buildOverride(BuildOverride override){
		
		
		switch (override) {
			case BUILDING_DENY:
				
				return Colour.negative + "Can't build in this building.";

			case SETTLEMENT_DENY:
				
				return Colour.negative + "Can't build in this settlement.";

			case CHUNK_GROUP_DENY:
				
				return Colour.negative + "Can't build in this chunk group.";
				
			case HOME_DENY:
				
				return Colour.negative + "Can't build in this home.";

			case WILDERNESS_DENY:
				
				return Colour.negative + "Can't build in the wilderness.";

			case CRUMBLE_ARENA_DENY:
				
				return Colour.negative + "Can't build on the arena.";

			default:
				return Colour.negative + "Can't build here.";
				
		}
		
		
	}
	
	
}
