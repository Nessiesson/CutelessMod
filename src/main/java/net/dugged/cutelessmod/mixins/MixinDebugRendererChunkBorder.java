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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRendererChunkBorder.class)
public abstract class MixinDebugRendererChunkBorder {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void onRender(final float partialTicks, final long finishTimeNano, CallbackInfo ci) {
		ci.cancel();
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

		for (int x = -16; x <= 32; x += 16) {
			for (int z = -16; z <= 32; z += 16) {
				bufferbuilder.pos(d5 + x, d3, d6 + z).color(1F, 0F, 0F, 0F).endVertex();
				bufferbuilder.pos(d5 + x, d3, d6 + z).color(1F, 0F, 0F, 0.5F).endVertex();
				bufferbuilder.pos(d5 + x, d4, d6 + z).color(1F, 0F, 0F, 0.5F).endVertex();
				bufferbuilder.pos(d5 + x, d4, d6 + z).color(1F, 0F, 0F, 0F).endVertex();
			}
		}

		for (int x = 2; x < 16; x += 2) {
			bufferbuilder.pos(d5 + x, d3, d6).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + x, d3, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + x, d4, d6).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + x, d4, d6).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + x, d3, d6 + 16D).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + x, d3, d6 + 16D).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + x, d4, d6 + 16D).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + x, d4, d6 + 16D).color(1F, 1F, 0F, 0F).endVertex();
		}

		for (int z = 2; z < 16; z += 2) {
			bufferbuilder.pos(d5, d3, d6 + z).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5, d3, d6 + z).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d4, d6 + z).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5, d4, d6 + z).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + 16D, d3, d6 + z).color(1F, 1F, 0F, 0F).endVertex();
			bufferbuilder.pos(d5 + 16D, d3, d6 + z).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d4, d6 + z).color(1F, 1F, 0F, 1F).endVertex();
			bufferbuilder.pos(d5 + 16D, d4, d6 + z).color(1F, 1F, 0F, 0F).endVertex();
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
