package net.dugged.cutelessmod.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GuiIngameForge.class, remap = false)
public abstract class MixinGuiIngameForge extends GuiIngame {
	@Shadow
	private FontRenderer fontrenderer;

	public MixinGuiIngameForge(final Minecraft mcIn) {
		super(mcIn);
	}

	/**
	 * @author forge team, nessie
	 * @reason forge version has weird bugs
	 */
	@Overwrite
	protected void renderRecordOverlay(final int width, final int height, final float partialTicks) {
		if (this.overlayMessageTime > 0) {
			this.mc.profiler.startSection("overlayMessage");
			final float hue = (float) this.overlayMessageTime - partialTicks;
			final int opacity = (int) Math.min(hue * 255F / 20F, 255);
			if (opacity > 8) {
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) (width / 2), (float) (height - 68), 0F);
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				final int colour = this.animateOverlayMessageColor ? MathHelper.hsvToRGB(hue / 50F, 0.7F, 0.6F) & 0xFFFFFF : 0xFFFFFF;
				this.fontrenderer.drawString(this.overlayMessage, -this.fontrenderer.getStringWidth(this.overlayMessage) / 2, -4, colour + (opacity << 24 & 0xFF000000));
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			mc.profiler.endSection();
		}
	}
}
