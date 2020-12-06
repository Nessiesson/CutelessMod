package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class WorldEdit {
	private static final Minecraft mc = Minecraft.getMinecraft();
	public static BlockPos posA = null;
	public static BlockPos posB = null;

	public static boolean hasSelection() {
		return posA != null && posB != null;
	}

	public static BlockPos playerPos() {
		return new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
	}

	public static EnumFacing getLookingDirection() {
		Entity entity = mc.getRenderViewEntity();
		EnumFacing enumfacing = entity.getHorizontalFacing();
		if (entity.rotationPitch > 45) {
			enumfacing = EnumFacing.DOWN;
		} else if (entity.rotationPitch < -45) {
			enumfacing = EnumFacing.UP;
		}
		return enumfacing;
	}

	public static BlockPos offsetLookingDirection(BlockPos pos, int offset) {
		return pos.offset(getLookingDirection(), offset);
	}

	public static void sendMessage(String msg) {
		sendMessage(new TextComponentTranslation(msg));
	}

	public static void sendMessage(TextComponentTranslation msg) {
		msg.getStyle().setColor(TextFormatting.LIGHT_PURPLE);
		Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(msg);
	}

	public static BlockPos minPos() {
		return new BlockPos(Math.min(posA.getX(), posB.getX()), Math.min(posA.getY(), posB.getY()), Math.min(posA.getZ(), posB.getZ()));
	}

	public static BlockPos maxPos() {
		return new BlockPos(Math.max(posA.getX(), posB.getX()), Math.max(posA.getY(), posB.getY()), Math.max(posA.getZ(), posB.getZ()));
	}

	public static int widthX() {
		return maxPos().getX() - minPos().getX() + 1;
	}

	public static int widthY() {
		return maxPos().getY() - minPos().getY() + 1;
	}

	public static int widthZ() {
		return maxPos().getZ() - minPos().getZ() + 1;
	}

	public static long volume() {
		return widthX() * widthY() * widthZ();
	}

	public static boolean isOneByOne() {
		return widthX() == 1 && widthY() == 1 && widthZ() == 1;
	}

	public static boolean checkSphere(final double x, final double y, final double z, final double r) {
		return x * x + y * y + z * z <= r * r;
	}

	public static boolean checkCircle(final double x, final double z, final double r) {
		return x * x + z * z <= r * r;
	}

	public static int maxWidth() {
		return Math.max(widthX(), Math.max(widthY(), widthZ()));
	}
}
