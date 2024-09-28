package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

public class RandomTickHelper {

	private static boolean visible = false;
	private static float x;
	private static float y;
	private static float z;
	private static final int RANGE = 128;
	private static final int DISTANCE_SQ = RANGE * RANGE;


	public static void render(float partialTicks) {
		if (visible) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final double d1 =
				player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			final double d2 =
				player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			final double d3 =
				player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			CutelessModUtils.drawString(partialTicks, "Random Tick Renderer", x, y+0.4f, z, 0);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.glLineWidth(3F);
			AxisAlignedBB bb = new AxisAlignedBB(x - 0.01, y, z - 0.01, x + 0.01, y + 0.4,
				z + 0.01).offset(-d1, -d2, -d3);
			RenderGlobal.drawSelectionBoundingBox(bb, 1.0f, 0.0f, 0.0f, 1.0f);
			int minChunkX = (int) Math.floor((x - RANGE - 8) / 16.0);
			int maxChunkX = (int) Math.floor((x + RANGE - 8) / 16.0);
			int minChunkZ = (int) Math.floor((z - RANGE - 8) / 16.0);
			int maxChunkZ = (int) Math.floor((z + RANGE - 8) / 16.0);
			for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
				for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
					if ((chunkX * 16 + 8 - x) * (chunkX * 16 + 8 - x)
						+ (chunkZ * 16 + 8 - z) * (chunkZ * 16 + 8 - z) < DISTANCE_SQ && (
						(chunkX * 16 + 24 - x) * (chunkX * 16 + 24 - x)
							+ (chunkZ * 16 + 8 - z) * (chunkZ * 16 + 8 - z) >= DISTANCE_SQ ||
							(chunkX * 16 - 8 - x) * (chunkX * 16 - 8 - x)
								+ (chunkZ * 16 + 8 - z) * (chunkZ * 16 + 8 - z) >= DISTANCE_SQ ||
							(chunkX * 16 + 8 - x) * (chunkX * 16 + 8 - x)
								+ (chunkZ * 16 + 24 - z) * (chunkZ * 16 + 24 - z) >= DISTANCE_SQ ||
							(chunkX * 16 + 8 - x) * (chunkX * 16 + 8 - x)
								+ (chunkZ * 16 - 8 - z) * (chunkZ * 16 - 8 - z) >= DISTANCE_SQ)) {
						bb = new AxisAlignedBB(chunkX * 16, 0, chunkZ * 16, (chunkX + 1) * 16, 255,
							(chunkZ + 1) * 16).offset(-d1, -d2, -d3);
						RenderGlobal.drawSelectionBoundingBox(bb, 1.0f, 0.0f, 0.0f, 1.0f);
					}
				}
			}
			GlStateManager.enableBlend();
			GlStateManager.glLineWidth(1F);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
		}
	}


	public static void updatePosition(EntityPlayer player) {
		if ((float) player.posX == x && (float) player.posY == y && (float) player.posZ == z) {
			visible = false;
		} else {
			x = (float) player.posX;
			y = (float) player.posY;
			z = (float) player.posZ;
			visible = true;
		}
	}
}
