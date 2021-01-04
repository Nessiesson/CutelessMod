package net.dugged.cutelessmod.chunk_display.mixins;

import net.dugged.cutelessmod.chunk_display.gui.GuiChunkGrid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {
	@Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
	private void onPostRenderHUD(float partialTicks, long nanoTime, CallbackInfo ci) {
		if (GuiChunkGrid.instance.getMinimapType() != 0) {
			Minecraft mc = Minecraft.getMinecraft();
			GuiChunkGrid.instance.renderMinimap(mc.displayWidth, mc.displayHeight);
		}
	}
}
