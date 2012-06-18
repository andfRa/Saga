package org.saga.listeners.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.saga.config.ExperienceConfiguration;
import org.saga.metadata.SpawnerTag;
import org.saga.statistics.StatisticsManager;
import org.saga.utility.TwoPointFunction;

public class SagaEntityDeathEvent {
	

	/**
	 * Minecraft event.
	 */
	private EntityDeathEvent event;
	
	/**
	 * Damage event.
	 */
	private SagaEntityDamageEvent damEvent;
	 
	
	/**
	 * Sets death event.
	 * 
	 * @param event event
	 * @param deadEntity dead entity
	 */
	public SagaEntityDeathEvent(EntityDeathEvent event, LivingEntity deadEntity) {

		
		this.event = event;
		
		// Entity damage event:
		damEvent = new SagaEntityDamageEvent(event.getEntity().getLastDamageCause(), event.getEntity());
		
		

	}

	
	/**
	 * Applies the event.
	 * 
	 */
	public void apply() {

		
		// Killed a creature:
		if(damEvent.isPlayerAttackCreature()){
			
			String group = "creature";
			
			// Get exp:
			Double exp = ExperienceConfiguration.config().getExp(damEvent.getDefenderCreature());
			
			// Unnatural spawn:
			if(event.getEntity().hasMetadata(SpawnerTag.METADATA_KEY)){
				
				// Modify enchant points:
				event.setDroppedExp(TwoPointFunction.randomRound(ExperienceConfiguration.config().spawnerEncPointMult * event.getDroppedExp()));
				
				exp *= ExperienceConfiguration.config().spawnerExpMult;
				
				group = "creature(spawner)";
				
			}
			
			// Award exp:
			damEvent.getAttackerPlayer().awardExp(exp);
			
			// Statistics:
			String creatureName = damEvent.getDefenderCreature().getClass().getSimpleName().replace("_", " ").toLowerCase().replace("craft","");
			StatisticsManager.manager().addExp(group, creatureName, exp);
			
		}
		
		// Killed a player:
		else if(damEvent.isPlayerAttackPlayer()){
			
			// Award exp:
			Double exp = ExperienceConfiguration.config().getExp(damEvent.getDefenderPlayer());
			damEvent.getAttackerPlayer().awardExp(exp);
			
			// Statistics:
			Integer level = damEvent.getDefenderPlayer().getLevel();
			String range = "";
			for (int maxi = 0; maxi <= ExperienceConfiguration.config().maximumLevel; maxi+=10) {
				
				if(level < maxi + 1){

					if(level == 0){
						range = "level " + level;
					}else{
						range = "levels " + (maxi - 9) + "-" + (maxi);
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
	 * @return last damage event
	 */
	public SagaEntityDamageEvent getLastDamageEvent() {
		return damEvent;
	}
	
}
