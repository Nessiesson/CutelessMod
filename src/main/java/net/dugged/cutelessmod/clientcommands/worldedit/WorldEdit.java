package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WorldEdit {
	private static final Minecraft mc = Minecraft.getMinecraft();
	private static final EntityPlayerSP player = mc.player;
	public static BlockPos posA = null;
	public static BlockPos posB = null;

	public static boolean hasSelection() {
		return posA != null && posB != null;
	}

	public static BlockPos playerPos() {
		return new BlockPos(player.posX, player.posY, player.posZ);
	}

	public static void sendMessage(String msg) {
		sendMessage(new TextComponentTranslation(msg));
	}

	public static void sendMessage(TextComponentTranslation msg) {
		msg.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
	}

	public static BlockPos getMinPos() {
		return new BlockPos(Math.min(posA.getX(), posB.getX()), Math.min(posA.getY(), posB.getY()), Math.min(posA.getZ(), posB.getZ()));
	}

	public static BlockPos getMaxPos() {
		return new BlockPos(Math.max(posA.getX(), posB.getX()), Math.max(posA.getY(), posB.getY()), Math.max(posA.getZ(), posB.getZ()));
	}

	public static int widthX() {
		return getMaxPos().getX() - getMinPos().getX() + 1;
	}

	public static int widthY() {
		return getMaxPos().getY() - getMinPos().getY() + 1;
	}

	public static int widthZ() {
		return getMaxPos().getZ() - getMinPos().getZ() + 1;
	}

	public static long volume() {
		return widthX() * widthY() * widthZ();
	}
}
