package net.dugged.cutelessmod;

import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MODID)
public class Configuration {

	// Booleans
	public static boolean alwaysDay = false;
	public static boolean alwaysRenderTileEntities = false;
	public static boolean alwaysSingleplayerCheats = false;
	public static boolean clickBlockMining = false;
	public static boolean clientEntityUpdates = true;
	public static boolean craftingHax = true;
	public static boolean deathLocation = false;
	public static boolean extendedChat = false;
	public static boolean fixBlock36Particles = false;
	public static boolean flightInertiaCancellation = false;
	public static boolean instantDoubleRetraction = false;
	public static boolean jumpBoostStepAssist = false;
	public static boolean stepAssist = false;
	public static boolean miningGhostBlockFix = false;
	public static boolean noFall = false;
	public static boolean respawnOnDeath = false;
	public static boolean showBlockBreakingParticles = true;
	public static boolean showCenteredPlants = false;
	public static boolean showClearLava = false;
	public static boolean showDeathAnimations = true;
	public static boolean showGuiBackGround = true;
	public static boolean showHand = true;
	public static boolean showIdealToolMarker = false;
	public static boolean showItemAttributes = true;
	public static boolean showItemFrameFrame = true;
	public static boolean showOneBossBar = false;
	public static boolean showPotionShift = true;
	public static boolean showRain = true;
	public static boolean showRainbowLeaves = false;
	public static boolean showServerNames = true;
	public static boolean showSmoothWaterLighting = false;
	public static boolean showSneakEyeHeight = false;
	public static boolean showSnowDripParticles = false;
	public static boolean showSpectatorTeamMenu = true;
	public static boolean showShulkerBoxDisplay = false;
	public static boolean smoothItemMovement = false;
	public static boolean sortEnchantmentTooltip = false;
	public static boolean derpyChicken = false;
	@Config.Comment("Enables client-side start of elytra flight by Earthcomputer. May cause you to fall to your death randomly.")
	public static boolean elytraFix = false;
	public static boolean colouredFireworksTrail = false;
	public static boolean alwaysShowPing = false;
	public static boolean dynamicServerListUpdates = false;
	public static boolean alwaysPickBlockMaxStack = false;
	public static boolean lightUpdates = true;

	// Ints
	@Config.RangeInt(min = 0)
	public static int speedyPlace = 4;
	@Config.RangeInt(min = 0)
	public static int reconnectTimer = 10;

	// Sliders
	@Config.SlidingOption
	@Config.RangeDouble(min = 0D, max = 1D)
	public static double spectatorMaxSpeed = 0.2;
	@Config.SlidingOption
	@Config.RangeDouble(min = 0D, max = 3D)
	public static double blockBreakingMultiplier = 1D;
}
