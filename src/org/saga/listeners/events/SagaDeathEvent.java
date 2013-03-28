package org.saga.listeners.events;

import org.bukkit.event.entity.EntityDeathEvent;
import org.saga.config.AttributeConfiguration;
import org.saga.config.ExperienceConfiguration;
import org.saga.metadata.SpawnerTag;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;

public class SagaDeathEvent {
	

	/**
	 * Minecraft event.
	 */
	private EntityDeathEvent event;
	
	/**
	 * Damage event.
	 */
	private SagaDamageEvent damEvent = null;
	 
	
	/**
	 * Sets death event.
	 * 
	 * @param event event
	 */
	public SagaDeathEvent(EntityDeathEvent event) {
		
		this.event = event;
		
		if(event.getEntity().getLastDamageCause() != null) damEvent = new SagaDamageEvent(event.getEntity().getLastDamageCause());
		
	}

	
	/**
	 * Applies the event.
	 * 
	 */
	public void apply() {

		
		// No cause:
		if(damEvent == null) return;
		
		// Killed a creature:
		if(damEvent.isPvC()){
			
			String group = "creature";
			
			// Get exp:
			Double exp = ExperienceConfiguration.config().getExp(damEvent.creatureDefender);
			
			// Unnatural spawn:
			if(event.getEntity().hasMetadata(SpawnerTag.METADATA_KEY)){
				
				// Modify enchant points:
				event.setDroppedExp(TwoPointFunction.randomRound(ExperienceConfiguration.config().spawnerEncPointMult * event.getDroppedExp()));
				
				exp *= ExperienceConfiguration.config().spawnerExpMult;
				
				group = "creature(spawner)";
				
			}
			
			// Award exp:
			damEvent.sagaAttacker.awardExp(exp);
			
			// Statistics:
			String creatureName = damEvent.creatureDefender.getClass().getSimpleName().replace("_", " ").toLowerCase().replace("craft","");
			StatisticsManager.manager().addExp(group, creatureName, exp);
			
		}
		
		// Killed a player:
		else if(damEvent.isPvP()){
			
			// Award exp:
			Double exp = ExperienceConfiguration.config().getExp(damEvent.sagaDefender);
			damEvent.sagaAttacker.awardExp(exp);
			
			// Statistics:
			Integer usedAttr = damEvent.sagaDefender.getUsedAttributePoints();
			String range = "";
			for (int maxi = 0; maxi <= AttributeConfiguration.config().getMaxAttributeCap(); maxi+=10) {
				
				if(usedAttr < maxi + 1){

					if(usedAttr == 0){
						range = "attributes " + usedAttr;
					}else{
						range = "attributes " + (maxi - 9) + "-" + (maxi);
					}
					break;
					
				}
				
			}
			StatisticsManager.manager().addExp("player", range, exp);
			
		}


	}

	
	
	
	// Getters:
	/**
	 * Gets the last damage event.
	 * 
	 * @return last damage event, null if none
	 */
	public SagaDamageEvent getLastDamageEvent() {
		return damEvent;
	}
	
}
