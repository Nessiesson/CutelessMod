package net.dugged.cutelessmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;

public class BeaconAreaRenderer {
	public static void render(float partialTicks) {
		GlStateManager.depthMask(false);
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		GlStateManager.disableCull();
		GlStateManager.disableBlend();
		GlStateManager.glLineWidth(3F);
		final Minecraft mc = Minecraft.getMinecraft();
		final EntityPlayerSP player = mc.player;
		final double playerX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double playerY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double playerZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		for (AxisAlignedBB axisalignedbb : CutelessMod.beaconsToRender.keySet()) {

			RenderGlobal.drawSelectionBoundingBox(axisalignedbb.offset(-playerX, -playerY, -playerZ), 0.9F, 0.9F, 0.9F, 1F);
		}
		CutelessMod.beaconsToRender.entrySet().removeIf(entry -> 1 > entry.getValue());
		GlStateManager.glLineWidth(1F);
		GlStateManager.enableTexture2D();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.depthMask(true);
	}
}
