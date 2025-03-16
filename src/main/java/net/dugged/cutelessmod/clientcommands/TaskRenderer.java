package net.dugged.cutelessmod.clientcommands;

import net.dugged.cutelessmod.CutelessMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;

public class TaskRenderer {

	public static void renderTasksFancy(float partialTicks) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		ChunkPos playerChunk = new ChunkPos(player.getPosition());
		float pulse = 0.6F + 0.4F * (float) Math.sin(CutelessMod.tickCounter * 0.5F);
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		GlStateManager.glLineWidth(20.0F);
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		final double d1 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		for (TaskChunk task : TaskManager.getInstance().getTasks()) {
			for (ChunkPos cp : task.getPendingChunks()) {
				int dx = Math.abs(cp.x - playerChunk.x);
				int dz = Math.abs(cp.z - playerChunk.z);
				boolean inRange = (dx <= 2 && dz <= 2);
				int xStart = cp.getXStart();
				int zStart = cp.getZStart();
				int xEnd = cp.getXEnd() + 1;
				int zEnd = cp.getZEnd() + 1;
				double yStart = 0;
				double yEnd = mc.world.getHeight();
				AxisAlignedBB bb = new AxisAlignedBB(xStart, yStart, zStart, xEnd, yEnd, zEnd);
				AxisAlignedBB offsetBB = bb.offset(-d1, -d2, -d3);
				if (TaskManager.getInstance().getCurrentlyProcessingChunks().contains(cp)) {
					renderGlowingOutline(offsetBB, 1F, 1F, 0F, pulse, 0.25F);
				} else if (inRange) {
					renderGlowingOutline(offsetBB, 0F, 1F, 0F, pulse, 0.5F);
				} else {
					renderGlowingOutline(offsetBB, 1F, 0F, 0F, pulse, 0.5F);
				}
			}
		}
		GlStateManager.enableDepth();
		GlStateManager.glLineWidth(1F);
		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableFog();
		GlStateManager.popMatrix();
	}

	private static void renderGlowingOutline(AxisAlignedBB box, float r, float g, float b,
		float pulse, float thinLineAlpha) {
		Tessellator tess = Tessellator.getInstance();
		BufferBuilder buffer = tess.getBuffer();
		GlStateManager.pushMatrix();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(box.minX, box.minY, box.minZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.minX, box.minY, box.minZ).color(r, g, b, pulse).endVertex();
		tess.draw();
		buffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(box.minX, box.maxY, box.minZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.maxX, box.maxY, box.minZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.maxX, box.maxY, box.maxZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.minX, box.maxY, box.maxZ).color(r, g, b, pulse).endVertex();
		buffer.pos(box.minX, box.maxY, box.minZ).color(r, g, b, pulse).endVertex();
		tess.draw();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
		buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(box.minX, box.minY, box.minZ).color(r, g, b, thinLineAlpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.minZ).color(r, g, b, thinLineAlpha).endVertex();
		buffer.pos(box.maxX, box.minY, box.maxZ).color(r, g, b, thinLineAlpha).endVertex();
		buffer.pos(box.minX, box.minY, box.maxZ).color(r, g, b, thinLineAlpha).endVertex();
		tess.draw();
		GlStateManager.popMatrix();
	}
}