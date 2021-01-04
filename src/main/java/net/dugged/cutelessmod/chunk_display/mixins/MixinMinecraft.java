package net.dugged.cutelessmod.chunk_display.mixins;

import net.dugged.cutelessmod.chunk_display.gui.GuiChunkGrid;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
	@Inject(method = "updateFramebufferSize()V", at = @At("HEAD"))
	private void onResize(CallbackInfo ci) {
		if (GuiChunkGrid.instance != null && GuiChunkGrid.instance.getMinimapType() != 0) {
			GuiChunkGrid.instance.getController().updateMinimap();
		}
	}
}
