package net.dugged.cutelessmod;

import net.dugged.cutelessmod.chunk_display.CarpetPluginChannel;
import net.dugged.cutelessmod.chunk_display.gui.Controller;
import net.dugged.cutelessmod.chunk_display.gui.GuiChunkGrid;
import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.dugged.cutelessmod.clientcommands.mixins.IItemSword;
import net.dugged.cutelessmod.clientcommands.worldedit.BrushBase;
import net.dugged.cutelessmod.clientcommands.worldedit.WorldEdit;
import net.dugged.cutelessmod.mixins.ISoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemCompass;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.*;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
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

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.A;
import static net.dugged.cutelessmod.clientcommands.worldedit.WorldEditSelection.Position.B;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, clientSideOnly = true)
public class CutelessMod {
	public static final Logger LOGGER = LogManager.getLogger(Reference.NAME);
	public static final KeyBinding highlightEntities = new KeyBinding("key.cutelessmod.highlight_entities", KeyConflictContext.IN_GAME, Keyboard.KEY_C, Reference.NAME);
	private static final KeyBinding emptyScreenKey = new KeyBinding("key.cutelessmod.emptyscreen", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private static final KeyBinding reloadAudioEngineKey = new KeyBinding("key.cutelessmod.reload_audio", KeyConflictContext.IN_GAME, Keyboard.KEY_B, Reference.NAME);
	private static final KeyBinding repeatLastCommandKey = new KeyBinding("key.cutelessmod.repeat_last_command", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private static final KeyBinding spyKey = new KeyBinding("key.cutelessmod.spy", KeyConflictContext.IN_GAME, Keyboard.KEY_Y, Reference.NAME);
	private static final KeyBinding toggleBeaconAreaKey = new KeyBinding("key.cutelessmod.toggle_beacon_area", KeyConflictContext.IN_GAME, Keyboard.KEY_J, Reference.NAME);
	private static final KeyBinding chunkDebug = new KeyBinding("key.cutelessmod.chunk_debug", KeyConflictContext.IN_GAME, Keyboard.KEY_F6, Reference.NAME);
	private static final KeyBinding putItemCounterKey = new KeyBinding("key.cutelessmod.put_item_counter", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private static final KeyBinding resetItemCounterKey = new KeyBinding("key.cutelessmod.reset_item_counter", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private static final KeyBinding putFrequencyAnalyzerKey = new KeyBinding("key.cutelessmod.put_frequency_analyzer", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final StepAssistHelper stepAssistHelper = new StepAssistHelper();
	private static final List<KeyBinding> keybinds = new ArrayList<>();
	public static Map<String, List<ChatLine>> chatHistory = new HashMap<>();
	public static Map<String, List<String>> tabCompleteHistory = new HashMap<>();
	public static Map<BlockPos, ChunkPos> dungeonPositions = new ConcurrentHashMap<>();
	public static int toggleBeaconArea = 0;
	public static long lastTimeUpdate;
	public static ContainerSpy spy;
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
	private final CarpetPluginChannel carpetPluginChannel = new CarpetPluginChannel();
	private String originalTitle;
	private long swordCooldown = 0;
	private boolean loggedOut;

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		this.originalTitle = Display.getTitle();
		updateTitle();
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		Arrays.stream(Configuration.class.getDeclaredFields())
				.filter(f -> boolean.class.equals(f.getType()))
				.filter(f -> !(f.isAnnotationPresent(Config.RequiresMcRestart.class) || f.isAnnotationPresent(Config.RequiresWorldRestart.class)))
				.map(f -> new KeyBinding(f.getName(), Keyboard.KEY_NONE, Reference.NAME + " Autogenerated"))
				.peek(keybinds::add)
				.forEach(ClientRegistry::registerKeyBinding);

		ClientRegistry.registerKeyBinding(highlightEntities);
		ClientRegistry.registerKeyBinding(emptyScreenKey);
		ClientRegistry.registerKeyBinding(reloadAudioEngineKey);
		ClientRegistry.registerKeyBinding(repeatLastCommandKey);
		ClientRegistry.registerKeyBinding(spyKey);
		ClientRegistry.registerKeyBinding(toggleBeaconAreaKey);
		ClientRegistry.registerKeyBinding(chunkDebug);
		ClientRegistry.registerKeyBinding(putItemCounterKey);
		ClientRegistry.registerKeyBinding(resetItemCounterKey);
		ClientRegistry.registerKeyBinding(putFrequencyAnalyzerKey);

		spy = new ContainerSpy();
		ClientCommandHandler.instance.init();
		GuiChunkGrid.instance = new GuiChunkGrid();
		if (Configuration.chestWithoutTESR) {
			TileEntityRendererDispatcher.instance.renderers.remove(TileEntityChest.class);
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
		for (final KeyBinding key : keybinds) {
			if (key.isPressed()) {
				try {
					final Field field = Configuration.class.getField(key.getKeyDescription());
					final boolean state = !field.getBoolean(Configuration.class);
					field.setBoolean(Configuration.class, state);
					ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
					mc.ingameGUI.setOverlayMessage(String.format("%s %s.", field.getName(), state ? "enabled" : "disabled"), false);
				} catch (NoSuchFieldException | IllegalAccessException ignored) {
					// noop
				}
			}
		}

		if (reloadAudioEngineKey.isPressed()) {
			((ISoundHandler) mc.getSoundHandler()).getSoundManager().reloadSoundSystem();
			this.debugFeedback();
		}

		if (toggleBeaconAreaKey.isPressed()) {
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

		if (spyKey.isPressed()) {
			if (mc.player.isSneaking()) {
				spy.resetChests();
			} else {
				spy.startFindingInventories();
			}
		}

		if (emptyScreenKey.isPressed()) {
			mc.displayGuiScreen(new GuiEmptyScreen());
		}

		if (repeatLastCommandKey.isPressed() && !lastCommand.isEmpty()) {
			mc.player.sendChatMessage(CutelessMod.lastCommand);
		}

		if (chunkDebug.isPressed()) {
			mc.displayGuiScreen(GuiChunkGrid.instance);
		}

		if (putItemCounterKey.isPressed()) {
			if (ItemCounter.position != null) {
				ItemCounter.reset();
				ItemCounter.position = null;
			} else {
				ItemCounter.position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
			}
		}

		if (putFrequencyAnalyzerKey.isPressed()) {
			if (FrequencyAnalyzer.position != null) {
				FrequencyAnalyzer.reset();
				FrequencyAnalyzer.position = null;
			} else {
				mc.ingameGUI.setOverlayMessage("Analyzer might be inaccurate depending on server TPS", false);
				FrequencyAnalyzer.position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
			}
		}
		if (resetItemCounterKey.isPressed()) {
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
		if (Configuration.fixWeirdShift2Shift6LinuxBug) {
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
		ClientCommandHandler.instance.tick();
		tickCounter++;
		swordCooldown--;
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
			if (!mc.player.isSneaking() && spyKey.isKeyDown() && mc.world.getTotalWorldTime() % 10 == 0) {
				spy.startFindingInventories();
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
	public void onleftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
		Item itemInHand = mc.player.getHeldItemMainhand().getItem();
		if (mc.player.isCreative() && itemInHand instanceof ItemSword) {
			Item.ToolMaterial material = ((IItemSword) itemInHand).getMaterial();
			if (swordCooldown > tickCounter) {
				event.setCanceled(true);
			} else if (mc.player instanceof EntityPlayerSP) {
				if (WorldEdit.getPos(material, A) == null || !WorldEdit.getPos(material, A).equals(event.getPos())) {
					WorldEdit.setPos(material, A, event.getPos());
				} else {
					WorldEdit.setPos(material, A, null);
				}
				swordCooldown = tickCounter + 10;
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onRighClickBlock(final PlayerInteractEvent.RightClickBlock event) {
		Item itemInHand = mc.player.getHeldItemMainhand().getItem();
		if (mc.player.isCreative() && itemInHand instanceof ItemSword) {
			Item.ToolMaterial material = ((IItemSword) itemInHand).getMaterial();
			if (swordCooldown > tickCounter) {
				event.setCanceled(true);
			} else if (mc.player != null) {
				if (WorldEdit.getPos(material, B) == null || !WorldEdit.getPos(material, B).equals(event.getPos())) {
					WorldEdit.setPos(material, B, event.getPos());
				} else {
					WorldEdit.setPos(material, B, null);
				}
				swordCooldown = tickCounter + 10;
				event.setCanceled(true);
			}
		} else if (mc.player.isCreative() && WorldEdit.currentBrushes.containsKey(itemInHand)) {
			BrushBase brush = WorldEdit.currentBrushes.get(itemInHand);
			if (brush.getUseCooldown() <= tickCounter) {
				brush.execute(mc.world, event.getPos());
				brush.setUseCooldown(tickCounter + 2);
			}
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public void onLeftClickEmpty(final PlayerInteractEvent.LeftClickEmpty event) {
		if (Configuration.worldeditCompass && mc.world != null && mc.player.isCreative() && mc.player.getHeldItemMainhand().getItem() instanceof ItemCompass) {
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
		if (Configuration.worldeditCompass && mc.world != null && mc.player.isCreative() && itemInHand instanceof ItemCompass && guiCompass != null) {
			if (guiCompass.isMenuActive()) {
				guiCompass.exit();
			} else {
				guiCompass.onRightClick();
			}
		} else if (mc.player.isCreative() && WorldEdit.currentBrushes.containsKey(itemInHand)) {
			BrushBase brush = WorldEdit.currentBrushes.get(itemInHand);
			if (brush.getUseCooldown() <= tickCounter) {
				Vec3d vec3d = mc.player.getPositionEyes(mc.getRenderPartialTicks());
				Vec3d vec3d1 = mc.player.getLook(mc.getRenderPartialTicks());
				Vec3d vec3d2 = vec3d.add(vec3d1.x * 500, vec3d1.y * 500, vec3d1.z * 500);
				RayTraceResult rayTraceResult = CutelessModUtils.rayTrace(vec3d, vec3d2, 500, true, false);
				if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
					brush.execute(mc.world, rayTraceResult.getBlockPos());
					brush.setUseCooldown(tickCounter + 2);
				}
			}
		}
	}

	@SubscribeEvent
	public void onGuiChanged(final GuiOpenEvent event) {
		if (event.getGui() instanceof GuiMultiplayer) {
			this.updateTitle();
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
