package net.dugged.cutelessmod;

import net.dugged.cutelessmod.mixins.ISoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mod(modid = Reference.MODID, name = Reference.NAME, version = Reference.VERSION, clientSideOnly = true)
public class CutelessMod {
	public static final KeyBinding highlightEntities = new KeyBinding("key.cutelessmod.highlight_entities", KeyConflictContext.IN_GAME, Keyboard.KEY_C, Reference.NAME);
	private static final KeyBinding emptyScreenKey = new KeyBinding("key.cutelessmod.emptyscreen", KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, Reference.NAME);
	private static final KeyBinding reloadAudioEngineKey = new KeyBinding("key.cutelessmod.reload_audio", KeyConflictContext.IN_GAME, Keyboard.KEY_B, Reference.NAME);
	private static final KeyBinding spyKey = new KeyBinding("key.cutelessmod.spy", KeyConflictContext.IN_GAME, Keyboard.KEY_Y, Reference.NAME);
	private static final KeyBinding toggleBeaconAreaKey = new KeyBinding("key.cutelessmod.toggle_beacon_area", KeyConflictContext.IN_GAME, Keyboard.KEY_J, Reference.NAME);
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final StepAssistHelper stepAssistHelper = new StepAssistHelper();
	public static Map<AxisAlignedBB, Integer> beaconsToRender = new HashMap<>();
	public static Map<String, List<ChatLine>> chatHistory = new HashMap<>();
	public static Map<String, List<String>> tabCompleteHistory = new HashMap<>();
	public static boolean toggleBeaconArea = false;
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
	private String originalTitle;
	private static final List<KeyBinding> keybinds = new ArrayList<>();

	@Mod.EventHandler
	public void preInit(final FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		this.originalTitle = Display.getTitle();
		updateTitle();
	}

	@Mod.EventHandler
	public void init(final FMLInitializationEvent event) {
		final List<Field> fields = Arrays.stream(Configuration.class.getDeclaredFields()).filter(f -> boolean.class.equals(f.getType())).sorted(Comparator.comparing(Field::getName)).collect(Collectors.toList());
		for (final Field f : fields) {
			keybinds.add(new KeyBinding(f.getName(), Keyboard.KEY_NONE, Reference.NAME));
		}

		for (final KeyBinding key : keybinds) {
			ClientRegistry.registerKeyBinding(key);
		}

		ClientRegistry.registerKeyBinding(highlightEntities);
		ClientRegistry.registerKeyBinding(emptyScreenKey);
		ClientRegistry.registerKeyBinding(reloadAudioEngineKey);
		ClientRegistry.registerKeyBinding(spyKey);
		ClientRegistry.registerKeyBinding(toggleBeaconAreaKey);
		spy = new ContainerSpy();
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
			toggleBeaconArea = !toggleBeaconArea;
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
	}

	@SubscribeEvent
	public void onLoadWorld(final WorldEvent.Load event) {
		tabFooter = null;
	}

	@SubscribeEvent
	public void onTick(final TickEvent.ClientTickEvent event) {
		tickCounter++;
		if (overlayTimer > 0) {
			overlayTimer--;
		}
		if (mc.world != null) {
			if (!mc.isIntegratedServerRunning()) {
				currentServer = mc.getCurrentServerData();
			}
			if (mspt > 0) {
				final int tps = Math.min(20, 1000 / mspt);
				ITextComponent base = new TextComponentString("");
				if (tabFooter != null && tabFooter.getUnformattedText().matches("(?s).*TPS: \\d*\\.\\d* MSPT: \\d*\\.\\d*.*")) {
					mc.ingameGUI.getTabList().setFooter(tabFooter);
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

			for (AxisAlignedBB axisalignedbb : CutelessMod.beaconsToRender.keySet()) {
				CutelessMod.beaconsToRender.put(axisalignedbb, CutelessMod.beaconsToRender.get(axisalignedbb) - 1);
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
		}

		if (event.phase == TickEvent.Phase.END) {
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
