package net.dugged.cleancuteless;

import net.minecraftforge.common.config.Config;

@Config(modid = Reference.MODID, category = "")
public final class Configuration {
	public static final General general = new General();
	public static final Visuals visuals = new Visuals();
	public static final Rendering rendering = new Rendering();
	public static final Player player = new Player();
	public static final Building building = new Building();
	public static final Ui ui = new Ui();
	public static final Network network = new Network();
	public static final Fixes fixes = new Fixes();

	private Configuration() {
	}

	public static final class General {
		@Config.RequiresMcRestart
		@Config.Comment("Create an unbound toggle keybind for each boolean option without a restart requirement (applies on next launch).")
		public boolean autoGenerateKeybinds = false;
		@Config.Comment("Allow all commands in singleplayer without operator permissions.")
		public boolean singleplayerCheats = false;
	}

	public static final class Visuals {
		@Config.Comment("Show daytime on the client without changing server time.")
		public boolean forceDaytime = false;
		@Config.Comment("Give firework trail sparks random colors.")
		public boolean colorfulFireworkTrails = false;
		@Config.Comment("Tilt chicken and enderman models for the derpy visual effect.")
		public boolean tiltedMobs = false;
		@Config.Comment("Render armor on player models.")
		public boolean showArmor = true;
		@Config.RequiresMcRestart
		@Config.Comment("Render barrier blocks as translucent textured blocks.")
		public boolean showBarrierBlocks = false;
		@Config.Comment("Spawn block-breaking particles when blocks are destroyed.")
		public boolean showBlockBreakingParticles = true;
		@Config.Comment("Show the block selection outline while the camera is underwater.")
		public boolean showBlockSelectorUnderwater = false;
		@Config.Comment("Center plants in their block instead of using their random offset.")
		public boolean showCenteredPlants = false;
		@Config.Comment("Remove the thick fog overlay while inside lava.")
		public boolean showClearLava = false;
		@Config.Comment("Tilt the camera when the player takes damage.")
		public boolean showDamageTilt = false;
		@Config.Comment("Render mob death animations.")
		public boolean showDeathAnimations = true;
		@Config.Comment("Spawn the poof particles when mobs die.")
		public boolean showDeathParticles = true;
		@Config.Comment("Draw the dark gradient behind GUI screens.")
		public boolean showGuiBackground = true;
		@Config.Comment("Render the player's hand in first person.")
		public boolean showHand = true;
		@Config.Comment("Play the held-item change animation.")
		public boolean showHandChangeAnimation = true;
		@Config.Comment("Show letter markers for ideal tools, empty shulkers, and firework flight levels.")
		public boolean showIdealToolMarker = false;
		@Config.Comment("Render the item frame border and use the full frame selection box.")
		public boolean showItemFrameBorder = true;
		@Config.Comment("Render end portal faces on all sides not blocked by opaque blocks.")
		public boolean showMoreEndPortalFaces = false;
		@Config.Comment("Shift inventory screens sideways while potion effects are active.")
		public boolean showPotionShift = true;
		@Config.Comment("Show rain weather on the client.")
		public boolean showRain = true;
		@Config.Comment("Color leaf blocks with a position-based rainbow hue.")
		public boolean showRainbowLeaves = false;
		@Config.Comment("Show randomized block model variants.")
		public boolean showRandomTextures = true;
		@Config.Comment("Show shulker box contents and filled map previews in item tooltips.")
		public boolean showShulkerContents = false;
		@Config.Comment("Enable smooth lighting across water surfaces.")
		public boolean smoothWaterLighting = false;
		@Config.Comment("Smooth camera height changes while sneaking.")
		public boolean showSneakEyeHeight = false;
		@Config.Comment("Show dripping particles under snow layers.")
		public boolean showSnowDripParticles = false;
		@Config.Comment("Open the player teleport menu directly in spectator mode.")
		public boolean showSpectatorTeamMenu = true;
		@Config.Comment("Show view bobbing while walking.")
		public boolean showWorldBobbing = true;
		@Config.Comment("Show distance fog in the world.")
		public boolean showWorldFog = true;
		@Config.Comment("Apply the field-of-view change while underwater.")
		public boolean waterAffectsFov = true;
		@Config.SlidingOption
		@Config.RangeDouble(min = 1D, max = 50D)
		@Config.Comment("Default zoom factor while holding the zoom key.")
		public double zoomAmount = 4D;
		@Config.RangeInt(min = 0, max = 1000)
		@Config.Comment("Time in milliseconds for zoom transitions; 0 disables smoothing.")
		public int zoomSmoothnessMs = 150;
		@Config.Comment("Adjust the zoom factor with the mouse wheel while zooming.")
		public boolean zoomScroll = true;
		@Config.Comment("Use the smooth cinematic camera while zoomed in.")
		public boolean zoomCinematic = true;
		@Config.SlidingOption
		@Config.RangeDouble(min = 0D, max = 1D)
		@Config.Comment("Lowest mouse sensitivity multiplier while fully zoomed in.")
		public double zoomSensitivityFloor = 0.4D;
		@Config.SlidingOption
		@Config.RangeDouble(min = 1D, max = 8D)
		@Config.Comment("Easing exponent for zoom transitions; higher reaches the target faster.")
		public double zoomAnimationEasing = 4D;
		@Config.SlidingOption
		@Config.RangeDouble(min = 0D, max = 3D)
		@Config.Comment("Scale block-break particle motion and lifetime; 0 removes motion and 1 is vanilla.")
		public double blockBreakParticleScale = 1D;
		@Config.Comment("Show one entity per block with a stacked count label.")
		public boolean stackedEntityCounts = false;
	}

	public static final class Rendering {
		@Config.Comment("Render tile entities at any distance instead of culling them by distance.")
		public boolean renderAllTileEntities = false;
		@Config.RequiresMcRestart
		@Config.Comment("Render chests as block models instead of tile entities.")
		public boolean renderChestsWithBlockModels = false;
		@Config.Comment("Update client lighting when blocks change.")
		public boolean lightUpdates = true;
		@Config.Comment("Disable cave mood sounds and the light checks they trigger.")
		public boolean disableCaveAmbience = false;
	}

	public static final class Player {
		@Config.Comment("Tick non-player entities on the client.")
		public boolean updateClientEntities = true;
		@Config.Comment("Shift+Ctrl-click a recipe to craft all available items (Alt throws the result).")
		public boolean quickCraftAll = true;
		@Config.Comment("Press Shift+Space to stop elytra flight.")
		public boolean cancelElytraFlight = false;
		@Config.Comment("Stop horizontal drift when releasing flight controls.")
		public boolean cancelFlightInertia = false;
		@Config.Comment("Prevent mouse movement from changing yaw.")
		public boolean lockYaw = false;
		@Config.Comment("Disable collision while flying in creative mode.")
		public boolean noClip = false;
		@Config.Comment("Send on-ground packets to prevent fall damage.")
		public boolean noFall = false;
		@Config.Comment("Respawn automatically instead of opening the death screen.")
		public boolean respawnOnDeath = false;
		@Config.Comment("Block firework use while its cooldown is active.")
		public boolean enforceRocketCooldown = false;
		@Config.SlidingOption
		@Config.RangeDouble(min = 0D, max = 1D)
		@Config.Comment("Maximum spectator flight speed set by scrolling; 0 stops, 0.2 is vanilla.")
		public double spectatorFlightSpeed = 0.2;
		@Config.Comment("Let the client move item entities out of blocks instead of waiting for server corrections.")
		public boolean smoothItemMovement = false;
	}

	public static final class Building {
		@Config.Comment("Give the stack picked with middle-click a full stack in creative mode.")
		public boolean pickBlockMaxStack = false;
		@Config.Comment("Remove the client-side delay between block breaks.")
		public boolean noBlockBreakDelay = false;
		@Config.Comment("Add a second row of four hotbar slots in creative mode.")
		public boolean extendedCreativeHotbar = false;
		@Config.Comment("Keep current items when a creative hotbar snapshot has empty slots.")
		public boolean ignoreAirHotbarSnapshots = false;
		@Config.Comment("Remove the extra piston retraction step on the client.")
		public boolean instantPistonDoubleRetraction = false;
		@Config.Comment("Right-click a piston or slime block with an empty hand to show movement order and block counts.")
		public boolean showPistonOrder = false;
		@Config.Comment("Smooth piston movement on the client.")
		public boolean smoothPistons = false;
		@Config.Comment("Enable the creative-mode WorldEdit compass menu and click-to-teleport.")
		public boolean worldEditCompass = false;
		@Config.RangeInt(min = 0)
		@Config.Comment("Ticks to wait between right-click actions (0 allows every tick).")
		public int rightClickDelayTicks = 4;
	}

	public static final class Ui {
		@Config.Comment("Show numeric ping in the server list.")
		public boolean showPing = false;
		@Config.Comment("Shift-click a chat line to copy it (Ctrl keeps formatting).")
		public boolean copyChatOnClick = false;
		@Config.Comment("Print a clickable death-coordinate message to chat.")
		public boolean showDeathLocation = false;
		@Config.Comment("Hide the Realms button on the main menu.")
		public boolean hideRealmsButton = false;
		@Config.Comment("Raise the chat input limit from 256 to 2048 characters.")
		public boolean extendedChat = false;
		@Config.RangeInt(min = 0)
		@Config.Comment("Seconds before automatically reconnecting after a disconnect (0 reconnects immediately).")
		public int reconnectDelaySeconds = 10;
		@Config.Comment("Show attribute modifiers in item tooltips with advanced tooltips enabled.")
		public boolean showItemAttributes = true;
		@Config.Comment("Show only the first boss bar.")
		public boolean showSingleBossBar = false;
		@Config.Comment("Show the scoreboard sidebar.")
		public boolean showScoreboard = true;
		@Config.Comment("Show server names in the server list.")
		public boolean showServerNames = true;
		@Config.Comment("Sort item enchantments by level, highest first.")
		public boolean sortEnchantments = false;
	}

	public static final class Network {
		@Config.Comment("Encode precise placement rotations in block-placement packets for Carpet servers.")
		public boolean carpetAccuratePlacement = false;
		@Config.Comment("Refresh pings for visible servers every 15 seconds.")
		public boolean refreshServerListPings = false;
		@Config.Comment("Ignore incoming block action events such as chest opening and note block sounds.")
		public boolean ignoreBlockEvents = false;
	}

	public static final class Fixes {
		@Config.Comment("Mark elytra as deployed on the client when the server starts fall flying.")
		public boolean fixElytraDeploy = false;
		@Config.Comment("Use the moving piston texture for piston break particles.")
		public boolean fixPistonBreakParticles = false;
		@Config.Comment("Allow fire placement above upward-facing observers in creative mode.")
		public boolean fixObserverFire = false;
		@Config.Comment("Resync blocks after instant mining so ghost blocks disappear.")
		public boolean fixMiningGhostBlocks = false;
		@Config.Comment("Restore Shift+number keys on Linux keyboards.")
		public boolean fixLinuxShiftNumberKeys = false;
		@Config.Comment("Fix texture seams on layered item models.")
		public boolean fixItemStitchingSeams = false;
	}
}
