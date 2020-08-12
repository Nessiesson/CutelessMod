package net.dugged.cutelessmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.debug.DebugRendererChunkBorder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DebugRendererChunkBorder.class)
public abstract class MixinDebugRendererChunkBorder {
	/**
	 * @author nessie
	 * @reason sadly the easiest way to achieve what i want.
	 */
	@Overwrite
	public void render(final float partialTicks, final long finishTimeNano) {
		final Minecraft mc = Minecraft.getMinecraft();
		Entity player = mc.player;
		if (((EntityPlayerSP) player).isSpectator()) {
			player = mc.getRenderViewEntity();
		}

		if (player == null) {
			return;
		}

		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder bufferbuilder = tessellator.getBuffer();
		final double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
		final double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
		final double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
		final double d3 = -d1;
		final double d4 = 256D - d1;
		GlStateManager.disableTexture2D();
		GlStateManager.disableBlend();
		final double d5 = (player.chunkCoordX << 4) - d0;
		final double d6 = (player.chunkCoordZ << 4) - d2;
		GlStateManager.glLineWidth(1F);
		bufferbuilder.begin(GL11.GL_CURRENT_BIT | GL11.GL_POINT_BIT, DefaultVertexFormats.POSITION_COLOR);

		for (int i = -16; i <= 32; i += 16) {
			for (int j = -16; j <= 32; j += 16) {
				bufferbuilder.pos(d5 + i, d3, d6 + j).color(1F, 0F, 0F, 0F).endVertex();
				bufferbuilder.pos(d5 + i, d3, d6 + j).color(1F, 0F, 0F, 0.5F).endVertex();
				bufferbuilder.pos(d5 + i, d4, d6 + j).color(1F, 0F, 0F, 0.5F).endVertex();
				bufferbuilder.pos(d5 + i, d4, d6 + j).color(1F, 0F, 0F, 0F).endVertex();
			}
		}

		for (int k = 2; k < 16; k += 2) {
			bufferbuilder.pos(d5 + k, d3, d6).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + k, d3, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + k, d4, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + k, d4, d6).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + k, d3, d6 + 16D).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + k, d3, d6 + 16D).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + k, d4, d6 + 16D).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + k, d4, d6 + 16D).color(1F, 1F, 0F, 0F).endVertex();
		}

		for (int l = 2; l < 16; l += 2) {
			bufferbuilder.pos(d5, d3, d6 + l).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5, d3, d6 + l).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d4, d6 + l).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d4, d6 + l).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + 16D, d3, d6 + l).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + 16D, d3, d6 + l).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d4, d6 + l).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d4, d6 + l).color(1F, 1F, 0F, 0F).endVertex();
		}

		for (int i1 = 0; i1 <= 256; i1 += 2) {
			final double d7 = i1 - d1;
			bufferbuilder.pos(d5, d7, d6).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5, d7, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d7, d6 + 16D).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d7, d6 + 16D).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d7, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d7, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d7, d6).color(1F, 1F, 0F, 0F).endVertex();
		}

		tessellator.draw();
		GlStateManager.glLineWidth(2F);
		bufferbuilder.begin(GL11.GL_CURRENT_BIT | GL11.GL_POINT_BIT, DefaultVertexFormats.POSITION_COLOR);

		for (int j1 = 0; j1 <= 16; j1 += 16) {
			for (int l1 = 0; l1 <= 16; l1 += 16) {
				bufferbuilder.pos(d5 + j1, d3, d6 + l1).color(0.25F, 0.25F, 1F, 0F).endVertex();
				bufferbuilder.pos(d5 + j1, d3, d6 + l1).color(0.25F, 0.25F, 1F, 1F).endVertex();
				bufferbuilder.pos(d5 + j1, d4, d6 + l1).color(0.25F, 0.25F, 1F, 1F).endVertex();
				bufferbuilder.pos(d5 + j1, d4, d6 + l1).color(0.25F, 0.25F, 1F, 0F).endVertex();
			}
		}

		for (int k1 = 0; k1 <= 256; k1 += 16) {
			final double d8 = k1 - d1;
			bufferbuilder.pos(d5, d8, d6).color(0.25F, 0.25F, 1F, 0F).endVertex();
			bufferbuilder.pos(d5, d8, d6).color(0.25F, 0.25F, 1F, 1F).endVertex();
			bufferbuilder.pos(d5, d8, d6 + 16D).color(0.25F, 0.25F, 1F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d8, d6 + 16D).color(0.25F, 0.25F, 1F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d8, d6).color(0.25F, 0.25F, 1F, 1F).endVertex();
			bufferbuilder.pos(d5, d8, d6).color(0.25F, 0.25F, 1F, 1F).endVertex();
			bufferbuilder.pos(d5, d8, d6).color(0.25F, 0.25F, 1F, 0F).endVertex();
		}

		tessellator.draw();
		GlStateManager.glLineWidth(1F);
		GlStateManager.enableBlend();
		GlStateManager.enableTexture2D();
	}
}
