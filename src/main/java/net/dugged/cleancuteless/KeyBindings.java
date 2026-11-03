package net.dugged.cleancuteless;

import net.dugged.cleancuteless.chunk_display.gui.GuiChunkGrid;
import net.dugged.cleancuteless.mixins.ISoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(Side.CLIENT)
public class KeyBindings {
	public static final List<KeyBinding> autoGenKeybinds = new ArrayList<>();
	private static final Map<KeyBinding, AutoKeybind> autoGenTargets = new HashMap<>();
	private static final Map<KeyBinding, Runnable> keybinds = new HashMap<>();
	private static final Minecraft mc = Minecraft.getMinecraft();

	public static final KeyBinding carpetFaceIntoKey = register("face_into");
	public static final KeyBinding carpetFlipFaceKey = register("flip_face");
	public static final KeyBinding zoomerKey = register("zoomer", Keyboard.KEY_LCONTROL);

	public static final KeyBinding chunkDebug = register("chunk_debug", Keyboard.KEY_F6, () -> mc.displayGuiScreen(GuiChunkGrid.instance));
	public static final KeyBinding emptyScreenKey = register("emptyscreen", () -> mc.displayGuiScreen(new GuiEmptyScreen()));
	public static final KeyBinding highlightEntitiesKey = register("highlight_entities", Keyboard.KEY_C, () -> CleanCuteless.highlightEntities = !CleanCuteless.highlightEntities);
	public static final KeyBinding putDespawnSphereKey = register("put_despawn_sphere", () -> DespawnSphereRenderer.getInstance().updatePosition(mc.player));
	public static final KeyBinding putRandomTickAreaKey = register("put_random_tick_area", () -> RandomTickRenderer.getInstance().updatePosition(mc.player));

	public static final KeyBinding gammaHaxKey = register("gammahax", () -> {
		mc.gameSettings.gammaSetting = 1000;
		mc.gameSettings.saveOptions();
		sendOverlayMessage("Enabled fullbright gammahax.");
	});

	public static final KeyBinding putFrequencyAnalyzerKey = register("put_frequency_analyzer", () -> {
		if (FrequencyAnalyzer.position != null) {
			FrequencyAnalyzer.reset();
			FrequencyAnalyzer.position = null;
		} else {
			sendOverlayMessage("Analyzer might be inaccurate depending on server TPS.");
			FrequencyAnalyzer.position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
		}
	});

	public static final KeyBinding putItemCounterKey = register("put_item_counter", () -> {
		if (ItemCounter.position != null) {
			ItemCounter.reset();
			ItemCounter.position = null;
		} else {
			ItemCounter.position = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
		}
	});

	public static final KeyBinding reloadAudioEngineKey = register("reload_audio", Keyboard.KEY_B, () -> {
		((ISoundHandler) mc.getSoundHandler()).getSoundManager().reloadSoundSystem();
		sendReloadAudioDebugMessage();
	});

	public static final KeyBinding repeatLastCommandKey = register("repeat_last_command", () -> {
		if (!CleanCuteless.lastCommand.isEmpty()) {
			mc.player.sendChatMessage(CleanCuteless.lastCommand);
		}
	});

	public static final KeyBinding resetItemCounterKey = register("reset_item_counter", () -> {
		sendOverlayMessage("Reset Item Counter.");
		ItemCounter.reset();
	});

	public static final KeyBinding snapaimKey = register("snapaim", () -> {
		final EntityPlayerSP player = mc.player;
		player.rotationYaw = (int) (Math.round(player.rotationYaw / 45F) * 45F);
		sendOverlayMessage("Thanos'd.");
	});

	public static final KeyBinding toggleBeaconAreaKey = register("toggle_beacon_area", Keyboard.KEY_J, () -> {
		CleanCuteless.toggleBeaconArea++;
		switch (CleanCuteless.toggleBeaconArea) {
			case 1:
				sendOverlayMessage("Showing area outlines.");
				break;
			case 2:
				sendOverlayMessage("Showing beacon areas.");
				break;
			case 3:
				sendOverlayMessage("Showing next beacon positions.");
				break;
		}

		if (CleanCuteless.toggleBeaconArea > 3) {
			CleanCuteless.toggleBeaconArea = 0;
		}
	});

	public static final KeyBinding xrayToggleKey = register("xray.toggle", () -> {
		CleanCuteless.xray.enabled = !CleanCuteless.xray.enabled;
		sendOverlayMessage(String.format("X-Ray %s.", CleanCuteless.xray.enabled ? "enabled" : "disabled"));
		mc.renderGlobal.loadRenderers();
	});

	public static void init() {
		if (Configuration.general.autoGenerateKeybinds) {
			collectAutoKeybinds();
			autoGenKeybinds.forEach(ClientRegistry::registerKeyBinding);
		}

		keybinds.keySet().forEach(ClientRegistry::registerKeyBinding);
	}

	private static void collectAutoKeybinds() {
		autoGenKeybinds.clear();
		autoGenTargets.clear();
		for (final Field categoryField : Configuration.class.getDeclaredFields()) {
			if (!Modifier.isPublic(categoryField.getModifiers()) || !Modifier.isStatic(categoryField.getModifiers())) {
				continue;
			}
			if (categoryField.getType().getEnclosingClass() != Configuration.class) {
				continue;
			}
			final Object owner;
			try {
				owner = categoryField.get(null);
			} catch (final IllegalAccessException exception) {
				continue;
			}
			if (owner == null) {
				continue;
			}
			for (final Field field : categoryField.getType().getDeclaredFields()) {
				if (!Modifier.isPublic(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
					continue;
				}
				if (!boolean.class.equals(field.getType())) {
					continue;
				}
				if (field.isAnnotationPresent(Config.RequiresMcRestart.class) || field.isAnnotationPresent(Config.RequiresWorldRestart.class)) {
					continue;
				}
				final String description = "key.cutelessmod.option." + categoryField.getName() + "." + field.getName().toLowerCase(Locale.ENGLISH);
				final KeyBinding bind = new KeyBinding(description, Keyboard.KEY_NONE, Reference.NAME + " Autogenerated");
				autoGenKeybinds.add(bind);
				autoGenTargets.put(bind, new AutoKeybind(owner, field));
			}
		}
	}

	@SubscribeEvent
	public static void onKeyPressed(final InputEvent.KeyInputEvent event) {
		for (final KeyBinding key : autoGenKeybinds) {
			if (key.isPressed()) {
				final AutoKeybind target = autoGenTargets.get(key);
				if (target == null) {
					continue;
				}
				try {
					final boolean state = !target.field.getBoolean(target.owner);
					target.field.setBoolean(target.owner, state);
					ConfigManager.sync(Reference.MODID, Config.Type.INSTANCE);
					sendOverlayMessage(I18n.format(key.getKeyDescription()) + (state ? " enabled." : " disabled."));

					if (target.owner == Configuration.ui && "showScoreboard".equals(target.field.getName())) {
						GuiIngameForge.renderObjective = state;
					}
				} catch (final IllegalAccessException ignored) {
					// noop
				}
			}
		}

		for (final Map.Entry<KeyBinding, Runnable> bind : keybinds.entrySet()) {
			if (bind.getKey().isPressed()) {
				final Runnable value = bind.getValue();
				if (value != null) {
					value.run();
				}
			}
		}
	}

	private static final class AutoKeybind {
		private final Object owner;
		private final Field field;

		private AutoKeybind(final Object owner, final Field field) {
			this.owner = owner;
			this.field = field;
		}
	}

	private static void sendOverlayMessage(final String message) {
		mc.ingameGUI.setOverlayMessage(message, false);
	}

	private static void sendReloadAudioDebugMessage() {
		final ITextComponent tag = new TextComponentTranslation("debug.prefix");
		final ITextComponent text = new TextComponentTranslation("text.cutelessmod.reload_audio");
		tag.setStyle(new Style().setColor(TextFormatting.YELLOW).setBold(true));
		final ITextComponent message = new TextComponentString("").appendSibling(tag).appendText(" ").appendSibling(text);
		mc.ingameGUI.getChatGUI().printChatMessage(message);
	}

	private static KeyBinding register(final String entry) {
		return register(entry, Keyboard.KEY_NONE);
	}

	private static KeyBinding register(final String entry, final int key) {
		return register(entry, key, null);
	}

	private static KeyBinding register(final String entry, final Runnable onPress) {
		return register(entry, Keyboard.KEY_NONE, onPress);
	}

	private static KeyBinding register(final String entry, final int key, final Runnable onPress) {
		// Historical description prefix; changing it would unbind existing controls in options.txt.
		final KeyBinding bind = new KeyBinding("key.cutelessmod." + entry, KeyConflictContext.IN_GAME, key, Reference.NAME);
		keybinds.put(bind, onPress);
		return bind;
	}
}
