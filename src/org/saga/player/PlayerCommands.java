package org.saga.player;

import org.saga.Saga;
import org.saga.abilities.AbilityDefinition;
import org.saga.config.AbilityConfiguration;
import org.saga.config.ProficiencyConfiguration;
import org.saga.messages.ChunkGroupMessages;
import org.saga.messages.PlayerMessages;
import org.saga.messages.SagaMessages;
import org.saga.messages.StatsMessages;
import org.saga.player.Proficiency.ProficiencyType;
import org.sk89q.Command;
import org.sk89q.CommandContext;
import org.sk89q.CommandPermissions;

public class PlayerCommands {
	
	
	// Info:
	@Command(
			aliases = {"stats"},
			usage = "[page]",
			flags = "",
			desc = "Shows player stats.",
			min = 0,
			max = 1
	)
    @CommandPermissions({"saga.user.stats"})
	public static void stats(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
	
		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			
			String sPage = args.getString(0);
			
			try {
				page = Integer.parseInt(sPage);
			}
			catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidInteger(sPage));
				return;
			}
			
		}else{
			page = 1;
		}
		
		sagaPlayer.message(StatsMessages.stats(sagaPlayer, page-1));
    	
		
	}
	

	// Proficiencies:
	@Command(
            aliases = {"aremoveprofession","aremoveprof"},
            usage = "",
            flags = "",
            desc = "Removes trained profession.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.admin.removeprofession"})
	public static void removeProfession(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// No proficiency:
		Proficiency proficiency = sagaPlayer.getProfession();
		if(proficiency == null){
			sagaPlayer.message(PlayerMessages.noProficiency(ProficiencyType.PROFESSION));
			return;
		}
		
		// Remove:
		sagaPlayer.clearProfession();
		
		// Inform:
		sagaPlayer.message(PlayerMessages.removedProficiency(proficiency));
		
			
	}

	@Command(
            aliases = {"aremoveclass"},
            usage = "",
            flags = "",
            desc = "Removes trained class.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.admin.removeclass"})
	public static void removeClass(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {


		// No proficiency:
		Proficiency proficiency = sagaPlayer.getClazz();
		if(proficiency == null){
			sagaPlayer.message(PlayerMessages.noProficiency(ProficiencyType.CLASS));
			return;
		}
		
		// Remove:
		sagaPlayer.clearClass();
		
		// Inform:
		sagaPlayer.message(PlayerMessages.removedProficiency(proficiency));
		
			
	}
	
	
	// Guardian stone:
	@Command(
			aliases = {"disableguardianrune","grdisable"},
            usage = "",
            flags = "",
            desc = "Disables the guardian rune.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.guardianrune.disable"})
	public static void disableGuardianStone(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		GuardianRune stone = sagaPlayer.getGuardianRune();
		
		// Already disabled:
		if(!stone.isEnabled()){
			sagaPlayer.message(PlayerMessages.alreadyDisabled(stone));
			return;
		}
		
		// Disable:
		stone.setEnabled(false);
		
		// Inform:
		sagaPlayer.message(PlayerMessages.disabled(stone));
		
		
	}
	
	@Command(
            aliases = {"enableguardianrune","grenable"},
            usage = "",
            flags = "",
            desc = "Enables the guardian rune.",
            min = 0,
            max = 0)
	@CommandPermissions({"saga.user.guardianrune.enable"})
	public static void enableGuardianStone(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		GuardianRune stone = sagaPlayer.getGuardianRune();
		
		// Already enabled:
		if(stone.isEnabled()){
			sagaPlayer.message(PlayerMessages.alreadyEnabled(stone));
			return;
		}
		
		// Disable:
		stone.setEnabled(true);
		
		// Inform:
		sagaPlayer.message(PlayerMessages.enabled(stone));
		
		
	}
	
	
	// Info:
	@Command(
            aliases = {"trainingcost","trncost"},
            usage = "",
            flags = "",
            desc = "Show training costs.",
            min = 0,
            max = 1
    )
    @CommandPermissions({"saga.user.trainingcost"})
    public static void upgradeCost(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {
		
		// Inform:
		sagaPlayer.message(PlayerMessages.trainingCost(sagaPlayer));
		
	}

	@Command(
			aliases = {"phelp"},
			usage = "[page number]",
			flags = "",
			desc = "Display player help.",
			min = 0,
			max = 1
	)
	@CommandPermissions({"saga.user.help"})
	public static void help(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Integer page = null;
		
		// Arguments:
		if(args.argsLength() == 1){
			try {
				page = Integer.parseInt(args.getString(0));
			} catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
				return;
			}
		}else{
			page = 0;
		}
		
		// Inform:
		sagaPlayer.message(PlayerMessages.help(page - 1));

		
	}
	
	@Command(
			aliases = {"abilityinfo","abinf"},
			usage = "<ability_name> [page number]",
			flags = "",
			desc = "Display ability information.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.abilityinfo"})
	public static void abilityInfo(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Integer page = null;
		AbilityDefinition definition = null;
		
		// Arguments:
		if(args.argsLength() == 2){

			String name = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			definition = AbilityConfiguration.config().getDefinition(name);
			if(definition == null){
				sagaPlayer.message(StatsMessages.invalidAbility(name));
				return;
			}
			
			try {
				page = Integer.parseInt(args.getString(1));
			} catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
				return;
			}
			
		}else{
			
			String name = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			definition = AbilityConfiguration.config().getDefinition(name);
			if(definition == null){
				sagaPlayer.message(StatsMessages.invalidAbility(name));
				return;
			}
			
			page = 0;
			
		}
		
		// Inform:
		sagaPlayer.message(PlayerMessages.abilityInfo(definition, page-1));

		
	}
	
	@Command(
			aliases = {"proficiencyinfo","classinfo","profinfo","prinf"},
			usage = "<prof/class_name> [page number]",
			flags = "",
			desc = "Display class/profession information.",
			min = 1,
			max = 2
	)
	@CommandPermissions({"saga.user.proficiencyinfo"})
	public static void profiInfo(CommandContext args, Saga plugin, SagaPlayer sagaPlayer) {

		
		Integer page = null;
		ProficiencyDefinition definition = null;
		
		// Arguments:
		if(args.argsLength() == 2){

			String name = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			definition = ProficiencyConfiguration.config().getDefinition(name);
			if(definition == null){
				sagaPlayer.message(PlayerMessages.invalidProficiency(name));
				return;
			}
			
			try {
				page = Integer.parseInt(args.getString(1));
			} catch (NumberFormatException e) {
				sagaPlayer.message(ChunkGroupMessages.invalidPage(args.getString(0)));
				return;
			}
			
		}else{
			
			String name = args.getString(0).replace(SagaMessages.spaceSymbol, " ");
			definition = ProficiencyConfiguration.config().getDefinition(name);
			if(definition == null){
				sagaPlayer.message(PlayerMessages.invalidProficiency(name));
				return;
			}
			
			page = 0;
			
		}
		
		// Inform:
		sagaPlayer.message(PlayerMessages.proficiencyInfo(definition, page-1));

		
	}
	

}
