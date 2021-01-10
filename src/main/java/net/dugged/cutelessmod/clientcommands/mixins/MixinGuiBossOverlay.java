package net.dugged.cutelessmod.clientcommands.mixins;

import net.dugged.cutelessmod.clientcommands.ClientCommandHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public abstract class MixinGuiBossOverlay extends Gui {
	@Shadow
	@Final
	private static ResourceLocation GUI_BARS_TEXTURES;

	@Inject(method = "renderBossHealth", at = @At(value = "HEAD"))
	private void renderProgressBar(CallbackInfo ci) {
		final Minecraft mc = Minecraft.getMinecraft();
		final float progress = ClientCommandHandler.instance.getProgress();
		if (progress > 0) {
			ScaledResolution scaledresolution = new ScaledResolution(mc);
			int i = scaledresolution.getScaledWidth();
			int y = scaledresolution.getScaledHeight() - 30;
			int x = i / 2 - 91;
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			mc.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
			final int color = 2;
			drawTexturedModalRect(x, y, 0, color * 5 * 2, 182, 5);
			int j = (int) (progress * 183.0F);
			if (j > 0) {
				drawTexturedModalRect(x, y, 0, color * 5 * 2 + 5, j, 5);
			}
			String s = new TextComponentTranslation("text.cutelessmod.clientcommands.worldEdit.progressBar").getUnformattedText();
			mc.fontRenderer.drawString(s, i / 2 - mc.fontRenderer.getStringWidth(s) / 2, y - 10, 16777215);
		}
	}
}