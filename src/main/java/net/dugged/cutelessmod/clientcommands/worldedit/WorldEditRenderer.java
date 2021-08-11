package net.dugged.cutelessmod.clientcommands.worldedit;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class WorldEditRenderer {
	public static void render(float partialTicks) {
		if (WorldEdit.posA != null | WorldEdit.posB != null) {
			final EntityPlayerSP player = Minecraft.getMinecraft().player;
			final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
			final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
			final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

			GlStateManager.depthFunc(GL11.GL_ALWAYS);
			GlStateManager.disableTexture2D();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			GlStateManager.disableBlend();
			GlStateManager.glLineWidth(3F);
			if (WorldEdit.posA != null) {
				AxisAlignedBB posBB = new AxisAlignedBB(WorldEdit.posA).offset(-d1, -d2, -d3);
				RenderGlobal.drawSelectionBoundingBox(posBB, 1F, 0F, 0F, 0.6F);
			}
			if (WorldEdit.posB != null) {
				AxisAlignedBB posBB = new AxisAlignedBB(WorldEdit.posB).offset(-d1, -d2, -d3);
				RenderGlobal.drawSelectionBoundingBox(posBB, 0F, 0F, 1F, 0.6F);
			}
			GlStateManager.depthFunc(GL11.GL_LEQUAL);
			if (WorldEdit.posA != null && WorldEdit.posB != null) {
				AxisAlignedBB posBB = new AxisAlignedBB(WorldEdit.posA, WorldEdit.posB).offset(-d1, -d2, -d3).expand(1, 1, 1).grow(0.0075F);
				//Purple BB to differentiate from structure block
				RenderGlobal.drawSelectionBoundingBox(posBB, 1.0F, 85.0F / 255, 1.0F, 0.6F);
			}
			GlStateManager.glLineWidth(1F);
			GlStateManager.enableTexture2D();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
		}
	}
}
