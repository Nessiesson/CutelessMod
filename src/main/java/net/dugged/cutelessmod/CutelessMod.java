package net.dugged.cutelessmod;

import net.dugged.cutelessmod.chunk_display.CarpetPluginChannel;
import net.dugged.cutelessmod.chunk_display.gui.Controller;
import net.dugged.cutelessmod.chunk_display.gui.GuiChunkGrid;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.mixins.ISoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenOptionsSounds;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCompass;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, clientSideOnly = true)
public class CutelessMod {
	public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final StepAssistHelper stepAssistHelper = new StepAssistHelper();
	public static Map<String, List<ChatLine>> chatHistory = new HashMap<>();
	public static Map<String, List<String>> tabCompleteHistory = new HashMap<>();
	public static int toggleBeaconArea = 0;
	public static long lastTimeUpdate;
	public static int mspt;
	public static int overlayTimer = 0;
	public static ITextComponent tabFooter;
	public static int sendPacketsThisTick = 0;
	public static int[] sendPackets = new int[20];
	public static int receivedPacketsThisTick = 0;
	public static int[] receivedPackets = new int[20];
	public static ServerData currentServer;
	public static long tickCounter = 0;
	public static int hotbarSlot = 0;
	public static String statPluginFilter = "stat.useItem.minecraft.diamond_pickaxe";
	public static StatPlugin statPlugin = new StatPlugin();
	public static String lastCommand = "";
	public static GuiCompass guiCompass = new GuiCompass(mc);
	public static boolean enableSounds = CutelessMod.shouldHaveSounds();
	public static boolean highlightEntities = false;
	private final CarpetPluginChannel carpetPluginChannel = new CarpetPluginChannel();
	private String originalTitle;
	private boolean loggedOut;

	public static boolean shouldHaveSounds() {
		final GameSettings settings = mc.gameSettings;
		return settings.showSubtitles || settings.getSoundLevel(SoundCategory.MASTER) > 0F;
	}

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		this.originalTitle = Display.getTitle();
		updateTitle();
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		KeyBindings.init();
		ClientCommandHandler.getInstance().init();
		GuiChunkGrid.instance = new GuiChunkGrid();
		if (Configuration.chestWithoutTESR) {
			TileEntityRendererDispatcher.instance.renderers.remove(TileEntityChest.class);
		}

		try {
			GuiIngameForge.renderObjective = Configuration.class.getField("showScoreboards").getBoolean(Configuration.class);
		} catch (NoSuchFieldException | IllegalAccessException ignored) {
			// noop
		}
	}

	@SubscribeEvent
	public void onConfigChangedEvent(final ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(Reference.MODID)) {
			ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
		}
	}

	@SubscribeEvent
	public void onKeyPressed(final InputEvent.KeyInputEvent event) {
		if (KeyBindings.reloadAudioEngineKey.isPressed()) {
			((ISoundHandler) mc.getSoundHandler()).getSoundManager().reloadSoundSystem();
			this.debugFeedback();
		}

		if (KeyBindings.toggleBeaconAreaKey.isPressed()) {
			toggleBeaconArea++;
			switch (toggleBeaconArea) {
				case 1:
					mc.ingameGUI.setOverlayMessage("Showing area outlines", false);
					break;
				case 2:
					mc.ingameGUI.setOverlayMessage("Showing beacon areas", false);
					break;
				case 3:
					mc.ingameGUI.setOverlayMessage("Showing next beacon positions", false);
					break;
			}
			if (toggleBeaconArea > 3) {
				toggleBeaconArea = 0;
			}
		}

		if (KeyBindings.emptyScreenKey.isPressed()) {
			mc.displayGuiScreen(new GuiEmptyScreen());
		}

		if (KeyBindings.repeatLastCommandKey.isPressed() && !lastCommand.isEmpty()) {
			mc.player.sendChatMessage(CutelessMod.lastCommand);
		}

		if (KeyBindings.chunkDebug.isPressed()) {
			mc.displayGuiScreen(GuiChunkGrid.instance);
		}

		if (KeyBindings.putItemCounterKey.isPressed()) {
			if (ItemCounter.position != null) {
				ItemCounter.reset();
				ItemCounter.position = null;
			} else {
				ItemCounter.position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
			}
		}

		if (KeyBindings.putFrequencyAnalyzerKey.isPressed()) {
			if (FrequencyAnalyzer.position != null) {
				FrequencyAnalyzer.reset();
				FrequencyAnalyzer.position = null;
			} else {
				mc.ingameGUI.setOverlayMessage("Analyzer might be inaccurate depending on server TPS", false);
				FrequencyAnalyzer.position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
			}
		}

		if (KeyBindings.putRandomTickAreaKey.isPressed()) {
			RandomTickRenderer.getInstance().updatePosition(mc.player);
		}

		if (KeyBindings.putDespawnSphereKey.isPressed()) {
			DespawnSphereRenderer.getInstance().updatePosition(mc.player);
		}

		if (KeyBindings.highlightEntitiesKey.isPressed()) {
			highlightEntities = !highlightEntities;
		}

		if (KeyBindings.resetItemCounterKey.isPressed()) {
			mc.ingameGUI.setOverlayMessage("Reset Item Counter", false);
			ItemCounter.reset();
		}

		/*
		 Fixes the issue where Shift-2 and Shift-6 are not working on Linux.
		 Stolen from https://github.com/Leo3418/mckeyboardfix

		 In Minecraft on GNU/Linux, Shift-6 is interpreted as a press on the
		 `^` key, and Shift-2 is interpreted as a press on the `@` key, and
		 the numeric key is never detected as pressed in these cases. For
		 most keyboards, this does not make sense since they do not have
		 dedicated `^` or `@` key, but indeed, this is what happens.

		 To fix this issue, we just need to emulate a press on the numeric
		 key. But merely doing this is not enough because after we emulate a
		 press on key `x`, every key combination from Shift-1 to Shift-`x-1`
		 does not work until the user presses the combination again, so we
		 also need to emulate those presses to mitigate this side effect of
		 the fix.
		*/
		if (Configuration.weirdShift2Shift6LinuxBug) {
			switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_CIRCUMFLEX: // Shift-6
					KeyBinding.onTick(Keyboard.KEY_6);
					KeyBinding.onTick(Keyboard.KEY_5);
					KeyBinding.onTick(Keyboard.KEY_4);
					KeyBinding.onTick(Keyboard.KEY_3);
					// Fall through
				case Keyboard.KEY_AT: // Shift-2
					KeyBinding.onTick(Keyboard.KEY_2);
					KeyBinding.onTick(Keyboard.KEY_1);
			}
		}

		if (KeyBindings.gammaHaxKey.isPressed()) {
			mc.gameSettings.gammaSetting = 1000;
			mc.gameSettings.saveOptions();
			mc.ingameGUI.setOverlayMessage("Enabled fullbright gammahax.", false);
		}

		if (KeyBindings.snapaimKey.isPressed()) {
			final EntityPlayerSP player = mc.player;
			player.rotationYaw = (int) (Math.round(player.rotationYaw / 45F) * 45F);
			mc.ingameGUI.setOverlayMessage("Thanos'd.", false);
		}
	}

	@SubscribeEvent
	public void onMouseEvent(final InputEvent.MouseInputEvent event) {
		Zoomer.INSTANCE.onMouseScroll();
	}

	@SubscribeEvent
	public void onModifyFOV(final EntityViewRenderEvent.FOVModifier event) {
		event.setFOV(Zoomer.INSTANCE.changeFovBasedOnZoom(event.getFOV()));
	}

	@SubscribeEvent
	public void onLoadWorld(final WorldEvent.Load event) {
		tabFooter = null;
	}

	@SubscribeEvent
	public void onTick(final TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}
		tickCounter++;
		if (overlayTimer > 0) {
			overlayTimer--;
		}
		if (mc.world != null) {
			if (!mc.isIntegratedServerRunning()) {
				currentServer = mc.getCurrentServerData();
			}
			// If connected to OBS request & update stat every 5 mins
			if (statPlugin.isConnected() && tickCounter > statPlugin.lastTick + 1200) {
				statPlugin.lastTick = tickCounter;
				mc.world.sendPacketToServer(new CPacketClientStatus(CPacketClientStatus.State.REQUEST_STATS));
			}

			if (mspt > 0) {
				final int tps = Math.min(20, 1000 / mspt);
				ITextComponent base = new TextComponentString("");
				if (tabFooter != null && tabFooter.getUnformattedText().matches("(?s).*TPS: \\d*\\.\\d* MSPT: \\d*\\.\\d*.*")) {
					mc.ingameGUI.getTabList().setFooter(tabFooter);
					Pattern pattern = Pattern.compile("(?s)(?<=TPS: )\\d*\\.\\d*");
					Matcher matcher = pattern.matcher(tabFooter.getUnformattedText());
					if (matcher.find()) {
						mspt = (int) (1000 / Float.parseFloat(matcher.group(0)));
					}
				} else {
					if (tabFooter != null) {
						base.appendSibling(tabFooter).appendText("\n");
					}
					final ITextComponent tMSPT = new TextComponentString("MSPT: ");
					final ITextComponent tTPS = new TextComponentString("TPS: ");
					final ITextComponent MSPT = new TextComponentString(Integer.toString(mspt));
					final ITextComponent TPS = new TextComponentString(Integer.toString(tps));

					tMSPT.getStyle().setColor(TextFormatting.GRAY);
					tTPS.getStyle().setColor(TextFormatting.GRAY);
					MSPT.getStyle().setColor(CutelessModUtils.returnColourForMSPT(mspt));
					TPS.getStyle().setColor(CutelessModUtils.returnColourForTPS(tps));

					base.appendSibling(tMSPT).appendSibling(MSPT).appendSibling(new TextComponentString(" ")).appendSibling(tTPS).appendSibling(TPS);
					mc.ingameGUI.getTabList().setFooter(base);
				}
			}
			for (int i = sendPackets.length - 1; i > 0; --i) {
				sendPackets[i] = sendPackets[i - 1];
			}
			sendPackets[0] = sendPacketsThisTick;
			for (int i = receivedPackets.length - 1; i > 0; --i) {
				receivedPackets[i] = receivedPackets[i - 1];
			}
			receivedPackets[0] = receivedPacketsThisTick;
			sendPacketsThisTick = 0;
			receivedPacketsThisTick = 0;
			if (mc.isIntegratedServerRunning() || mc.getCurrentServerData() != null) {
				Controller.tick();
				loggedOut = true;
			} else if (loggedOut) {
				loggedOut = false;
				GuiChunkGrid.instance = new GuiChunkGrid();
			}
			PistonHelper.updatePistonMovement(mc.world);
			FrequencyAnalyzer.update();
		}

		final EntityPlayerSP player = mc.player;
		if (player == null) {
			return;
		}

		stepAssistHelper.update(player);
		if (Configuration.noFall && player.fallDistance > 2F && !player.isElytraFlying()) {
			player.connection.sendPacket(new CPacketPlayer(true));
		}

		if (Configuration.flightInertiaCancellation && player.capabilities.isFlying) {
			final GameSettings settings = mc.gameSettings;
			if (!(GameSettings.isKeyDown(settings.keyBindForward) || GameSettings.isKeyDown(settings.keyBindBack) || GameSettings.isKeyDown(settings.keyBindLeft) || GameSettings.isKeyDown(settings.keyBindRight))) {
				player.motionX = player.motionZ = 0D;
			}
		}
	}

	@SubscribeEvent
	public void onLeftClickEmpty(final PlayerInteractEvent.LeftClickEmpty event) {
		if (Configuration.worldEditCompass && mc.world != null && mc.player.isCreative() && mc.player.getHeldItemMainhand().getItem() instanceof ItemCompass) {
			boolean teleported = false;
			boolean stopInAir = CutelessModUtils.isShiftKeyDown();
			int distance = stopInAir ? 50 : 500;
			Vec3d vec3d = mc.player.getPositionEyes(mc.getRenderPartialTicks());
			Vec3d vec3d1 = mc.player.getLook(mc.getRenderPartialTicks());
			Vec3d vec3d2 = vec3d.add(vec3d1.x * 500, vec3d1.y * 500, vec3d1.z * 500);
			RayTraceResult rayTraceResult = CutelessModUtils.rayTrace(vec3d, vec3d2, distance, true, stopInAir);
			if (rayTraceResult != null) {
				BlockPos destinationBlock = rayTraceResult.getBlockPos();
				if (stopInAir) {
					for (int y = 0; y < 2; y++) {
						for (int z = 0; z < 2; z++) {
							for (float i = 0; i < 1.25; i += 0.25) {
								mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, true, (float) destinationBlock.getX() + i, (float) destinationBlock.getY() + y, (float) destinationBlock.getZ() + z, 0.005, 0.005, 0.005, 5);
							}
						}
					}
					for (int x = 0; x < 2; x++) {
						for (int z = 0; z < 2; z++) {
							for (float i = 0; i < 1.25; i += 0.25) {
								mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, true, (float) destinationBlock.getX() + x, (float) destinationBlock.getY() + i, (float) destinationBlock.getZ() + z, 0.005, 0.005, 0.005, 5);
							}
						}
					}
					for (int y = 0; y < 2; y++) {
						for (int x = 0; x < 2; x++) {
							for (float i = 0; i < 1.25; i += 0.25) {
								mc.world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, true, (float) destinationBlock.getX() + x, (float) destinationBlock.getY() + y, (float) destinationBlock.getZ() + i, 0.005, 0.005, 0.005, 5);
							}
						}
					}
					destinationBlock = destinationBlock.up();
					if (mc.world.getBlockState(destinationBlock).getCollisionBoundingBox(mc.world, destinationBlock) == null && mc.world.getBlockState(destinationBlock.up()).getCollisionBoundingBox(mc.world, destinationBlock.up()) == null) {
						mc.player.connection.sendPacket(new CPacketChatMessage("/tp " + destinationBlock.getX() + " " + destinationBlock.getY() + " " + destinationBlock.getZ()));
						teleported = true;
					}
				} else {
					destinationBlock = destinationBlock.up();
					while (destinationBlock.getY() <= mc.world.getHeight()) {
						if (mc.world.getBlockState(destinationBlock).getCollisionBoundingBox(mc.world, destinationBlock) == null && mc.world.getBlockState(destinationBlock.up()).getCollisionBoundingBox(mc.world, destinationBlock.up()) == null) {
							mc.player.connection.sendPacket(new CPacketChatMessage("/tp " + destinationBlock.getX() + " " + destinationBlock.getY() + " " + destinationBlock.getZ()));
							teleported = true;
							break;
						}
						destinationBlock = destinationBlock.up();
					}
				}
			}
			if (!teleported) {
				TextComponentTranslation error = new TextComponentTranslation("text.cutelessmod.error_while_teleporting");
				error.getStyle().setColor(TextFormatting.RED);
				mc.player.sendMessage(error);
			}
		}
	}

	@SubscribeEvent
	public void onRightClickEmpty(final PlayerInteractEvent.RightClickEmpty event) {
		Item itemInHand = mc.player.getHeldItemMainhand().getItem();
		if (Configuration.worldEditCompass && mc.world != null && mc.player.isCreative() && itemInHand instanceof ItemCompass && guiCompass != null) {
			if (guiCompass.isMenuActive()) {
				guiCompass.exit();
			} else {
				guiCompass.onRightClick();
			}
		}
	}

	@SubscribeEvent
	public void onGuiChanged(final GuiOpenEvent event) {
		if (event.getGui() instanceof GuiMultiplayer) {
			this.updateTitle();
		} else if (mc.currentScreen instanceof GuiScreenOptionsSounds) {
			if (!CutelessMod.enableSounds && CutelessMod.shouldHaveSounds()) {
				CutelessMod.enableSounds = true;
				((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(mc.getSoundHandler());
			}
		}
	}

	@SubscribeEvent
	public void replaceBlockMidair(final PlayerInteractEvent.LeftClickBlock event) {
		if (mc.player.isCreative() && GuiScreen.isAltKeyDown() && event.getWorld().isRemote) {
			mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(event.getPos(), event.getFace(), EnumHand.MAIN_HAND, (float) event.getHitVec().x, (float) event.getHitVec().y, (float) event.getHitVec().z));
		}
	}

	@SubscribeEvent
	public void showWorldFog(final EntityViewRenderEvent.RenderFogEvent event) {
		if (!Configuration.showWorldFog) {
			final float renderDistance = 16F * Minecraft.getMinecraft().gameSettings.renderDistanceChunks;
			GlStateManager.setFogStart(renderDistance * 1.6F);
			GlStateManager.setFogEnd(renderDistance * 2F);
		}
	}

	@SubscribeEvent
	public void chickenAlert(final EntityJoinWorldEvent event) {
		final World world = event.getWorld();
		if (!world.isRemote) {
			return;
		}

		final Entity entity = event.getEntity();
		final BlockPos pos = entity.getPosition();
		if (!(entity instanceof EntityChicken) || !world.isAnyPlayerWithinRangeAt(pos.getX(), pos.getY(), pos.getZ(), 8D)) {
			return;
		}

		if (!world.getEntitiesWithinAABB(EntityChicken.class, entity.getEntityBoundingBox().grow(3D, 1.5D, 3D)).isEmpty()) {
			mc.ingameGUI.displayTitle("Duplicate chicken", "", -1, -1, -1);
		}

	}

	private void updateTitle() {
		Display.setTitle(this.originalTitle + " - " + mc.getSession().getUsername());
	}

	private void debugFeedback() {
		final ITextComponent tag = new TextComponentTranslation("debug.prefix");
		final ITextComponent text = new TextComponentTranslation("text.cutelessmod.reload_audio");
		tag.setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(true));
		final ITextComponent message = new TextComponentString("").appendSibling(tag).appendText(" ").appendSibling(text);
		mc.ingameGUI.getChatGUI().printChatMessage(message);
	}
}
