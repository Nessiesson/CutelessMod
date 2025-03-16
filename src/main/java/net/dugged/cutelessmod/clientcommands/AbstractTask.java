package net.dugged.cutelessmod.clientcommands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.math.BlockPos;

public abstract class AbstractTask {

	public static BlockPos getMinPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()),
			Math.min(pos1.getZ(), pos2.getZ()));
	}

	public static BlockPos getMaxPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()),
			Math.max(pos1.getZ(), pos2.getZ()));
	}

	public abstract boolean isComplete();

	protected void sendCommand(String cmd) {
		Minecraft.getMinecraft().player.connection.sendPacket(new CPacketChatMessage(cmd));
	}
}
