package net.dugged.cutelessmod.mixins;

import net.dugged.cutelessmod.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityEndGatewayRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityEndPortalRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.FloatBuffer;
import java.util.Random;

@Mixin(TileEntityEndPortalRenderer.class)
public abstract class MixinTileEntityEndPortalRenderer extends TileEntitySpecialRenderer<TileEntityEndPortal> {
	@Shadow
	@Final
	private static Random RANDOM;

	@Shadow
	@Final
	private static FloatBuffer MODELVIEW;

	@Shadow
	@Final
	private static FloatBuffer PROJECTION;

	@Shadow
	protected abstract int getPasses(final double p_191286_1_);

	@Shadow
	protected abstract float getOffset();

	@Shadow
	@Final
	private static ResourceLocation END_SKY_TEXTURE;

	@Shadow
	@Final
	private static ResourceLocation END_PORTAL_TEXTURE;

	@Shadow
	protected abstract FloatBuffer getBuffer(final float p_147525_1_, final float p_147525_2_, final float p_147525_3_, final float p_147525_4_);

	@Inject(method = "render(Lnet/minecraft/tileentity/TileEntityEndPortal;DDDFIF)V", at = @At("HEAD"), cancellable = true)
	public void cutelessmod$render(final TileEntityEndPortal te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha, final CallbackInfo ci) {
		//noinspection ConstantValue
		if (!Configuration.showMoreEndPortalFaces || (TileEntityEndPortalRenderer) (Object) this instanceof TileEntityEndGatewayRenderer) {
			return;
		}
		
		ci.cancel();
		GlStateManager.disableLighting();
		RANDOM.setSeed(31100L);
		GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, MODELVIEW);
		GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, PROJECTION);
		final double d0 = x * x + y * y + z * z;
		final int numPasses = this.getPasses(d0);
		final double offset = this.getOffset();
		boolean shouldRenderFog = false;

		for (int pass = 0; pass < numPasses; ++pass) {
			GlStateManager.pushMatrix();
			float f1 = 2F / (float) (18 - pass);

			if (pass == 0) {
				this.bindTexture(END_SKY_TEXTURE);
				f1 = 0.15F;
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			}

			if (pass >= 1) {
				this.bindTexture(END_PORTAL_TEXTURE);
				shouldRenderFog = true;
				Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
			}

			if (pass == 1) {
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
			}

			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_LINEAR);
			GlStateManager.texGen(GlStateManager.TexGen.S, GL11.GL_EYE_PLANE, this.getBuffer(1F, 0F, 0F, 0F));
			GlStateManager.texGen(GlStateManager.TexGen.T, GL11.GL_EYE_PLANE, this.getBuffer(0F, 1F, 0F, 0F));
			GlStateManager.texGen(GlStateManager.TexGen.R, GL11.GL_EYE_PLANE, this.getBuffer(0F, 0F, 1F, 0F));
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
			GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.pushMatrix();
			GlStateManager.loadIdentity();
			GlStateManager.translate(0.5F, 0.5F, 0F);
			GlStateManager.scale(0.5F, 0.5F, 1F);
			final float next = (float) (pass + 1);
			GlStateManager.translate(17F / next, (2F + next / 1.5F) * ((float) Minecraft.getSystemTime() % 800000F / 800000F), 0F);
			GlStateManager.rotate((next * next * 4321F + next * 9F) * 2F, 0F, 0F, 1F);
			GlStateManager.scale(4.5F - next / 4F, 4.5F - next / 4F, 1F);
			GlStateManager.multMatrix(PROJECTION);
			GlStateManager.multMatrix(MODELVIEW);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			final float dx = (RANDOM.nextFloat() * 0.5F + 0.1F) * f1;
			final float dy = (RANDOM.nextFloat() * 0.5F + 0.4F) * f1;
			final float dz = (RANDOM.nextFloat() * 0.5F + 0.5F) * f1;

			if (te.shouldRenderFace(EnumFacing.SOUTH)) {
				bufferbuilder.pos(x, y, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y + offset, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y + offset, z + 1D).color(dx, dy, dz, 1F).endVertex();
			}

			if (te.shouldRenderFace(EnumFacing.NORTH)) {
				bufferbuilder.pos(x, y + offset, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y + offset, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y, z).color(dx, dy, dz, 1F).endVertex();
			}

			if (te.shouldRenderFace(EnumFacing.EAST)) {
				bufferbuilder.pos(x + 1D, y + offset, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y + offset, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y, z).color(dx, dy, dz, 1F).endVertex();
			}

			if (te.shouldRenderFace(EnumFacing.WEST)) {
				bufferbuilder.pos(x, y, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y + offset, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y + offset, z).color(dx, dy, dz, 1F).endVertex();
			}

			if (te.shouldRenderFace(EnumFacing.DOWN)) {
				bufferbuilder.pos(x, y, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y, z + 1D).color(dx, dy, dz, 1F).endVertex();
			}

			if (te.shouldRenderFace(EnumFacing.UP)) {
				bufferbuilder.pos(x, y + offset, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y + offset, z + 1D).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x + 1D, y + offset, z).color(dx, dy, dz, 1F).endVertex();
				bufferbuilder.pos(x, y + offset, z).color(dx, dy, dz, 1F).endVertex();
			}

			tessellator.draw();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			this.bindTexture(END_SKY_TEXTURE);
		}

		GlStateManager.disableBlend();
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
		GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
		GlStateManager.enableLighting();

		if (shouldRenderFog) {
			Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
		}
	}
}
