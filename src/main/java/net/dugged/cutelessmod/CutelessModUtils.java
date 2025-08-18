package net.dugged.cutelessmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;

public class CutelessModUtils {
	private static final Minecraft mc = Minecraft.getMinecraft();

	public static TextFormatting returnColourForMSPT(final int mspt) {
		if (mspt <= 40) {
			return TextFormatting.GREEN;
		} else if (mspt <= 45) {
			return TextFormatting.YELLOW;
		} else if (mspt <= 50) {
			return TextFormatting.GOLD;
		} else {
			return TextFormatting.RED;
		}
	}

	public static TextFormatting returnColourForTPS(final int tps) {
		if (tps >= 20) {
			return TextFormatting.GREEN;
		} else if (tps >= 15) {
			return TextFormatting.GOLD;
		} else {
			return TextFormatting.RED;
		}
	}

	public static RayTraceResult rayTrace(Vec3d vec31, Vec3d vec32, int dist, boolean displayRaytrace, boolean stopMidAir) {
		World world = mc.world;
		if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
			if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
				int i = MathHelper.floor(vec32.x);
				int j = MathHelper.floor(vec32.y);
				int k = MathHelper.floor(vec32.z);
				int l = MathHelper.floor(vec31.x);
				int i1 = MathHelper.floor(vec31.y);
				int j1 = MathHelper.floor(vec31.z);
				BlockPos blockpos = new BlockPos(l, i1, j1);
				IBlockState iblockstate = world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();
				if ((iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, false)) {
					RayTraceResult raytraceresult = iblockstate.collisionRayTrace(world, blockpos, vec31, vec32);

					if (raytraceresult != null) {
						return raytraceresult;
					}
				}

				RayTraceResult raytraceresult2 = null;
				int k1 = dist;

				while (k1-- >= 0) {
					if (Double.isNaN(vec31.x) || Double.isNaN(vec31.y) || Double.isNaN(vec31.z)) {
						return null;
					}
					if (l == i && i1 == j && j1 == k) {
						return raytraceresult2;
					}

					boolean flag2 = true;
					boolean flag = true;
					boolean flag1 = true;
					double d0 = 999.0D;
					double d1 = 999.0D;
					double d2 = 999.0D;

					if (i > l) {
						d0 = (double) l + 1.0D;
					} else if (i < l) {
						d0 = (double) l + 0.0D;
					} else {
						flag2 = false;
					}

					if (j > i1) {
						d1 = (double) i1 + 1.0D;
					} else if (j < i1) {
						d1 = (double) i1 + 0.0D;
					} else {
						flag = false;
					}

					if (k > j1) {
						d2 = (double) j1 + 1.0D;
					} else if (k < j1) {
						d2 = (double) j1 + 0.0D;
					} else {
						flag1 = false;
					}

					double d3 = 999.0D;
					double d4 = 999.0D;
					double d5 = 999.0D;
					double d6 = vec32.x - vec31.x;
					double d7 = vec32.y - vec31.y;
					double d8 = vec32.z - vec31.z;

					if (flag2) {
						d3 = (d0 - vec31.x) / d6;
					}
					if (flag) {
						d4 = (d1 - vec31.y) / d7;
					}
					if (flag1) {
						d5 = (d2 - vec31.z) / d8;
					}
					if (d3 == -0.0D) {
						d3 = -1.0E-4D;
					}
					if (d4 == -0.0D) {
						d4 = -1.0E-4D;
					}
					if (d5 == -0.0D) {
						d5 = -1.0E-4D;
					}

					EnumFacing enumfacing;

					if (d3 < d4 && d3 < d5) {
						enumfacing = i > l ? EnumFacing.WEST : EnumFacing.EAST;
						vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
					} else if (d4 < d5) {
						enumfacing = j > i1 ? EnumFacing.DOWN : EnumFacing.UP;
						vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
					} else {
						enumfacing = k > j1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
						vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
					}

					l = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
					i1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
					j1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
					blockpos = new BlockPos(l, i1, j1);
					IBlockState iblockstate1 = world.getBlockState(blockpos);
					Block block1 = iblockstate1.getBlock();
					if (displayRaytrace && k1 <= dist - 1) {
						mc.world.spawnParticle(EnumParticleTypes.CRIT, true, vec31.x, vec31.y, vec31.z, 0, 0, 0, 5);
					}
					if (iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
						if (block1.canCollideCheck(iblockstate1, false)) {
							RayTraceResult raytraceresult1 = iblockstate1.collisionRayTrace(world, blockpos, vec31, vec32);
							return raytraceresult1;
						} else {
							raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
						}
					} else if (stopMidAir) {
						raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
					}
				}
				return raytraceresult2;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static void drawString(float partialTicks, String str, float x, float y, float z, int verticalShift) {
		final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
		final EntityPlayerSP player = Minecraft.getMinecraft().player;
		final RenderManager rm = Minecraft.getMinecraft().getRenderManager();
		final double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(x - d0, y - d1, z - d2);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-rm.playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (rm.options.thirdPersonView == 2 ? -1 : 1) * rm.playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthFunc(GL11.GL_ALWAYS);
		fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, verticalShift, -1);
		GlStateManager.depthFunc(GL11.GL_LEQUAL);
		GlStateManager.enableLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public static void drawCube(float partialTicks, BlockPos pos, float r, float g, float b) {
		if (pos != null) {
			Minecraft mc = Minecraft.getMinecraft();
			final EntityPlayerSP player = mc.player;
			final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			GlStateManager.depthMask(false);
			GlStateManager.disableFog();
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.glLineWidth(4F);
			AxisAlignedBB posBB = new AxisAlignedBB(pos).offset(-d1, -d2, -d3);
			RenderGlobal.drawSelectionBoundingBox(posBB, r, g, b, 1.0F);
			GlStateManager.glLineWidth(1F);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableFog();
			GlStateManager.depthMask(true);
		}
	}

	public static void drawCube(float partialTicks, BlockPos pos, int r, int g, int b) {
		drawCube(partialTicks, pos, r / 255.0F, g / 255.0F, b / 255.0F);
	}

	// https://stackoverflow.com/a/8545681
	public static int getMostCommon(int[] a) {
		Arrays.sort(a);
		int previous = a[0];
		int popular = a[0];
		int count = 1;
		int maxCount = 1;
		for (int i = 1; i < a.length; i++) {
			if (a[i] == previous)
				count++;
			else {
				if (count > maxCount) {
					popular = a[i - 1];
					maxCount = count;
				}
				previous = a[i];
				count = 1;
			}
		}
		return count > maxCount ? a[a.length - 1] : popular;
	}
}
