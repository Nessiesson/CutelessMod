package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.world.World;

public class Handler {
	protected static final Minecraft mc = Minecraft.getMinecraft();
	public static boolean gamerulePermission = false;
	public static boolean sendCommandfeedback = true;
	public static boolean logAdminCommands = true;
	public static boolean doTileDrops = true;
	public boolean finished = false;
	public boolean sendAffectedBlocks = false;
	protected long affectedBlocks = 0;
	protected World world;

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

	public void tick() {
	}

}
