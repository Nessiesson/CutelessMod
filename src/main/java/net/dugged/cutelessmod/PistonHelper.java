package net.dugged.cutelessmod;

import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonMoving;
import net.minecraft.block.BlockSlime;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

// Made by EDDxample for carpet client
// Source: https://github.com/X-com/CarpetClient/blob/master/src/main/java/carpetclient/coders/EDDxample/PistonHelper.java

public class PistonHelper {

	private static final String gold = "\u00a76", red = "\u00a74===", green = "\u00a72Blocks", pushe = "\u00a76Pushes", pull = "\u00a76Pull";

	public static boolean validState, activated, extending;
	public static BlockPos pistonPos;
	private static BlockPos[] tobreak, tomove;

	public static void setPistonMovement(World worldIn, IBlockState state, BlockPos pos, boolean extending) {
		EnumFacing enumfacing = state.getValue(BlockDirectional.FACING);
		IBlockState state1 = worldIn.getBlockState(pos.offset(enumfacing));
		BlockPistonStructureHelper ph;

		//Weird trick to remove the piston head
		if (!extending) {
			worldIn.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
			worldIn.setBlockToAir(pos);
			worldIn.setBlockToAir(pos.offset(enumfacing));
		}

		ph = new BlockPistonStructureHelper(worldIn, pos, enumfacing, extending);
		boolean canMove = ph.canMove();
		ph.canMove();
		PistonHelper.set(pos, ph.getBlocksToMove().toArray(new BlockPos[0]), ph.getBlocksToDestroy().toArray(new BlockPos[0]), canMove, extending);
		PistonHelper.activated = true;

		//Weird trick to add the piston head back
		if (!extending) {
			worldIn.setBlockState(pos, state, 2);
			worldIn.setBlockState(pos.offset(enumfacing), state1, 2);
		}
	}

	public static void updatePistonMovement(World worldIn) {
		if (activated) {
			IBlockState blockState = worldIn.getBlockState(pistonPos);
			if (blockState.getBlock() instanceof BlockPistonBase || blockState.getBlock() instanceof BlockSlime) {
				setPistonMovement(worldIn, blockState, pistonPos, !blockState.getValue(BlockPistonBase.EXTENDED));
			} else if (!(blockState.getBlock() instanceof BlockPistonMoving)) {
				activated = false;
			}
		}
	}

	private static void set(BlockPos posIn, BlockPos[] btm, BlockPos[] btb, boolean isValid, boolean _extending) {
		pistonPos = posIn;
		tomove = btm;
		tobreak = btb;
		validState = isValid;
		extending = _extending;
	}

	public static void draw(float partialTicks) {
		if (Configuration.showPistonOrder && activated) {
			final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			final double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			final double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			BlockPos pos;

			int count = 0;
			for (int i = 1; i <= tobreak.length; i++) {
				pos = tobreak[tobreak.length - i];
				if (pos != null) {
					count++;
					drawString("\u00a7c" + count, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.5f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
				}
			}
			int moved = -count;
			for (int i = 1; i <= tomove.length; i++) {
				pos = tomove[tomove.length - i];
				if (pos != null) {
					count++;
					drawString(Integer.toString(count), (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.5f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
				}
			}
			moved += count;
			pos = pistonPos;
			if (validState) {
				if (extending) {
					drawString(pushe, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.8f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
				} else {
					drawString(pull, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.8f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
				}
				drawString(green, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.2f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
			} else {
				if (extending) {
					drawString(pushe, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.8f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
				} else {
					drawString(pull, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.8f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
				}
				drawString(red, (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.2f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
			}
			drawString(gold + (Math.max(moved, 0)), (float) (pos.getX() + 0.5f - d0), (float) (pos.getY() + 0.5f - d1), (float) (pos.getZ() + 0.5f - d2), 0, rm.playerViewY, rm.playerViewX, rm.options.thirdPersonView == 2);
		}
	}

	public static void drawString(String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal) {
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthFunc(GL11.GL_ALWAYS);
		fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, verticalShift, -1);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
