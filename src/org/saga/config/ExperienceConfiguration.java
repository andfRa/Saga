package org.saga.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;

import org.bukkit.entity.Player;
import org.saga.Saga;
import org.saga.constants.IOConstants.WriteReadType;
import org.saga.utility.TwoPointFunction;
import org.saga.utility.WriterReader;

import com.google.gson.JsonParseException;

public class ExperienceConfiguration {

	
	/**
	 * Instance of the configuration.
	 */
	transient private static ExperienceConfiguration instance;
	
	/**
	 * Gets the instance.
	 * 
	 * @return instance
	 */
	public static ExperienceConfiguration config() {
		return instance;
	}
	
	/**
	 * Experience needed to level up.
	 */
	private TwoPointFunction levelUpExp;

	/**
	 * Skill points.
	 */
	private TwoPointFunction skillPoints;
	
	/**
	 * Experience table.
	 */
	transient private Hashtable<Integer, Integer> expTable;
	
	
	// Initialization:
	/**
	 * Used by gson.
	 * 
	 */
	public ExperienceConfiguration() {
	}
	
	/**
	 * Completes.
	 * 
	 * @return integrity check
	 */
	public boolean complete() {
		

		boolean integrity = true;
		
		// Set instance:
		instance = this;

		if(levelUpExp == null){
			Saga.severe(getClass(), "levelUpExp field not initialized", "setting default");
			levelUpExp = new TwoPointFunction(0.0);
			integrity=false;
		}
		
		if(skillPoints == null){
			Saga.severe(getClass(), "skillPoints field not initialized", "setting default");
			skillPoints = new TwoPointFunction(0.0);
			integrity=false;
		}
		
		// Transient:
		expTable = createExpTable();
		
		return integrity;
		
		
	}

	
	// Interaction:
	/**
	 * Creates the experience table.
	 * 
	 * @return experience table
	 */
	private Hashtable<Integer, Integer> createExpTable() {

		Hashtable<Integer, Integer> expTable = new Hashtable<Integer, Integer>();
		expTable.put(0, 1);
		expTable.put(1, 7);
		expTable.put(2, 17);
		expTable.put(3, 31);
		expTable.put(4, 48);
		expTable.put(5, 69);
		expTable.put(6, 93);
		expTable.put(7, 121);
		expTable.put(8, 153);
		expTable.put(9, 187);
		expTable.put(10, 225);
		expTable.put(11, 267);
		expTable.put(12, 312);
		expTable.put(13, 361);
		expTable.put(14, 413);
		expTable.put(15, 469);
		expTable.put(16, 528);
		expTable.put(17, 591);
		expTable.put(18, 657);
		expTable.put(19, 727);
		expTable.put(20, 800);
		expTable.put(21, 877);
		expTable.put(22, 957);
		expTable.put(23, 1041);
		expTable.put(24, 1128);
		expTable.put(25, 1219);
		expTable.put(26, 1313);
		expTable.put(27, 1411);
		expTable.put(28, 1512);
		expTable.put(29, 1617);
		expTable.put(30, 1725);
		expTable.put(31, 1837);
		expTable.put(32, 1952);
		expTable.put(33, 2071);
		expTable.put(34, 2193);
		expTable.put(35, 2319);
		expTable.put(36, 2448);
		expTable.put(37, 2581);
		expTable.put(38, 2717);
		expTable.put(39, 2857);
		expTable.put(40, 3000);
		expTable.put(41, 3147);
		expTable.put(42, 3297);
		expTable.put(43, 3451);
		expTable.put(44, 3608);
		expTable.put(45, 3769);
		expTable.put(46, 3933);
		expTable.put(47, 4101);
		expTable.put(48, 4272);
		expTable.put(49, 4447);
		expTable.put(50, 4625);
		expTable.put(51, 4807);
		expTable.put(52, 4992);
		expTable.put(53, 5181);
		expTable.put(54, 5373);
		expTable.put(55, 5569);
		expTable.put(56, 5768);
		expTable.put(57, 5971);
		expTable.put(58, 6177);
		expTable.put(59, 6387);
		expTable.put(60, 6600);
		expTable.put(61, 6817);
		expTable.put(62, 7037);
		expTable.put(63, 7261);
		expTable.put(64, 7488);
		expTable.put(65, 7719);
		expTable.put(66, 7953);
		expTable.put(67, 8191);
		expTable.put(68, 8432);
		expTable.put(69, 8677);
		expTable.put(70, 8925);
		expTable.put(71, 9177);
		expTable.put(72, 9432);
		expTable.put(73, 9691);
		expTable.put(74, 9953);
		expTable.put(75, 10219);
		expTable.put(76, 10488);
		expTable.put(77, 10760);
		expTable.put(78, 11036);
		expTable.put(79, 11316);
		expTable.put(80, 11599);
		expTable.put(81, 11886);
		expTable.put(82, 12176);
		expTable.put(83, 12470);
		expTable.put(84, 12767);
		expTable.put(85, 13068);
		expTable.put(86, 13372);
		expTable.put(87, 13680);
		expTable.put(88, 13991);
		expTable.put(89, 14306);
		expTable.put(90, 14624);
		expTable.put(91, 14946);
		expTable.put(92, 15271);
		expTable.put(93, 15600);
		expTable.put(94, 15932);
		expTable.put(95, 16268);
		expTable.put(96, 16607);
		expTable.put(97, 16950);
		expTable.put(98, 17296);
		expTable.put(99, 17646);
		expTable.put(100, 17999);
		expTable.put(101, 18356);
		expTable.put(102, 18716);
		expTable.put(103, 19080);
		expTable.put(104, 19447);
		expTable.put(105, 19818);
		expTable.put(106, 20192);
		expTable.put(107, 20570);
		expTable.put(108, 20951);
		expTable.put(109, 21336);
		expTable.put(110, 21725);
		expTable.put(111, 22116);
		expTable.put(112, 22511);
		expTable.put(113, 22910);
		expTable.put(114, 23312);
		expTable.put(115, 23718);
		expTable.put(116, 24127);
		expTable.put(117, 24540);
		expTable.put(118, 24956);
		expTable.put(119, 25376);
		expTable.put(120, 25799);
		expTable.put(121, 26226);
		expTable.put(122, 26656);
		expTable.put(123, 27090);
		expTable.put(124, 27527);
		expTable.put(125, 27968);
		expTable.put(126, 28412);
		expTable.put(127, 28860);
		expTable.put(128, 29311);
		expTable.put(129, 29766);
		expTable.put(130, 30224);
		expTable.put(131, 30686);
		expTable.put(132, 31151);
		expTable.put(133, 31620);
		expTable.put(134, 32092);
		expTable.put(135, 32568);
		expTable.put(136, 33047);
		expTable.put(137, 33530);
		expTable.put(138, 34016);
		expTable.put(139, 34506);
		expTable.put(140, 34999);
		expTable.put(141, 35496);
		expTable.put(142, 35996);
		expTable.put(143, 36500);
		expTable.put(144, 37007);
		expTable.put(145, 37518);
		expTable.put(146, 38033);
		expTable.put(147, 38550);
		expTable.put(148, 39071);
		expTable.put(149, 39596);
		expTable.put(150, 40124);
		expTable.put(151, 40656);
		expTable.put(152, 41191);
		expTable.put(153, 41730);
		expTable.put(154, 42272);
		expTable.put(155, 42818);
		expTable.put(156, 43367);
		expTable.put(157, 43920);
		expTable.put(158, 44476);
		expTable.put(159, 45036);
		expTable.put(160, 45599);
		expTable.put(161, 46166);
		expTable.put(162, 46736);
		expTable.put(163, 47310);
		expTable.put(164, 47887);
		expTable.put(165, 48468);
		expTable.put(166, 49052);
		expTable.put(167, 49640);
		expTable.put(168, 50231);
		expTable.put(169, 50826);
		expTable.put(170, 51424);
		expTable.put(171, 52026);
		expTable.put(172, 52631);
		expTable.put(173, 53240);
		expTable.put(174, 53852);
		expTable.put(175, 54468);
		expTable.put(176, 55087);
		expTable.put(177, 55710);
		expTable.put(178, 56336);
		expTable.put(179, 56966);
		expTable.put(180, 57599);
		expTable.put(181, 58236);
		expTable.put(182, 58876);
		expTable.put(183, 59520);
		expTable.put(184, 60167);
		expTable.put(185, 60818);
		expTable.put(186, 61472);
		expTable.put(187, 62130);
		expTable.put(188, 62791);
		expTable.put(189, 63456);
		expTable.put(190, 64124);
		expTable.put(191, 64796);
		expTable.put(192, 65471);
		expTable.put(193, 66150);
		expTable.put(194, 66832);
		expTable.put(195, 67518);
		expTable.put(196, 68207);
		expTable.put(197, 68900);
		expTable.put(198, 69596);
		expTable.put(199, 70296);
		expTable.put(200, 70999);
		
		return expTable;
		
	}
	
	/**
	 * Gets the experience that represents players level.
	 * 
	 * @param level level
	 * @return level experience
	 */
	public int getLevelExperience(Integer level) {

		// Level experience:
		Integer levelExp = expTable.get(level);
		
		// Find maximum value:
		if(levelExp == null){
			
			int maxValue = 0;
			Collection<Integer> expValues = expTable.values();
			
			for (int expValue : expValues) {
				
				if(expValue > maxValue) maxValue = expValue;
				
			}
			levelExp = maxValue;
			
		}
		
		return levelExp;
		
	}
	
	/**
	 * Gets partial experience.
	 * 
	 * @param fraction fraction
	 * @param level level
	 * @return partial experiecne
	 */
	public Integer getPartialExp(Float fraction, Integer level) {

		Integer levelExperience = getLevelExperience(level);
		Integer nextExperience = getLevelExperience(level + 1);
		
		Double deltaExp = nextExperience.doubleValue() - levelExperience.doubleValue();
		
		return new Double(deltaExp * new Double(fraction)).intValue();
		
	}
	
	/**
	 * Calculates players total experience.
	 * 
	 * @param player player
	 * @return players total experience
	 */
	public Integer getTotalExperience(Player player) {

		Integer level = player.getLevel();
		
		return getLevelExperience(level) + getPartialExp(player.getExp(), level);
		
	}
	
	/**
	 * Gets the experience required to level up.
	 * 
	 * @param level level
	 * @return experience required
	 */
	public Integer getLevelExp(Integer level) {
		
		return levelUpExp.value(level).intValue();

	}
	
	/**
	 * Gets the maximum level.
	 * 
	 * @return maximum level
	 */
	public Integer getMaxLevel() {
		
		return levelUpExp.getXMax().intValue();

	}
	
	/**
	 * Gets the skill points for the given level.
	 * 
	 * @param level level
	 * @return skill points
	 */
	public Integer getSkillPoints(Integer level) {
		
		return skillPoints.value(level).intValue();

	}
	
	// Load unload:
	/**
	 * Loads configuration.
	 * 
	 * @return experience configuration
	 */
	public static ExperienceConfiguration load(){
		
		
		boolean integrityCheck = true;

		// Load:
		ExperienceConfiguration config;
		try {
			config = WriterReader.readExperienceConfig();
		} catch (FileNotFoundException e) {
			
			Saga.severe(ExperienceConfiguration.class, "missing configuration ", "creating defaults");
			config = new ExperienceConfiguration();
			integrityCheck = false;
			
		} catch (IOException e) {
			
			Saga.severe(ExperienceConfiguration.class, "failed to load configuration: " + e.getClass() + ":" + e.getMessage(), "creating defaults");
			config = new ExperienceConfiguration();
			integrityCheck = false;
			
		} catch (JsonParseException e) {
			
			Saga.severe(ExperienceConfiguration.class, "failed to parse configuration: " + e.getClass() + ":" + e.getMessage(), "creating defaults");
			Saga.info("Parse message :" + e.getMessage());
			config = new ExperienceConfiguration();
			integrityCheck = false;
			
		}
		
		// Integrity check and complete:
		integrityCheck = config.complete() && integrityCheck;
		
		// Write default if integrity check failed:
		if (!integrityCheck) {
			
			Saga.severe("Integrity check failed for " + BalanceConfiguration.class.getSimpleName());
			Saga.info("Writing " + BalanceConfiguration.class.getSimpleName() + " with default values.");
			
			try {
				
				WriterReader.writeExperienceConfig(config, WriteReadType.CONFIG_DEFAULTS);
				
			} catch (IOException e) {
				
				Saga.severe(ExperienceConfiguration.class, "configuration write failure", "ignoring write");
				
			}
			
		}
		
		// Set instance:
		instance = config;
		
		return config;
		
		
	}
	
	/**
	 * Unloads configuration.
	 * 
	 */
	public static void unload(){
		instance = null;
	}
	
	
}
