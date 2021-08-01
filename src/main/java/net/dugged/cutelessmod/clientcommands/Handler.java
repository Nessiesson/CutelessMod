package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class Handler {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	public static boolean gamerulePermission = false;
	public static boolean sendCommandfeedback = true;
	public static boolean logAdminCommands = true;
	public static boolean doTileDrops = true;
	public boolean finished = false;
	public boolean sendAffectedBlocks = true;
	public boolean isWorldEditHandler = true;
	public boolean running = true;
	protected long affectedBlocks = 0;
	protected int totalCount;
	protected int currentCount;
	protected World world;
	protected long age = 0;
	protected long last_execution = 0;
	private boolean warned = false;

	public Handler(World worldIn) {
		world = worldIn;
		if (gamerulePermission) {
			if (sendCommandfeedback) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule sendCommandFeedback false"));
			}
			if (logAdminCommands) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule logAdminCommands false"));
			}
			if (doTileDrops) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule doTileDrops false"));
			}
		}
	}

	public static void getGameruleStates() {
		if (mc.player != null && mc.player.connection != null) {
			gamerulePermission = false;
			mc.player.connection.sendPacket(new CPacketTabComplete("/gamerul", null, false));
			if (gamerulePermission) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule doTileDrops"));
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule sendCommandFeedback"));
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule logAdminCommands"));
			}
		}
	}

	public float getProgress() {
		return (float) currentCount / totalCount;
	}

	public void finish() {
		if (gamerulePermission && !ClientCommandHandler.instance.otherHandlersRunning(this)) {
			if (doTileDrops) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule doTileDrops true"));
			}
			if (logAdminCommands) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule logAdminCommands true"));
			}
			if (sendCommandfeedback) {
				mc.player.connection.sendPacket(new CPacketChatMessage("/gamerule sendCommandFeedback true"));
			}
		}
		getGameruleStates();
		finished = true;
	}

	synchronized public void tick() {
		if (age - last_execution > 300 && !warned) {
			warned = true;
			TextComponentTranslation warning = new TextComponentTranslation("text.cutelessmod.clientcommands.handlerAgeWarning", getClass());
			if (isWorldEditHandler) {
				warning.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
			} else {
				warning.getStyle().setColor(TextFormatting.RED);
			}
			mc.ingameGUI.getChatGUI().printChatMessage(warning);
		}
		if (age - last_execution > 600 && warned) {
			TextComponentTranslation error = new TextComponentTranslation("text.cutelessmod.clientcommands.handlerTermination", getClass());
			if (isWorldEditHandler) {
				error.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
			} else {
				error.getStyle().setColor(TextFormatting.RED);
			}
			mc.ingameGUI.getChatGUI().printChatMessage(error);
			finished = true;
		} else {
			warned = false;
		}
		age++;
	}
}
